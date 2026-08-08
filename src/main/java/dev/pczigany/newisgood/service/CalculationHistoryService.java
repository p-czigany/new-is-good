package dev.pczigany.newisgood.service;

import dev.pczigany.newisgood.api.dto.CalculationHistoryResponse;
import dev.pczigany.newisgood.persistence.CalculationRecord;
import dev.pczigany.newisgood.persistence.CalculationRecordRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Coordinates persistence of calculation history entries and
 * translates records back into API responses. No framework
 * annotations; dependencies are constructor-injected.
 */
public final class CalculationHistoryService {

    private final CalculationRecordRepository repository;
    private final ObjectMapper objectMapper;

    public CalculationHistoryService(
            CalculationRecordRepository repository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void save(
            String algorithm,
            List<Integer> input,
            List<Long> result,
            String comment
    ) {
        try {
            CalculationRecord calculation = new CalculationRecord(
                    algorithm,
                    objectMapper.writeValueAsString(input),
                    objectMapper.writeValueAsString(result),
                    comment
            );

            repository.save(calculation);
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "Failed to serialize calculation data",
                    exception
            );
        }
    }

    public List<CalculationHistoryResponse> findAll(String searchTerm) {
        List<CalculationRecord> records;

        if (searchTerm == null || searchTerm.isBlank()) {
            records = repository.findAllByOrderByCreatedAtDesc();
        } else {
            records = repository
                    .findByCommentContainingIgnoreCaseOrderByCreatedAtDesc(
                            searchTerm.trim()
                    );
        }

        return records.stream()
                .map(this::toResponse)
                .toList();
    }

    private CalculationHistoryResponse toResponse(
            CalculationRecord calculation
    ) {
        try {
            List<Integer> input = objectMapper.readValue(
                    calculation.getInputJson(),
                    new TypeReference<List<Integer>>() {
                    }
            );

            List<Long> result = objectMapper.readValue(
                    calculation.getResultJson(),
                    new TypeReference<List<Long>>() {
                    }
            );

            return new CalculationHistoryResponse(
                    calculation.getId(),
                    calculation.getAlgorithm(),
                    input,
                    result,
                    calculation.getComment(),
                    calculation.getCreatedAt()
            );
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "Failed to deserialize calculation data",
                    exception
            );
        }
    }
}

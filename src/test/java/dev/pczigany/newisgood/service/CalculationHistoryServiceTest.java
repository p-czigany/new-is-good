package dev.pczigany.newisgood.service;

import static org.assertj.core.api.Assertions.assertThat;

import dev.pczigany.newisgood.api.dto.CalculationHistoryResponse;
import dev.pczigany.newisgood.persistence.CalculationRecord;
import dev.pczigany.newisgood.persistence.FakeCalculationRecordRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

class CalculationHistoryServiceTest {

    private final FakeCalculationRecordRepository repository =
            new FakeCalculationRecordRepository();

    private final CalculationHistoryService historyService =
            new CalculationHistoryService(repository, new ObjectMapper());

    @Test
    void shouldPersistCalculationRequestAndResponse() {
        historyService.save(
                "C",
                List.of(1, 2, 3, 4),
                List.of(24L, 12L, 8L, 6L),
                "Linear solution"
        );

        List<CalculationRecord> saved = repository.saved();
        assertThat(saved).hasSize(1);

        CalculationRecord record = saved.getFirst();
        assertThat(record.getAlgorithm()).isEqualTo("C");
        assertThat(record.getInputJson()).isEqualTo("[1,2,3,4]");
        assertThat(record.getResultJson()).isEqualTo("[24,12,8,6]");
        assertThat(record.getComment()).isEqualTo("Linear solution");
    }

    @Test
    void shouldRetrieveAndDeserializeFilteredHistory() {
        repository.save(new CalculationRecord(
                1L,
                "C",
                "[1,2,3,4]",
                "[24,12,8,6]",
                "Manual calculation",
                Instant.now()
        ));
        repository.save(new CalculationRecord(
                2L,
                "A",
                "[5]",
                "[1]",
                "Something else",
                Instant.now()
        ));

        List<CalculationHistoryResponse> history =
                historyService.findAll(" manual ");

        assertThat(history).hasSize(1);

        CalculationHistoryResponse response = history.getFirst();
        assertThat(response.algorithm()).isEqualTo("C");
        assertThat(response.input()).containsExactly(1, 2, 3, 4);
        assertThat(response.result()).containsExactly(24L, 12L, 8L, 6L);
        assertThat(response.comment()).isEqualTo("Manual calculation");
    }

    @Test
    void shouldReturnAllWhenSearchTermIsBlank() {
        repository.save(new CalculationRecord(
                1L, "A", "[1]", "[1]", "one", Instant.now()
        ));
        repository.save(new CalculationRecord(
                2L, "B", "[2]", "[2]", "two", Instant.now()
        ));

        List<CalculationHistoryResponse> history =
                historyService.findAll("   ");

        assertThat(history).hasSize(2);
    }
}

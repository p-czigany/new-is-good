package dev.pczigany.newisgood.api.dto;

import java.time.Instant;
import java.util.List;

public record CalculationHistoryResponse(
        Long id,
        String algorithm,
        List<Integer> input,
        List<Long> result,
        String comment,
        Instant createdAt
) {
}

package dev.pczigany.newisgood.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CalculationRequest(
        @NotNull
        @Size(min = 1)
        List<@NotNull Integer> input,

        @Size(max = 500)
        String comment
) {
}

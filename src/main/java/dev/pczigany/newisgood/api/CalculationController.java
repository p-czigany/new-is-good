package dev.pczigany.newisgood.api;

import dev.pczigany.newisgood.api.dto.CalculationRequest;
import dev.pczigany.newisgood.api.dto.CalculationResponse;
import dev.pczigany.newisgood.service.CalculationHistoryService;
import dev.pczigany.newisgood.service.CalculationService;

import java.util.List;

/**
 * Framework-free application controller. Orchestrates the
 * calculation and history services and returns a response DTO.
 * Contains no HTTP-specific code; wrapped by a {@code Take} at the
 * HTTP boundary.
 */
public final class CalculationController {

    private final CalculationService calculationService;
    private final CalculationHistoryService historyService;

    public CalculationController(
            CalculationService calculationService,
            CalculationHistoryService historyService
    ) {
        this.calculationService = calculationService;
        this.historyService = historyService;
    }

    public CalculationResponse calculateA(CalculationRequest request) {
        List<Long> result = calculationService.calculateA(request.input());
        return saveAndRespond("A", request, result);
    }

    public CalculationResponse calculateB(CalculationRequest request) {
        List<Long> result = calculationService.calculateB(request.input());
        return saveAndRespond("B", request, result);
    }

    public CalculationResponse calculateC(CalculationRequest request) {
        List<Long> result = calculationService.calculateC(request.input());
        return saveAndRespond("C", request, result);
    }

    private CalculationResponse saveAndRespond(
            String algorithm,
            CalculationRequest request,
            List<Long> result
    ) {
        historyService.save(
                algorithm,
                request.input(),
                result,
                request.comment()
        );

        return new CalculationResponse(result);
    }
}

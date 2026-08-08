package dev.pczigany.newisgood.api;

import dev.pczigany.newisgood.api.dto.CalculationHistoryResponse;
import dev.pczigany.newisgood.service.CalculationHistoryService;

import java.util.List;

/**
 * Framework-free controller that returns the calculation history.
 * Contains no HTTP-specific code; wrapped by a {@code Take} at the
 * HTTP boundary.
 */
public final class HistoryController {

    private final CalculationHistoryService historyService;

    public HistoryController(CalculationHistoryService historyService) {
        this.historyService = historyService;
    }

    public List<CalculationHistoryResponse> getHistory(String searchTerm) {
        return historyService.findAll(searchTerm);
    }
}

package dev.pczigany.newisgood.api;

import dev.pczigany.newisgood.api.dto.CalculationHistoryResponse;
import tools.jackson.databind.ObjectMapper;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * HTTP adapter for the calculation history endpoint. Reads the
 * optional {@code searchTerm} query parameter and returns the
 * results as JSON.
 */
public final class TkHistory implements Take {

    private final HistoryController controller;
    private final ObjectMapper objectMapper;

    public TkHistory(
            HistoryController controller,
            ObjectMapper objectMapper
    ) {
        this.controller = controller;
        this.objectMapper = objectMapper;
    }

    @Override
    public Response act(Request req) throws IOException {
        String searchTerm = firstParam(req, "searchTerm");

        try {
            List<CalculationHistoryResponse> history =
                    controller.getHistory(searchTerm);
            byte[] payload = objectMapper.writeValueAsBytes(history);
            return new RsWithType(
                    new RsWithBody(new RsWithStatus(200), payload),
                    "application/json"
            );
        } catch (RuntimeException exception) {
            byte[] payload = objectMapper.writeValueAsBytes(
                    new ErrorBody(500, exception.getMessage())
            );
            return new RsWithType(
                    new RsWithBody(new RsWithStatus(500), payload),
                    "application/json"
            );
        }
    }

    private String firstParam(Request req, String name) throws IOException {
        Iterator<String> values = new RqHref.Base(req).href()
                .param(name)
                .iterator();
        return values.hasNext() ? values.next() : null;
    }

    private record ErrorBody(int status, String message) {
    }
}

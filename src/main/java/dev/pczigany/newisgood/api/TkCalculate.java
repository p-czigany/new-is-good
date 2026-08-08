package dev.pczigany.newisgood.api;

import dev.pczigany.newisgood.api.dto.CalculationRequest;
import dev.pczigany.newisgood.api.dto.CalculationResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqPrint;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * HTTP adapter for a single calculation endpoint. Deserializes the
 * JSON body, validates the request, delegates to
 * {@link CalculationController}, and writes back a JSON response.
 *
 * The specific algorithm (A/B/C) is chosen at construction time via
 * a method reference into the controller, keeping this class
 * agnostic of which endpoint it serves.
 */
public final class TkCalculate implements Take {

    private final CalculationController controller;
    private final Function<CalculationRequest, CalculationResponse> handler;
    private final ObjectMapper objectMapper;
    private final ValidationSupport validationSupport;

    public TkCalculate(
            CalculationController controller,
            Function<CalculationRequest, CalculationResponse> handler,
            ObjectMapper objectMapper,
            ValidationSupport validationSupport
    ) {
        this.controller = controller;
        this.handler = handler;
        this.objectMapper = objectMapper;
        this.validationSupport = validationSupport;
    }

    @Override
    public Response act(Request req) throws IOException {
        String body = new RqPrint(req).printBody();

        CalculationRequest request;
        try {
            request = objectMapper.readValue(body, CalculationRequest.class);
        } catch (JacksonException exception) {
            return jsonError(400, "Malformed JSON: " + exception.getMessage());
        }

        List<String> violations = validationSupport.validate(request);
        if (!violations.isEmpty()) {
            return jsonError(400, "Validation failed", violations);
        }

        try {
            CalculationResponse response = handler.apply(request);
            byte[] payload = objectMapper.writeValueAsBytes(response);
            return new RsWithType(
                    new RsWithBody(new RsWithStatus(200), payload),
                    "application/json"
            );
        } catch (RuntimeException exception) {
            return jsonError(500, exception.getMessage());
        }
    }

    /**
     * Exposes the underlying controller for tests that want to
     * inspect the collaborator wiring.
     */
    public CalculationController controller() {
        return controller;
    }

    private Response jsonError(int status, String message)
            throws IOException {
        return jsonError(status, message, List.of());
    }

    private Response jsonError(
            int status,
            String message,
            List<String> details
    ) throws IOException {
        ErrorBody payload = new ErrorBody(status, message, details);
        byte[] json = objectMapper.writeValueAsBytes(payload);
        return new RsWithType(
                new RsWithBody(new RsWithStatus(status), json),
                "application/json"
        );
    }

    private record ErrorBody(
            int status,
            String message,
            List<String> details
    ) {
    }
}

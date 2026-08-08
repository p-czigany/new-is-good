package dev.pczigany.newisgood.api;

import static org.assertj.core.api.Assertions.assertThat;

import dev.pczigany.newisgood.api.dto.CalculationRequest;
import dev.pczigany.newisgood.api.dto.CalculationResponse;
import dev.pczigany.newisgood.persistence.FakeCalculationRecordRepository;
import dev.pczigany.newisgood.service.CalculationHistoryService;
import dev.pczigany.newisgood.service.CalculationService;
import tools.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.takes.Response;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

class TkCalculateTest {

    private static Validator validator;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CalculationService calculationService =
            new CalculationService();
    private final FakeCalculationRecordRepository repository =
            new FakeCalculationRecordRepository();
    private final CalculationHistoryService historyService =
            new CalculationHistoryService(repository, objectMapper);
    private final CalculationController controller =
            new CalculationController(calculationService, historyService);

    @BeforeAll
    static void initValidator() {
        try (ValidatorFactory factory =
                     Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldCalculateSolutionA() throws IOException {
        Response response = invoke(
                controller::calculateA,
                "{\"input\":[1,2,3,4],\"comment\":\"Example\"}"
        );

        assertThat(head(response)).contains("HTTP/1.1 200");
        assertThat(body(response))
                .contains("\"result\":[24,12,8,6]");
        assertThat(repository.saved()).hasSize(1);
        assertThat(repository.saved().getFirst().getAlgorithm())
                .isEqualTo("A");
    }

    @Test
    void shouldCalculateSolutionB() throws IOException {
        Response response = invoke(
                controller::calculateB,
                "{\"input\":[1,2,3,4],\"comment\":\"Solution B\"}"
        );

        assertThat(head(response)).contains("HTTP/1.1 200");
        assertThat(body(response))
                .contains("\"result\":[24,12,8,6]");
    }

    @Test
    void shouldCalculateSolutionC() throws IOException {
        Response response = invoke(
                controller::calculateC,
                "{\"input\":[1,2,3,4],\"comment\":\"Solution C\"}"
        );

        assertThat(head(response)).contains("HTTP/1.1 200");
        assertThat(body(response))
                .contains("\"result\":[24,12,8,6]");
    }

    @Test
    void shouldRejectEmptyInput() throws IOException {
        Response response = invoke(
                controller::calculateA,
                "{\"input\":[],\"comment\":\"Invalid\"}"
        );

        assertThat(head(response)).contains("HTTP/1.1 400");
        assertThat(body(response)).contains("Validation failed");
        assertThat(repository.saved()).isEmpty();
    }

    @Test
    void shouldRejectMalformedJson() throws IOException {
        Response response = invoke(
                controller::calculateA,
                "{ not json"
        );

        assertThat(head(response)).contains("HTTP/1.1 400");
        assertThat(body(response)).contains("Malformed JSON");
    }

    private Response invoke(
            Function<CalculationRequest, CalculationResponse> handler,
            String body
    ) throws IOException {
        TkCalculate take = new TkCalculate(
                controller,
                handler,
                objectMapper,
                new JakartaValidationSupport(validator)
        );
        return take.act(new RqFake(
                List.of(
                        "POST /api/calculate/a HTTP/1.1",
                        "Host: localhost",
                        "Content-Type: application/json"
                ),
                body
        ));
    }

    private String head(Response response) throws IOException {
        return new RsPrint(response).printHead();
    }

    private String body(Response response) throws IOException {
        return new RsPrint(response).printBody();
    }
}

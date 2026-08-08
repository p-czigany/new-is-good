package dev.pczigany.newisgood.api;

import static org.assertj.core.api.Assertions.assertThat;

import dev.pczigany.newisgood.persistence.CalculationRecord;
import dev.pczigany.newisgood.persistence.FakeCalculationRecordRepository;
import dev.pczigany.newisgood.service.CalculationHistoryService;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.takes.Response;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

class TkHistoryTest {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .build();
//            .addModule(new JavaTimeModule()).build();
    private final FakeCalculationRecordRepository repository =
            new FakeCalculationRecordRepository();
    private final CalculationHistoryService historyService =
            new CalculationHistoryService(repository, objectMapper);
    private final HistoryController controller =
            new HistoryController(historyService);
    private final TkHistory take =
            new TkHistory(controller, objectMapper);

    @Test
    void shouldReturnAllHistoryWhenNoSearchTerm() throws IOException {
        repository.save(new CalculationRecord(
                1L, "A", "[1,2,3]", "[6,3,2]", "hello",
                Instant.now()
        ));

        Response response = take.act(new RqFake(
                List.of(
                        "GET /api/history HTTP/1.1",
                        "Host: localhost"
                ),
                ""
        ));

        assertThat(head(response)).contains("HTTP/1.1 200");
        String body = body(response);
        assertThat(body).contains("\"algorithm\":\"A\"");
        assertThat(body).contains("\"input\":[1,2,3]");
        assertThat(body).contains("\"result\":[6,3,2]");
    }

    @Test
    void shouldFilterHistoryBySearchTerm() throws IOException {
        repository.save(new CalculationRecord(
                1L, "A", "[1]", "[1]", "keep me", Instant.now()
        ));
        repository.save(new CalculationRecord(
                2L, "B", "[2]", "[2]", "skip me", Instant.now()
        ));

        Response response = take.act(new RqFake(
                List.of(
                        "GET /api/history?searchTerm=keep HTTP/1.1",
                        "Host: localhost"
                ),
                ""
        ));

        assertThat(head(response)).contains("HTTP/1.1 200");
        String body = body(response);
        assertThat(body).contains("keep me");
        assertThat(body).doesNotContain("skip me");
    }

    private String head(Response response) throws IOException {
        return new RsPrint(response).printHead();
    }

    private String body(Response response) throws IOException {
        return new RsPrint(response).printBody();
    }
}

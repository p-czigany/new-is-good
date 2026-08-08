package dev.pczigany.newisgood;

import dev.pczigany.newisgood.api.CalculationController;
import dev.pczigany.newisgood.api.HistoryController;
import dev.pczigany.newisgood.api.JakartaValidationSupport;
import dev.pczigany.newisgood.api.TkCalculate;
import dev.pczigany.newisgood.api.TkHistory;
import dev.pczigany.newisgood.api.ValidationSupport;
import dev.pczigany.newisgood.persistence.JdbcCalculationRecordRepository;
import dev.pczigany.newisgood.service.CalculationHistoryService;
import dev.pczigany.newisgood.service.CalculationService;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.h2.jdbcx.JdbcDataSource;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.Exit;
import org.takes.http.FtBasic;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Application entry point. Constructs the entire object graph
 * manually with {@code new}; no Spring, no DI container, no
 * classpath scanning.
 */
public final class App {

    private static final int PORT = 3000;

    private App() {
        // Utility class, no instances.
    }

    public static void main(String[] args) throws IOException {
        final ObjectMapper objectMapper = JsonMapper.builder().build();
        final ValidationSupport validationSupport =
                new JakartaValidationSupport(buildValidator());
        final CalculationHistoryService historyService =
                new CalculationHistoryService(
                        new JdbcCalculationRecordRepository(h2DataSource()),
                        objectMapper
                );
        final CalculationController calculationController =
                new CalculationController(
                        new CalculationService(),
                        historyService
                );
        new FtBasic(
                new TkFork(
                        new FkRegex(
                                "/api/calculate/a",
                                new TkCalculate(
                                        calculationController,
                                        calculationController::calculateA,
                                        objectMapper,
                                        validationSupport
                                )
                        ),
                        new FkRegex(
                                "/api/calculate/b",
                                new TkCalculate(
                                        calculationController,
                                        calculationController::calculateB,
                                        objectMapper,
                                        validationSupport
                                )
                        ),
                        new FkRegex(
                                "/api/calculate/c",
                                new TkCalculate(
                                        calculationController,
                                        calculationController::calculateC,
                                        objectMapper,
                                        validationSupport
                                )
                        ),
                        new FkRegex(
                                "/api/history",
                                new TkHistory(
                                        new HistoryController(historyService),
                                        objectMapper
                                )
                        )
                ),
                PORT
        ).start(Exit.NEVER);
    }

    private static DataSource h2DataSource() {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:appdb;DB_CLOSE_DELAY=-1");
        ds.setUser("sa");
        ds.setPassword("");
        return ds;
    }

    private static Validator buildValidator() {
        try (final ValidatorFactory factory
                     = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator();
        }
    }
}

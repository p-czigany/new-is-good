package dev.pczigany.newisgood.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

class CalculationRecordRepositoryTest {

    private JdbcCalculationRecordRepository repository;

    @BeforeEach
    void setUp() {
        // Fresh in-memory DB per test to guarantee isolation.
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(
                "jdbc:h2:mem:test-" + UUID.randomUUID()
                        + ";DB_CLOSE_DELAY=-1"
        );
        ds.setUser("sa");
        ds.setPassword("");
        repository = new JdbcCalculationRecordRepository(ds);
    }

    @Test
    void shouldFilterHistoryByCommentIgnoringCase() {
        repository.save(new CalculationRecord(
                "A",
                "[1,2,3,4]",
                "[24,12,8,6]",
                "Manual calculation"
        ));

        repository.save(new CalculationRecord(
                "C",
                "[2,3,4]",
                "[12,8,6]",
                "Performance example"
        ));

        List<CalculationRecord> result = repository
                .findByCommentContainingIgnoreCaseOrderByCreatedAtDesc(
                        "MANUAL"
                );

        assertThat(result)
                .extracting(CalculationRecord::getComment)
                .containsExactly("Manual calculation");
    }

    @Test
    void shouldReturnAllRecordsInInsertionOrder() {
        repository.save(new CalculationRecord(
                "A", "[1]", "[1]", "first"
        ));
        repository.save(new CalculationRecord(
                "B", "[2]", "[2]", "second"
        ));

        List<CalculationRecord> result =
                repository.findAllByOrderByCreatedAtDesc();

        assertThat(result)
                .extracting(CalculationRecord::getAlgorithm)
                .containsExactlyInAnyOrder("A", "B");
        assertThat(result).allSatisfy(r ->
                assertThat(r.getId()).isNotNull()
        );
        assertThat(result).allSatisfy(r ->
                assertThat(r.getCreatedAt()).isNotNull()
        );
    }
}

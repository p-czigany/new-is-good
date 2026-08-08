package dev.pczigany.newisgood.persistence;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Plain-JDBC implementation of the calculation record repository.
 * Each public method opens its own {@link Connection} from the
 * supplied {@link DataSource}, relies on JDBC auto-commit for
 * atomicity of the single-row insert, and cleanly closes resources
 * via try-with-resources.
 */
public final class JdbcCalculationRecordRepository
        implements CalculationRecordRepository {

    private static final String SCHEMA_RESOURCE = "/schema.sql";

    private static final String INSERT_SQL =
            "INSERT INTO calculation_history "
                    + "(algorithm, input_json, result_json, "
                    + "comment, created_at) "
                    + "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_ALL_SQL =
            "SELECT id, algorithm, input_json, result_json, "
                    + "comment, created_at "
                    + "FROM calculation_history "
                    + "ORDER BY created_at DESC, id DESC";

    private static final String SELECT_BY_COMMENT_SQL =
            "SELECT id, algorithm, input_json, result_json, "
                    + "comment, created_at "
                    + "FROM calculation_history "
                    + "WHERE LOWER(comment) LIKE ? "
                    + "ORDER BY created_at DESC, id DESC";

    private final DataSource dataSource;

    /**
     * Builds the repository against the given data source and
     * eagerly ensures the underlying schema exists.
     */
    public JdbcCalculationRecordRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        initSchema();
    }

    @Override
    public void save(CalculationRecord record) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {
            stmt.setString(1, record.getAlgorithm());
            stmt.setString(2, record.getInputJson());
            stmt.setString(3, record.getResultJson());
            stmt.setString(4, record.getComment());
            stmt.setTimestamp(
                    5,
                    Timestamp.from(record.getCreatedAt())
            );
            stmt.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to save calculation record",
                    exception
            );
        }
    }

    @Override
    public List<CalculationRecord> findAllByOrderByCreatedAtDesc() {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = stmt.executeQuery()) {
            return mapResultSet(rs);
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to load calculation records",
                    exception
            );
        }
    }

    @Override
    public List<CalculationRecord>
    findByCommentContainingIgnoreCaseOrderByCreatedAtDesc(
            String searchTerm
    ) {
        String pattern = "%" + searchTerm.toLowerCase() + "%";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(SELECT_BY_COMMENT_SQL)) {
            stmt.setString(1, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapResultSet(rs);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to search calculation records",
                    exception
            );
        }
    }

    private List<CalculationRecord> mapResultSet(ResultSet rs)
            throws SQLException {
        List<CalculationRecord> records = new ArrayList<>();
        while (rs.next()) {
            Timestamp createdAt = rs.getTimestamp("created_at");
            records.add(new CalculationRecord(
                    rs.getLong("id"),
                    rs.getString("algorithm"),
                    rs.getString("input_json"),
                    rs.getString("result_json"),
                    rs.getString("comment"),
                    createdAt == null ? null : createdAt.toInstant()
            ));
        }
        return records;
    }

    private void initSchema() {
        String ddl = readSchema();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(ddl);
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to initialise schema",
                    exception
            );
        }
    }

    private String readSchema() {
        try (InputStream in = JdbcCalculationRecordRepository.class
                .getResourceAsStream(SCHEMA_RESOURCE)) {
            if (in == null) {
                throw new IllegalStateException(
                        "Schema resource not found: " + SCHEMA_RESOURCE
                );
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to read schema resource",
                    exception
            );
        }
    }
}

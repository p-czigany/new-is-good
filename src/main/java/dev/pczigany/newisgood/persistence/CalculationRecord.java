package dev.pczigany.newisgood.persistence;

import java.time.Instant;

/**
 * Plain data-carrier for a persisted calculation history entry.
 * Contains no framework annotations; used by hand-written JDBC
 * repository code and by the history service.
 */
public final class CalculationRecord {

    private final Long id;
    private final String algorithm;
    private final String inputJson;
    private final String resultJson;
    private final String comment;
    private final Instant createdAt;

    /**
     * Constructor used by the service before insert; id is generated
     * by the database, createdAt is stamped now.
     */
    public CalculationRecord(
            String algorithm,
            String inputJson,
            String resultJson,
            String comment
    ) {
        this(null, algorithm, inputJson, resultJson, comment, Instant.now());
    }

    /**
     * Constructor used by the repository after a row is read back
     * from the database.
     */
    public CalculationRecord(
            Long id,
            String algorithm,
            String inputJson,
            String resultJson,
            String comment,
            Instant createdAt
    ) {
        this.id = id;
        this.algorithm = algorithm;
        this.inputJson = inputJson;
        this.resultJson = resultJson;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getInputJson() {
        return inputJson;
    }

    public String getResultJson() {
        return resultJson;
    }

    public String getComment() {
        return comment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

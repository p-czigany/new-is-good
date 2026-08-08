package dev.pczigany.newisgood.persistence;

import java.util.List;

/**
 * Persistence gateway for calculation history records. Deliberately
 * framework-free: no Spring Data, no annotations, no default methods.
 */
public interface CalculationRecordRepository {

    /**
     * Persists the given record. Implementations may either use their
     * own connection or participate in an ambient transaction.
     */
    void save(CalculationRecord record);

    /**
     * Returns all persisted records, newest first.
     */
    List<CalculationRecord> findAllByOrderByCreatedAtDesc();

    /**
     * Returns records whose comment contains the given search term
     * (case-insensitive), newest first.
     */
    List<CalculationRecord>
    findByCommentContainingIgnoreCaseOrderByCreatedAtDesc(String searchTerm);
}

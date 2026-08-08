package dev.pczigany.newisgood.persistence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * In-memory test double for {@link CalculationRecordRepository}.
 * Simple, deterministic, and free of framework dependencies so it
 * can replace Mockito in unit tests.
 */
public final class FakeCalculationRecordRepository
        implements CalculationRecordRepository {

    private final List<CalculationRecord> records = new ArrayList<>();

    @Override
    public void save(CalculationRecord record) {
        records.add(record);
    }

    @Override
    public List<CalculationRecord> findAllByOrderByCreatedAtDesc() {
        return records.stream()
                .sorted(Comparator
                        .comparing(CalculationRecord::getCreatedAt)
                        .reversed())
                .toList();
    }

    @Override
    public List<CalculationRecord>
    findByCommentContainingIgnoreCaseOrderByCreatedAtDesc(
            String searchTerm
    ) {
        String needle = searchTerm.toLowerCase(Locale.ROOT);
        return records.stream()
                .filter(r -> r.getComment() != null
                        && r.getComment()
                                .toLowerCase(Locale.ROOT)
                                .contains(needle))
                .sorted(Comparator
                        .comparing(CalculationRecord::getCreatedAt)
                        .reversed())
                .toList();
    }

    /**
     * Test-only accessor exposing the raw list in insertion order.
     */
    public List<CalculationRecord> saved() {
        return List.copyOf(records);
    }
}

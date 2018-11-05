package cz.cvut.kbss.benchmark.util;

import cz.cvut.kbss.benchmark.model.OccurrenceReport;

/**
 * Updates data using the provided instance.
 * <p>
 * The provided instance should not be managed by the persistence context.
 *
 * @param <R> Concrete implementation of {@link OccurrenceReport}
 */
public interface Updater<R extends OccurrenceReport> extends Transactional {

    void update(R report);
}

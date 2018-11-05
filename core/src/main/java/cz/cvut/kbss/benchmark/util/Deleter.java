package cz.cvut.kbss.benchmark.util;

import cz.cvut.kbss.benchmark.model.OccurrenceReport;

/**
 * Deletes the specified report.
 *
 * @param <R> Concrete implementation of {@link OccurrenceReport}
 */
public interface Deleter<R extends OccurrenceReport> extends Transactional {

    void delete(R report);
}

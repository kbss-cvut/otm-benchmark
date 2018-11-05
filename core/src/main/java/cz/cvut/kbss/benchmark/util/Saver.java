package cz.cvut.kbss.benchmark.util;

import cz.cvut.kbss.benchmark.model.OccurrenceReport;
import cz.cvut.kbss.benchmark.model.Person;

import java.util.Collection;

/**
 * Saves data into repository.
 *
 * @param <P> Concrete implementation of {@link Person}
 * @param <R> Concrete implementation of {@link OccurrenceReport}
 */
public interface Saver<P extends Person, R extends OccurrenceReport> extends Transactional {

    void persistAll(Collection<P> persons);

    void persist(R report);
}

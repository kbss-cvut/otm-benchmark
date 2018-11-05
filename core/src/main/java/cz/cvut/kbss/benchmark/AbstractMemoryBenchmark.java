package cz.cvut.kbss.benchmark;

import cz.cvut.kbss.benchmark.data.DataGenerator;
import cz.cvut.kbss.benchmark.model.OccurrenceReport;
import cz.cvut.kbss.benchmark.model.Person;
import cz.cvut.kbss.benchmark.util.*;
import cz.cvut.kbss.benchmark.util.Constants;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Primary entry point for the memory benchmark application.
 * <p>
 * Defines the algorithm invoked during the benchmark (CRUD operations in a loop) which are executed in a loop for a configured
 * amount of time.
 * <p>
 * Subclasses need to provide implementation of {@link Saver}, {@link Finder}, {@link Updater} and {@link Deleter} factory methods.
 *
 * @param <P> Concrete implementation of {@link Person}
 * @param <R> Concrete implementation of {@link OccurrenceReport}
 */
public abstract class AbstractMemoryBenchmark<P extends Person, R extends OccurrenceReport> {

    private final DataGenerator<P, R> generator;

    protected AbstractMemoryBenchmark(DataGenerator<P, R> generator) {
        this.generator = generator;
    }

    protected void execute() {
        BenchmarkUtil.scheduleApplicationShutdown(Config.getRuntime().orElse(Constants.DEFAULT_MEMORY_RUNTIME));
        persistPersons();
        while (true) {
            persist();
            findAll();
            update();
            remove();
        }
    }

    private void persistPersons() {
        generator.generatePersons();
        final Saver<P, R> saver = getSaver();
        try {
            saver.begin();
            saver.persistAll(generator.getPersons());
            saver.commit();
        } finally {
            saver.close();
        }
    }

    protected abstract Saver<P, R> getSaver();

    private void persist() {
        generator.generateReports();
        final Saver<P, R> saver = getSaver();
        try {
            generator.getReports().forEach(r -> {
                saver.begin();
                saver.persist(r);
                saver.commit();
            });
        } finally {
            saver.close();
        }
    }

    private void findAll() {
        final Finder<R> finder = getFinder();
        try {
            final Collection<R> result = finder.findAll();
            assertEquals(generator.getReports().size(), result.size());
        } finally {
            finder.close();
        }
    }

    protected abstract Finder<R> getFinder();

    private void update() {
        final Updater<R> updater = getUpdater();
        try {
            generator.getReports().forEach(r -> {
                updater.begin();
                AbstractRunner.updateReport(r, generator);
                updater.update(r);
                updater.commit();
            });
        } finally {
            updater.close();
        }
    }

    protected abstract Updater<R> getUpdater();

    private void remove() {
        final Deleter<R> deleter = getDeleter();
        try {
            generator.getReports().forEach(r -> {
                deleter.begin();
                deleter.delete(r);
                deleter.commit();
            });
        } finally {
            deleter.close();
        }
    }

    protected abstract Deleter<R> getDeleter();
}

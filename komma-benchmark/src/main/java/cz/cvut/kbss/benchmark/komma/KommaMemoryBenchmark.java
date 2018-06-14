package cz.cvut.kbss.benchmark.komma;

import cz.cvut.kbss.benchmark.komma.model.Event;
import cz.cvut.kbss.benchmark.komma.model.OccurrenceReport;
import cz.cvut.kbss.benchmark.komma.util.KommaUpdater;
import cz.cvut.kbss.benchmark.util.BenchmarkUtil;
import cz.cvut.kbss.benchmark.util.Config;
import cz.cvut.kbss.benchmark.util.Constants;
import net.enilink.komma.core.IEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class KommaMemoryBenchmark {

    private static final Logger LOG = LoggerFactory.getLogger(KommaMemoryBenchmark.class);

    private final KommaGenerator generator;
    private final PersistenceFactory persistenceFactory;

    private KommaMemoryBenchmark() {
        this.generator = new KommaGenerator(1);
        this.persistenceFactory = new PersistenceFactory();
    }

    public static void main(String[] args) {
        new KommaMemoryBenchmark().execute();
    }

    private void execute() {
        BenchmarkUtil.scheduleApplicationShutdown(Config.getRuntime().orElse(Constants.DEFAULT_MEMORY_RUNTIME));
        persistPersons();
        while (true) {
            try {
                persist();
                findAll();
                update();
                remove();
            } catch (AssertionError e) {
                LOG.warn("Exception caught!", e);
            }
        }
    }

    private void persistPersons() {
        final IEntityManager em = persistenceFactory.entityManager();
        generator.setEm(em);
        generator.persistPersons();
    }

    private void persist() {
        try (IEntityManager em = persistenceFactory.entityManager()) {
            generator.setEm(em);
            generator.executeCreate();
        }
    }

    private void findAll() {
        try (IEntityManager em = persistenceFactory.entityManager()) {
            final List<OccurrenceReport> result = em.findAll(OccurrenceReport.class).toList();
            assertEquals(generator.getReports().size(), result.size());
        }
    }

    private void update() {
        try (IEntityManager em = persistenceFactory.entityManager()) {
            generator.getReports().forEach(r -> {
                em.getTransaction().begin();
                KommaUpdater.updateReport(r, generator);
                em.getTransaction().commit();
            });
        } catch (RuntimeException e) {
            LOG.warn("Exception caught. {}", e);
        }
    }

    private void remove() {
        try (IEntityManager em = persistenceFactory.entityManager()) {
            generator.getReports().forEach(r -> {
                em.getTransaction().begin();
                final OccurrenceReport toDelete = em.find(generator.getUri(r), OccurrenceReport.class);
                deleteEvents(toDelete.getOccurrence().getSubEvents(), em);
                em.remove(toDelete.getOccurrence());
                toDelete.getAttachments().forEach(em::remove);
                em.remove(toDelete);
                em.getTransaction().commit();
            });
        }
    }

    private void deleteEvents(Set<Event> events, IEntityManager em) {
        if (events == null) {
            return;
        }
        for (Event e : events) {
            deleteEvents(e.getSubEvents(), em);
            em.remove(e);
        }
    }
}

package cz.cvut.kbss.benchmark.komma.util;

import cz.cvut.kbss.benchmark.komma.KommaGenerator;
import cz.cvut.kbss.benchmark.komma.model.DefaultOccurrenceReport;
import cz.cvut.kbss.benchmark.komma.model.Event;
import cz.cvut.kbss.benchmark.komma.model.OccurrenceReport;
import net.enilink.komma.core.IEntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cz.cvut.kbss.benchmark.util.Constants.ITEM_COUNT;
import static org.junit.Assert.assertFalse;

public class KommaDeleter {

    private final IEntityManager em;
    private final KommaGenerator generator;

    private List<DefaultOccurrenceReport> deleted;

    public KommaDeleter(IEntityManager em, KommaGenerator generator) {
        this.em = em;
        this.generator = generator;
    }

    public void executeDelete() {
        this.deleted = new ArrayList<>(ITEM_COUNT / 2);
        for (int i = 0; i < generator.getReports().size(); i++) {
            if (i % 2 != 0) {
                continue;
            }
            final DefaultOccurrenceReport report = generator.getDetachedReports().get(i);
            deleted.add(report);
            em.getTransaction().begin();
            final OccurrenceReport toDelete = em
                    .createQuery("construct { ?r a <komma:Result> . ?s ?p ?o } where { ?r (!<:>|<:>)* ?s . ?s ?p ?o }")
                    .setParameter("r", report.getUri()).getSingleResult(OccurrenceReport.class);
            deleteEvents(toDelete.getOccurrence().getSubEvents());
            em.remove(toDelete.getOccurrence());
            toDelete.getAttachments().forEach(em::remove);
            em.remove(toDelete);
            em.getTransaction().commit();
        }
    }

    private void deleteEvents(Set<Event> events) {
        if (events == null) {
            return;
        }
        for (Event e : events) {
            deleteEvents(e.getSubEvents());
            em.remove(e);
        }
    }

    public void verifyDelete() {
        deleted.forEach(r -> assertFalse(em.contains(em.find(r.getUri(), OccurrenceReport.class))));
    }
}

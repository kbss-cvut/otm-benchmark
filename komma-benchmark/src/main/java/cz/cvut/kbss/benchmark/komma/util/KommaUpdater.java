package cz.cvut.kbss.benchmark.komma.util;

import cz.cvut.kbss.benchmark.komma.KommaGenerator;
import cz.cvut.kbss.benchmark.komma.model.Occurrence;
import cz.cvut.kbss.benchmark.komma.model.OccurrenceReport;
import cz.cvut.kbss.benchmark.util.Constants;
import net.enilink.komma.core.IEntityManager;
import net.enilink.komma.core.URI;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class KommaUpdater {

    private final IEntityManager em;
    private final KommaGenerator generator;

    private List<URI> updatedUris;
    private List<OccurrenceReport> updated;

    public KommaUpdater(IEntityManager em, KommaGenerator generator) {
        this.em = em;
        this.generator = generator;
    }

    public void executeUpdate() {
        this.updated = new ArrayList<>(Constants.ITEM_COUNT / 2);
        this.updatedUris = new ArrayList<>(Constants.ITEM_COUNT / 2);
        for (int i = 0; i < generator.getDetachedReports().size(); i++) {
            if (i % 2 == 0) {
                continue;
            }
            em.getTransaction().begin();
            final OccurrenceReport toUpdate = em
                    .createQuery("construct { ?r a <komma:Result> . ?s ?p ?o } where { ?r (!<:>|<:>)* ?s . ?s ?p ?o }")
                    .setParameter("r", generator.getDetachedReports().get(i).getUri()).getSingleResult(OccurrenceReport.class);
            updateReport(toUpdate, generator);
            em.getTransaction().commit();
            updated.add(toUpdate);
            updatedUris.add(generator.getDetachedReports().get(i).getUri());
        }
    }

    public static void updateReport(OccurrenceReport toUpdate, KommaGenerator generator) {
        toUpdate.setLastModifiedBy(generator.randomItem(generator.getPersons()));
        toUpdate.getOccurrence().setName(toUpdate.getOccurrence().getName() + "-updated");
        toUpdate.setSeverityAssessment(generator.randomInt(Constants.MAX_SEVERITY));
        final GregorianCalendar lastModUpdated = new GregorianCalendar();
        lastModUpdated.setTimeInMillis(System.currentTimeMillis() + 10000);
        toUpdate.setLastModified(BenchmarkUtil.datatypeFactory().newXMLGregorianCalendar(lastModUpdated));
        toUpdate.setRevision(toUpdate.getRevision() + 1);
        toUpdate.getAuthor().getContacts().remove(toUpdate.getAuthor().getContacts().iterator().next());
        toUpdate.getAttachments().add(generator.generateAttachment());
    }

    public void verifyUpdates() {
        for (int i = 0; i < updated.size(); i++) {
            final OccurrenceReport expected = updated.get(i);
            final OccurrenceReport actual = em.find(updatedUris.get(i), OccurrenceReport.class);
            BenchmarkUtil.checkReport(expected, actual);
        }
    }
}

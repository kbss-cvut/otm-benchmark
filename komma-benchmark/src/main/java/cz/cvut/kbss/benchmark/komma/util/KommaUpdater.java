package cz.cvut.kbss.benchmark.komma.util;

import cz.cvut.kbss.benchmark.komma.KommaGenerator;
import cz.cvut.kbss.benchmark.komma.model.OccurrenceReport;
import cz.cvut.kbss.benchmark.util.Constants;
import net.enilink.komma.core.IEntityManager;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class KommaUpdater {

    private final IEntityManager em;
    private final KommaGenerator generator;

    private List<OccurrenceReport> updated;

    public KommaUpdater(IEntityManager em, KommaGenerator generator) {
        this.em = em;
        this.generator = generator;
    }

    public void executeUpdate() {
        this.updated = new ArrayList<>(Constants.ITEM_COUNT / 2);
        for (int i = 0; i < generator.getReports().size(); i++) {
            if (i % 2 == 0) {
                continue;
            }
            final OccurrenceReport toUpdate = generator.getReports().get(i);
            em.getTransaction().begin();
            updateReport(toUpdate, generator);
            em.getTransaction().commit();
            updated.add(toUpdate);
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
        updated.forEach(r -> {
            final OccurrenceReport result = em.find(generator.getUri(r), OccurrenceReport.class);
            BenchmarkUtil.checkReport(r, result);
        });
    }
}

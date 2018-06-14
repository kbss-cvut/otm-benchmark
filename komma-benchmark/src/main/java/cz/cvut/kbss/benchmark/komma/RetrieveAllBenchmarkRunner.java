package cz.cvut.kbss.benchmark.komma;

import cz.cvut.kbss.benchmark.komma.model.DefaultOccurrenceReport;
import cz.cvut.kbss.benchmark.komma.model.OccurrenceReport;
import cz.cvut.kbss.benchmark.komma.util.BenchmarkUtil;
import cz.cvut.kbss.benchmark.komma.util.KommaFinder;
import net.enilink.komma.core.IEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RetrieveAllBenchmarkRunner extends RetrieveBenchmarkRunner {

    @Override
    public void execute() {
        final IEntityManager em = persistenceFactory.entityManager();
        final KommaFinder finder = new KommaFinder(em);
        final List<OccurrenceReport> result = finder.findAll();
        verifyRetrieveAll(result);
    }

    private void verifyRetrieveAll(List<OccurrenceReport> actual) {
        final List<DefaultOccurrenceReport> expected = generator.getDetachedReports();
        assertEquals(expected.size(), actual.size());
        for (OccurrenceReport r : expected) {
            final Optional<OccurrenceReport> actualReport =
                    actual.stream().filter(rep -> r.getKey().equals(rep.getKey())).findFirst();
            assertTrue(actualReport.isPresent());
            BenchmarkUtil.checkReport(r, actualReport.get());
        }
    }
}

package cz.cvut.kbss.benchmark.alibaba;

import cz.cvut.kbss.benchmark.AbstractRunner;
import cz.cvut.kbss.benchmark.BenchmarkException;
import cz.cvut.kbss.benchmark.alibaba.data.AlibabaDataGenerator;
import cz.cvut.kbss.benchmark.alibaba.model.OccurrenceReport;
import cz.cvut.kbss.benchmark.alibaba.model.Person;
import cz.cvut.kbss.benchmark.alibaba.util.AliBabaSaver;
import cz.cvut.kbss.benchmark.data.DataGenerator;
import cz.cvut.kbss.benchmark.model.Resource;
import cz.cvut.kbss.benchmark.util.BenchmarkUtil;
import cz.cvut.kbss.benchmark.util.Config;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class AliBabaBenchmarkRunner extends AbstractRunner<Person, OccurrenceReport> {

    static final Logger LOG = LoggerFactory.getLogger(CreateBenchmarkRunner.class);

    PersistenceFactory persistenceFactory;

    @Override
    protected DataGenerator<Person, OccurrenceReport> createGenerator(int factor) {
        return new AlibabaDataGenerator(factor);
    }

    @Override
    public void setUp() {
        try {
            this.persistenceFactory = new PersistenceFactory();
        } catch (Exception e) {
            LOG.error("Unable to setup repository.", e);
            throw new BenchmarkException(e);
        }
        super.setUp();
    }

    @Override
    public void tearDown() {
        super.tearDown();
        try {
            persistenceFactory.close();
        } catch (RepositoryException e) {
            throw new BenchmarkException(e);
        }
        Config.getRepoUrl().ifPresent(rUrl -> BenchmarkUtil.clearRepository(rUrl + "/statements"));
    }

    @Override
    protected void checkReport(cz.cvut.kbss.benchmark.model.OccurrenceReport expected,
                               cz.cvut.kbss.benchmark.model.OccurrenceReport actual) {
        assertNotNull(actual);
        assertEquals(expected.getRevision(), actual.getRevision());
        assertEquals(expected.getLastModified(), actual.getLastModified());
        assertNotNull(actual.getOccurrence());
        assertEquals(expected.getOccurrence().getName(), actual.getOccurrence().getName());
        // Had to override because of problems with object equality
        final Set<String> expectedAttIds = new HashSet<>();
        expected.getAttachments().forEach(a -> expectedAttIds.add(((Resource) a).getKey()));
        final Set<String> actualAttIds = new HashSet<>();
        actual.getAttachments().forEach(a -> actualAttIds.add(((Resource) a).getKey()));
        assertEquals(expectedAttIds, actualAttIds);
        assertEquals(expected.getAuthor(), actual.getAuthor());
        assertEquals(expected.getLastModifiedBy(), actual.getLastModifiedBy());
        assertEquals(expected.getAuthor().getContacts(), actual.getAuthor().getContacts());
        assertEquals(expected.getLastModifiedBy().getContacts(), actual.getLastModifiedBy().getContacts());
        checkEvents(expected.getOccurrence().getSubEvents(), actual.getOccurrence().getSubEvents());
    }

    protected void persistTestData() {
        try {
            final ObjectConnection connection = persistenceFactory.objectConnection();
            persistData(new AliBabaSaver(connection));
            connection.close();
        } catch (Exception e) {
            throw new BenchmarkException(e);
        }
    }
}

package cz.cvut.kbss.benchmark.jopa;

import cz.cvut.kbss.benchmark.jopa.util.JopaFinder;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProperties;

import java.util.Map;

public class RetrieveAllBenchmarkRunner extends RetrieveBenchmarkRunner {

    @Override
    public void execute() {
        final EntityManager em = persistenceFactory.entityManager(
                Map.of(JOPAPersistenceProperties.TRANSACTION_MODE, "read_only"));
        executeRetrieveAll(new JopaFinder(em));
    }
}

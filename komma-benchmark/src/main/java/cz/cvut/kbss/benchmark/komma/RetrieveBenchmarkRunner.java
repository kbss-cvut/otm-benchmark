package cz.cvut.kbss.benchmark.komma;

import cz.cvut.kbss.benchmark.komma.util.BenchmarkUtil;
import cz.cvut.kbss.benchmark.komma.util.KommaFinder;
import net.enilink.komma.core.IEntityManager;

public class RetrieveBenchmarkRunner extends KommaBenchmarkRunner {

    @Override
    public void setUp() {
        super.setUp();
        final IEntityManager em = persistenceFactory.entityManager();
        generator.setEm(em);
        generator.persistDataWithDetached();
        em.close();
        System.gc();
        System.gc();
        startMeasuringMemoryUsage();
    }

    @Override
    public void execute() {
        final IEntityManager em = persistenceFactory.entityManager();
        BenchmarkUtil.executeRetrieve(generator, new KommaFinder(em));
    }
}

package cz.cvut.kbss.benchmark.komma;

import cz.cvut.kbss.benchmark.komma.util.KommaUpdater;
import net.enilink.komma.core.IEntityManager;

public class UpdateBenchmarkRunner extends KommaBenchmarkRunner {

    private KommaUpdater updater;

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
    public void tearDown() {
        updater.verifyUpdates();
        super.tearDown();
    }

    @Override
    public void execute() {
        final IEntityManager em = persistenceFactory.entityManager();
        this.updater = new KommaUpdater(em, generator);
        updater.executeUpdate();
    }
}

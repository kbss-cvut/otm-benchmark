package cz.cvut.kbss.benchmark.komma;

import cz.cvut.kbss.benchmark.komma.util.KommaDeleter;
import net.enilink.komma.core.IEntityManager;

public class DeleteBenchmarkRunner extends KommaBenchmarkRunner {

    private KommaDeleter deleter;

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
        deleter.verifyDelete();
        super.tearDown();
    }

    @Override
    public void execute() {
        final IEntityManager em = persistenceFactory.entityManager();
        this.deleter = new KommaDeleter(em, generator);
        deleter.executeDelete();
    }
}

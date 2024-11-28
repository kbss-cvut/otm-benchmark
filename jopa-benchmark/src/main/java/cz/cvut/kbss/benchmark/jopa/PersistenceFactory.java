package cz.cvut.kbss.benchmark.jopa;

import cz.cvut.kbss.benchmark.util.Config;
import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProperties;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProvider;
import cz.cvut.kbss.ontodriver.rdf4j.config.Rdf4jOntoDriverProperties;

import java.util.HashMap;
import java.util.Map;


class PersistenceFactory {

    private final EntityManagerFactory emf;

    PersistenceFactory() {
        // When running in a jar, RDF4J for some reason does not register appropriate RDF writer factories
//        RDFWriterRegistry.getInstance().add(new BinaryRDFWriterFactory());
        final Map<String, String> properties = new HashMap<>();
        if (Config.getRepoUrl().isPresent()) {
            properties.put(JOPAPersistenceProperties.ONTOLOGY_PHYSICAL_URI_KEY, Config.getRepoUrl().get());
        } else {
            properties.put(JOPAPersistenceProperties.ONTOLOGY_PHYSICAL_URI_KEY, "BenchmarkStorage");
            properties.put(Rdf4jOntoDriverProperties.USE_VOLATILE_STORAGE, Boolean.TRUE.toString());
        }
        properties.put(JOPAPersistenceProperties.DATA_SOURCE_CLASS, "cz.cvut.kbss.ontodriver.rdf4j.Rdf4jDataSource");
        properties.put(JOPAPersistenceProperties.LANG, "en");
        properties.put(JOPAPersistenceProperties.SCAN_PACKAGE, "cz.cvut.kbss.benchmark.jopa.model");
        properties.put(JOPAPersistenceProperties.JPA_PERSISTENCE_PROVIDER, JOPAPersistenceProvider.class.getName());
        properties.put(JOPAPersistenceProperties.CACHE_ENABLED, Boolean.FALSE.toString());
        this.emf = Persistence.createEntityManagerFactory("benchmarkPU", properties);
    }

    EntityManager entityManager() {
        return emf.createEntityManager();
    }

    void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}

package cz.cvut.kbss.benchmark.komma;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import cz.cvut.kbss.benchmark.komma.model.Event;
import cz.cvut.kbss.benchmark.komma.model.Occurrence;
import cz.cvut.kbss.benchmark.komma.model.OccurrenceReport;
import cz.cvut.kbss.benchmark.komma.model.Person;
import cz.cvut.kbss.benchmark.komma.model.Resource;
import cz.cvut.kbss.benchmark.komma.util.DisabledCacheModule;
import cz.cvut.kbss.benchmark.util.Config;
import net.enilink.komma.core.IEntityManager;
import net.enilink.komma.core.IEntityManagerFactory;
import net.enilink.komma.core.IUnitOfWork;
import net.enilink.komma.core.KommaModule;
import net.enilink.komma.em.EntityManagerFactoryModule;
import net.enilink.komma.em.util.UnitOfWork;
import net.enilink.komma.rdf4j.RDF4JModule;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFWriterRegistry;
import org.eclipse.rdf4j.rio.binary.BinaryRDFWriterFactory;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class PersistenceFactory {

    private final Repository repository;
    private final IEntityManagerFactory emf;

    PersistenceFactory() {
        // When running in a jar, RDF4J for some reason does not register appropriate RDF writer factories
        RDFWriterRegistry.getInstance().add(new BinaryRDFWriterFactory());
        if (Config.getRepoUrl().isPresent()) {
            this.repository = new HTTPRepository(Config.getRepoUrl().get());
        } else {
            this.repository = new SailRepository(new MemoryStore());
        }
        repository.init();
        final KommaModule kommaModule = new KommaModule() {
            {
                addConcept(Person.class);
                addConcept(Event.class);
                addConcept(Occurrence.class);
                addConcept(OccurrenceReport.class);
                addConcept(Resource.class);
            }
        };

        // create a Guice injector and retrieve an entity manager instance
        Injector injector = Guice.createInjector(createGuiceModule(kommaModule, repository));
        this.emf = injector.getInstance(IEntityManagerFactory.class);
    }

    private Module createGuiceModule(KommaModule kommaModule, Repository repository) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                install(new RDF4JModule());
                // Disable cache, like all other libraries
                install(new EntityManagerFactoryModule(kommaModule, null, new DisabledCacheModule()));

                UnitOfWork uow = new UnitOfWork();
                uow.begin();

                bind(UnitOfWork.class).toInstance(uow);
                bind(IUnitOfWork.class).toInstance(uow);
                bind(Repository.class).toInstance(repository);
            }
        };
    }

    public IEntityManager entityManager() {
        return emf.get();
    }

    public void close() {
        emf.close();
        if (repository.isInitialized()) {
            repository.shutDown();
        }
    }
}

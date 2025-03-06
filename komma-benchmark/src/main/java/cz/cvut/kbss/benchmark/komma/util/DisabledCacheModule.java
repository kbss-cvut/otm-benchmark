package cz.cvut.kbss.benchmark.komma.util;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.enilink.composition.cache.IPropertyCache;
import net.enilink.komma.core.IEntityManager;
import net.enilink.komma.core.URI;
import net.enilink.komma.em.DecoratingEntityManagerModule;
import net.enilink.komma.em.internal.CachingEntityManager;
import net.enilink.komma.em.internal.Fqn;

import java.util.Set;

/**
 * Represents a disabled cache.
 * <p>
 * This is done so that all the frameworks have the same conditions. JOPA and has also disabled cache for the
 * benchmark.
 */
public class DisabledCacheModule extends DecoratingEntityManagerModule {
    @Override
    protected void configure() {
        super.configure();
    }

    @Override
    protected Class<? extends IEntityManager> getManagerClass() {
        return CachingEntityManager.class;
    }

    @Provides
    @Inject(
            optional = true
    )
    Fqn provideContextKey(@Named("modifyContexts") Set<URI> modifyContexts) {
        return modifyContexts != null ? new Fqn(modifyContexts.toArray()) : new Fqn(new Object[0]);
    }

    @Provides
    @Singleton
    IPropertyCache provideCache() {
        return new IPropertyCache() {
            @Override
            public Object put(Object o, Object o1, Object[] objects, Object o2) {
                return null;
            }

            @Override
            public Object get(Object o, Object o1, Object[] objects) {
                return null;
            }
        };
    }
}

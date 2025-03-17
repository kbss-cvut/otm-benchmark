package cz.cvut.kbss.benchmark.komma.util;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import net.enilink.komma.core.IEntityManager;
import net.enilink.komma.core.IReference;
import net.enilink.komma.core.URI;
import net.enilink.komma.em.DecoratingEntityManagerModule;
import net.enilink.komma.em.internal.EagerCachingEntityManager;
import net.enilink.komma.em.internal.Fqn;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
        return EagerCachingEntityManager.class;
    }

    @Provides
    @Inject(optional = true)
    Fqn provideContextKey(@Named("modifyContexts") Set<URI> modifyContexts) {
        return modifyContexts != null ? new Fqn(modifyContexts.toArray()) : new Fqn();
    }

    @Provides
    Map<IReference, Object> cache() {
        return new ConcurrentHashMap<>();
    }
}

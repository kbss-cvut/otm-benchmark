package cz.cvut.kbss.benchmark.komma.util;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.enilink.composition.cache.IPropertyCache;

/**
 * Represents a disabled cache.
 * <p>
 * This is done so that all the frameworks have the same conditions. JOPA and has also disabled cache for the
 * benchmark.
 */
public class DisabledCacheModule extends AbstractModule {
    @Override
    protected void configure() {
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

package mentoring.concurrency;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;

/**
 * Provides {@link ConcurrencyHandler} binding.
 */
public class ConcurrencyModule extends AbstractModule {
    @Provides @Singleton
    public ConcurrencyHandler provideConcurrencyHandler() {
        return new ConcurrencyHandler();
    }
}

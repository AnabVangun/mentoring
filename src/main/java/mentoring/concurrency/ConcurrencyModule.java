package mentoring.concurrency;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javax.inject.Singleton;

/**
 * Provides {@link ConcurrencyHandler} binding.
 */
public class ConcurrencyModule extends AbstractModule {
    //FIXME: Singleton annotation should not be needed here but it seems to be.
    @Provides @Singleton
    public ConcurrencyHandler provideConcurrencyHandler() {
        return new ConcurrencyHandler();
    }
}

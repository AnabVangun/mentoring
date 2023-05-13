package mentoring.viewmodel.datastructure;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Provides {@link PersonMatchesViewModel} binding.
 */
public class PersonViewModelModule extends AbstractModule {
    
    @Provides
    public PersonMatchesViewModel providePersonMatchesViewModel() {
        return new PersonMatchesViewModel();
    }
}

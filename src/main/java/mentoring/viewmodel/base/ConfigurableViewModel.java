package mentoring.viewmodel.base;

import mentoring.configuration.Configuration;

/**
 * Interface for view models that can be configured.
 * @param <T> the type of {@link Configuration} this ViewModel accepts
 */
public interface ConfigurableViewModel<T extends Configuration<T>> {
    
    /**
     * Set how the encapsulated data should be displayed, and what data to expose.
     * @param configuration used to select the attributes to represent
     */
    void setConfiguration(T configuration);
}

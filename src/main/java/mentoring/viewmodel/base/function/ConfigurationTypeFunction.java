package mentoring.viewmodel.base.function;

import java.io.IOException;
import mentoring.configuration.Configuration;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;

/**
 * Represents an operation that accepts a single ViewModel argument and returns a 
 * {@link Configuration}.
 */
@FunctionalInterface
public interface ConfigurationTypeFunction {
    <T extends Configuration<T>> T getConfiguration(ConfigurationPickerViewModel<T> viewModel) 
            throws IOException;
}

package mentoring.viewmodel.base.function;

import java.io.File;
import java.io.IOException;
import mentoring.configuration.Configuration;

/**
 * Represents an operation that accepts a single File argument and returns a {@link Configuration}.
 * @param <T> type of the configuration to parse
 */
@FunctionalInterface
public interface ConfigurationParser<T extends Configuration<T>> {
    T apply(File file) throws IOException;
}

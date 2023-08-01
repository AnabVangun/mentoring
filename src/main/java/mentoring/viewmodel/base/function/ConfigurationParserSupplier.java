package mentoring.viewmodel.base.function;

import java.util.function.Supplier;
import mentoring.configuration.Configuration;
import mentoring.io.Parser;

/**
 * Represents an operation that accepts a single input argument and returns a {@link Parser} for
 * the appropriate configuration.
 * @param <T> type of the configuration to parse
 * @param <E> type of the returned parser
 */
@FunctionalInterface
public interface ConfigurationParserSupplier<T extends Configuration<T>, E extends Parser<T>> 
        extends Supplier<E> {
    
}

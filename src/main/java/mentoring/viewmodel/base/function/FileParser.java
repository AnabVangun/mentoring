package mentoring.viewmodel.base.function;

import java.io.File;
import java.io.IOException;

/**
 * Represents an operation that accepts a single File argument and returns its content as a Java 
 * object.
 * @param <T> type of the configuration to parse
 */
@FunctionalInterface
public interface FileParser<T> {
    T apply(File file) throws IOException;
}

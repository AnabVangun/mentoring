package mentoring.viewmodel.base.function;

import java.io.IOException;
import java.io.Reader;

/**
 * Represents an operation that accepts a single input argument and returns a {@link Reader}.
 * A typical implementation would return a FileReader using the input as a file path.
 */
@FunctionalInterface
public interface ReaderGenerator {
    Reader generate(String input) throws IOException;
}

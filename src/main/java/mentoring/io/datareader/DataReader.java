package mentoring.io.datareader;

import java.io.Reader;
import java.util.Map;
import java.util.Objects;

/**
 * Reader used to extract data from a file.
 * <p>Classes implementing this interface SHOULD be safe for reuse and multi-threading.
 */
public abstract class DataReader {
    /**
     * Extract data from the reader.
     * @param reader data source.
     * @return the data extracted from the data source.
     */
    public Map<String, Object> read(Reader reader){
        Objects.requireNonNull(reader);
        try {
            return readWithException(reader);
        } catch (Exception e){
            throw new IllegalArgumentException("Could not parse content from reader", e);
        }
    }
    
    protected abstract Map<String, Object> readWithException(Reader reader) throws Exception;
}

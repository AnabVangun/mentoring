package mentoring.io;

import java.io.Reader;
import java.util.Map;
import java.util.Objects;
import mentoring.io.datareader.DataReader;

/**
 * Parser used to build objects from files. 
 * <p>Subclasses SHOULD be safe for reuse and for multi-threading.
 */
abstract class Parser<T> {
    //Reader used to extract POJO data from a textual data source.
    protected final DataReader dataReader;
    //TODO add tests for default methods
    
    /**
     * Create a Parser.
     * @param dataReader reader used to extract POJO data from a textual data source.
     */
    protected Parser(DataReader dataReader){
        this.dataReader = dataReader;
    }
    
    /**
     * Parse an object from a data source.
     * @param reader data source representing in textual format the object to parse
     * @return the parsed object
     * @throws IllegalArgumentException if the content provided by the reader does not have the 
     * appropriate format.
     */
    public T parse(Reader reader) throws IllegalArgumentException {
        Objects.requireNonNull(reader);
        Map<String, Object> data = dataReader.read(reader);
        //TODO: missing the "validity check" step.
        //TODO: get behaviour from PropertyNameParser
        return buildObject(data);
    }
    
    protected abstract T buildObject(Map<String, Object> data);
    
    //TODO: this fails when type is List<String> or any other parameterized type
    @SuppressWarnings("unchecked")
    protected static <T> T extractAttribute(Map<String, Object> data, String propertyKey, 
            Class<T> type){
        if(data.containsKey(propertyKey)){
            return (T) data.get(propertyKey);
        } else {
            throw new IllegalArgumentException("Property %s is missing.".formatted(propertyKey));
        }
    }
    
    protected static Object extractAttribute(Map<String, Object> data, String propertyKey){
        return extractAttribute(data, propertyKey, Object.class);
    }
}

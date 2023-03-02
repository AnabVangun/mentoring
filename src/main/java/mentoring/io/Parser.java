package mentoring.io;

import java.io.Reader;
import java.util.Map;
import java.util.Objects;
import mentoring.io.datareader.DataReader;

/**
 * Parser used to build objects from readers. 
 * <p>Subclasses SHOULD be safe for reuse and for multi-threading.
 */
abstract class Parser<T> {
    //Reader used to extract POJO data from a textual data source.
    protected final DataReader dataReader;
    
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
        //TODO: get behaviour from PropertyNameParser
        return buildObject(data);
    }
    
    /**
     * Convert the raw data extracted from the reader into the expected Java object.
     * Implementations of this method SHOULD raise exceptions deriving from IllegalArgumentException
     * if the object cannot be built.
     * @param data extracted from the data source using a {@link DataReader}.
     * @return an object of the class this parser is tailored to parse.
     */
    protected abstract T buildObject(Map<String, Object> data);
    
    //TODO: this fails when type is List<String> or any other parameterized type
    @SuppressWarnings("unchecked")
    protected static <T> T extractAttribute(Map<? extends String, ? extends Object> data, 
            String propertyKey, Class<T> type){
        if(data.containsKey(propertyKey)){
            return (T) data.get(propertyKey);
        } else {
            throw new IllegalArgumentException("Property %s is missing.".formatted(propertyKey));
        }
    }
    
    protected static Object extractAttribute(Map<? extends String, ? extends Object> data, 
            String propertyKey){
        return extractAttribute(data, propertyKey, Object.class);
    }
}

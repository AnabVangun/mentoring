package mentoring.io;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import mentoring.io.datareader.DataReader;

/**
 * Parser used to build objects from readers. 
 * <p>Subclasses SHOULD be safe for reuse and for multi-threading.
 */
abstract class Parser<T> {
    //TODO: refactor to build a common abstract class for Parser and Decoder
    //Reader used to extract POJO data from a textual data source.
    protected final DataReader dataReader;
    
    private final static int BASE_ERROR_LENGTH = 250;
    
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
        assertDataValidity(data);
        return buildObject(data);
    }
    
    private void assertDataValidity(Map<String, Object> data){
        List<String> errors = registerSpecificErrors(data);
        registerMissingKeyErrors(data, errors);
        raiseExceptionIfAppropriate(data, errors);
    }
    
    /**
     * Perform the validity checks specific to the type of objects the parser handles. Errors 
     * related to missing attributes should not be registered in this method to avoid duplicates.
     * @param data map representing the data to decode.
     * @return a modifiable list containing the specific errors found so that a global
     * exception can be raised at the end of the validation.
     */
    protected abstract List<String> registerSpecificErrors(Map<String, Object> data);
    
    private void registerMissingKeyErrors(Map<String, Object> data, List<String> errorsFound){
        for (String expectedKey : getExpectedKeys()){
            if (! data.containsKey(expectedKey)){
                errorsFound.add("Key %s expected but missing from %s".formatted(expectedKey, data));
            }
        }
    }
    
    private void raiseExceptionIfAppropriate(Map<String, Object> data, List<String> errorsFound){
        if (! errorsFound.isEmpty()){
            String errorMessage;
            if (errorsFound.size() > 1){
                errorMessage = forgeComplexErrorMessage(data, errorsFound);
            } else {
                errorMessage = "Error found when decoding object %s: %s".formatted(data, 
                        errorsFound.get(0));
            }
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
    private String forgeComplexErrorMessage(Map<String, Object> data, List<String> errorsFound){
        String baseMessage = "Several errors found when decoding object %s:".formatted(data);
        StringBuilder builder = new StringBuilder(baseMessage.length() 
                + errorsFound.size()*BASE_ERROR_LENGTH);
        builder.append(baseMessage);
        for(String error: errorsFound){
            builder.append(System.lineSeparator()).append(error);
        }
        return builder.toString();
    }
    
    /**
     * Return the set of keys that are mandatory to parse a data source.
     */
    protected abstract Set<String> getExpectedKeys();
    
    /**
     * Convert the raw data extracted from the reader into the expected Java object.
     * Implementations of this method SHOULD raise exceptions deriving from IllegalArgumentException
     * if the object cannot be built.
     * @param data extracted from the data source using a {@link DataReader}.
     * @return an object of the class this parser is tailored to parse.
     */
    protected abstract T buildObject(Map<String, Object> data);
    
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
    
    @SuppressWarnings("unchecked")
    protected static <T> List<T> extractAttributeList(Map<? extends String, ? extends Object> data,
            String propertyKey, Class<T> type){
        return (List<T>) extractAttribute(data, propertyKey);
    }
}

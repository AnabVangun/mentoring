package mentoring.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mentoring.datastructure.AggregationType;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.MultiplePropertyNameBuilder;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.PropertyNameBuilder;
import mentoring.datastructure.PropertyType;

/**
 * Decoder used to build {@link PropertyName} and {@link MultiplePropertyName} objects from 
 * configuration files.
 * <p>This class should be subclassed for each type of {@link PropertyName} object that need 
 * specific parsing.
 * @param E specific type of PropertyName parsed.
 */
abstract class PropertyNameDecoder<E extends PropertyName<?>> {
    //TODO test class and subclasses.
    
    Set<E> decodePropertyNames(
            Iterable<? extends Map<? extends String, ? extends String>> properties){
        Set<E> result = new HashSet<>();
        for (Map<? extends String, ? extends String> property : properties){
            validateProperty(property);
            result.add(decodeSinglePropertyName(property));
        }
        return result;
    }
    
    private void validateProperty(Map<? extends String, ? extends String> toValidate){
        List<String> errorsFound = new ArrayList<>();
        registerMissingAttributeErrors(toValidate, errorsFound);
        registerSpecificErrors(toValidate, errorsFound);
        raiseExceptionIfAppropriate(errorsFound);
    }
    
    private void registerMissingAttributeErrors(
            Map<? extends String, ? extends String> toValidate, List<String> errorsFound){
        for (String expectedAttribute : getExpectedAttributeNames()){
            if (! toValidate.containsKey(expectedAttribute)){
                errorsFound.add("Attribute %s expected but missing from %s"
                        .formatted(expectedAttribute, toValidate));
            }
        }
    }
    
    /**
     * Perform the validity checks specific to the type of PropertyName of the decoder.
     * @param toValidate map representing the property to decode.
     * @param errorsFound the specific errors found must be registered in this list so that a global
     * exception can be raised at the end of the validation.
     */
    protected abstract void registerSpecificErrors(
            Map<? extends String, ? extends String> toValidate, List<String> errorsFound);
    
    /**
     * Register errors if the input map contains any unexpected attribute. This method is not called 
     * by default by {@link #validateProperty(java.util.Map)}: it is a convenience method provided
     * for use in {@link #registerSpecificErrors(java.util.Map, java.util.List) } by subclasses 
     * whenever appropriate.
     * @param toValidate map representing the property to decode.
     * @param errorsFound error messages for the unexpected attributes found are registered 
     * in this list so that a global exception can be raised at the end of the validation.
     */
    protected final void registerUnexpectedAttributeError(
            Map<? extends String, ? extends String> toValidate, List<String> errorsFound){
        for (String key : toValidate.keySet()){
            if (! getExpectedAttributeNames().contains(key)){
                errorsFound.add("Attribute %s found in %s but not expected, valid attributes are %s"
                        .formatted(key, toValidate, getExpectedAttributeNames()));
            }
        }
    }
    
    /**
     * Return the set of attributes that are mandatory to decode a property name.
     */
    protected abstract Set<String> getExpectedAttributeNames();
    
    private void raiseExceptionIfAppropriate(List<String> errorsFound){
        if(errorsFound.size() == 1){
            throw new IllegalArgumentException(errorsFound.get(0));
        } else if (errorsFound.size() > 1){
            throw new IllegalArgumentException(forgeComplexErrorMessage(errorsFound));
        }
    }
    
    private String forgeComplexErrorMessage(List<String> errorsFound){
        String baseMessage = "Several errors found when decoding property:";
        StringBuilder builder = new StringBuilder(baseMessage.length() + errorsFound.size()*250);
        builder.append(baseMessage);
        for(String error: errorsFound){
            builder.append(System.lineSeparator()).append(error);
        }
        return builder.toString();
    }
    
    /**
     * Decode a single valid PropertyName.
     * @param toDecode map representing the property to decode.
     * @return a valid PropertyName.
     */
    protected abstract E decodeSinglePropertyName(Map<? extends String, ? extends String> toDecode);
    
}

class SimplePropertyNameDecoder extends PropertyNameDecoder<PropertyName<?>>{
    final PropertyNameBuilder builder = new PropertyNameBuilder();
    final static Set<String> EXPECTED_PROPERTY_ATTRIBUTES = Set.of("name", "headerName", 
            "type");
    
    @Override
    protected Set<String> getExpectedAttributeNames(){
        return EXPECTED_PROPERTY_ATTRIBUTES;
    }
    
    @Override
    protected void registerSpecificErrors(Map<? extends String, ? extends String> toValidate, 
            List<String> errorsFound){
        registerUnexpectedAttributeError(toValidate, errorsFound);
    }

    @Override
    protected PropertyName<?> decodeSinglePropertyName(
            Map<? extends String, ? extends String> toDecode) {
        PropertyType<?> type = PropertyType.valueOf(toDecode.get("type"));
        return builder.prepare(toDecode.get("name"), type)
                .withHeaderName(toDecode.get("headerName"))
                .build();
    }
}

class MultiplePropertyNameDecoder extends PropertyNameDecoder<MultiplePropertyName<?,?>>{
    final MultiplePropertyNameBuilder builder = new MultiplePropertyNameBuilder();
    
    private final static Set<String> EXPECTED_PROPERTY_ATTRIBUTES;
    static {
        Set<String> properties = new HashSet<>(
            SimplePropertyNameDecoder.EXPECTED_PROPERTY_ATTRIBUTES);
        properties.add("aggregation");
        EXPECTED_PROPERTY_ATTRIBUTES = Collections.unmodifiableSet(properties);
    }
    
    @Override
    protected Set<String> getExpectedAttributeNames(){
        return EXPECTED_PROPERTY_ATTRIBUTES;
    }
    
    @Override
    protected void registerSpecificErrors(
            Map<? extends String, ? extends String> toValidate, List<String> errorsFound){
        registerUnexpectedAttributeError(toValidate, errorsFound);
    }

    @Override
    protected MultiplePropertyName<?,?> decodeSinglePropertyName(
            Map<? extends String, ? extends String> toDecode) {
        PropertyType<?> type = PropertyType.valueOf(toDecode.get("type"));
        AggregationType aggregation = AggregationType.getValueOf(toDecode.get("aggregation"));
        return builder.prepare(toDecode.get("name"), type)
                .setAggregation(aggregation)
                .withHeaderName(toDecode.get("headerName"))
                .build();
    }
}
package mentoring.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    private final static int BASE_ERROR_LENGTH = 250;
    
    Set<E> decodePropertyNames(
            Iterable<? extends Map<? extends String, ? extends String>> properties){
        Set<E> result = new HashSet<>();
        Map<Map<? extends String, ? extends String>, List<String>> errorsFoundByProperty 
                = new HashMap<>();
        for (Map<? extends String, ? extends String> property : properties){
            List<String> errors = getErrorsInProperty(property);
            if(errors.isEmpty()){
                result.add(decodeSinglePropertyName(property));
            } else {
                errorsFoundByProperty.put(property, errors);
            }
        }
        raiseExceptionIfAppropriate(errorsFoundByProperty);
        return result;
    }
    
    private List<String> getErrorsInProperty(Map<? extends String, ? extends String> toValidate){
        List<String> errorsFound = registerSpecificErrors(toValidate);
        registerMissingAttributeErrors(toValidate, errorsFound);
        return errorsFound;
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
     * Perform the validity checks specific to the type of PropertyName of the decoder. Errors 
     * related to missing attributes should not be registered in this method to avoid duplicates.
     * @param toValidate map representing the property to decode.
     * @return a modifiable list containing the specific errors found so that a global
     * exception can be raised at the end of the validation.
     */
    protected abstract List<String> registerSpecificErrors(
            Map<? extends String, ? extends String> toValidate);
    
    /**
     * Register errors if the input map contains any unexpected attribute. This method is not called 
     * by default by {@link #getErrorsInProperty(java.util.Map)}: it is a convenience method provided
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
    
    private void raiseExceptionIfAppropriate(
            Map<Map<? extends String, ? extends String>, List<String>> errorsFoundByProperty){
        if (! errorsFoundByProperty.isEmpty()){
            StringBuilder concatenatedError = 
                    new StringBuilder(errorsFoundByProperty.size()*BASE_ERROR_LENGTH);
            errorsFoundByProperty.forEach((key, value) -> 
                    concatenatedError.append(forgeComplexErrorMessage(key, value)));
            throw new IllegalArgumentException(concatenatedError.toString());
        }
    }
    
    private String forgeComplexErrorMessage(Map<? extends String, ? extends String> property, 
            List<String> errorsFound){
        String baseMessage = "Several errors found when decoding property %s:".formatted(property);
        StringBuilder builder = new StringBuilder(baseMessage.length() 
                + errorsFound.size()*BASE_ERROR_LENGTH);
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
    protected List<String> registerSpecificErrors(Map<? extends String, ? extends String> toValidate){
        List<String> result = new ArrayList<>();
        registerUnexpectedAttributeError(toValidate, result);
        return result;
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
    protected List<String> registerSpecificErrors(
            Map<? extends String, ? extends String> toValidate){
        List<String> result = new ArrayList<>();
        registerUnexpectedAttributeError(toValidate, result);
        return result;
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
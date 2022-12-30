package mentoring.io;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import mentoring.datastructure.IndexedPropertyName;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyName;

/**
 * Parser used to build {@link PropertyName} and {@link MultiplePropertyName} objects from 
 * configuration files.
 * <p>This class should be subclassed for each type of {@link PropertyName} object that need 
 * specific parsing.
 * @param T type contained by the PropertyName parsed.
 * @param E specific type of PropertyName parsed.
 */
abstract class PropertyNameParser<E extends PropertyName<?>> {
    
    Set<E> parsePropertyNames(Iterable<Map<String, String>> toParse){
        Set<E> properties = new HashSet<>();
        for (Map<String, String> property : toParse){
            validateProperty(property);
            properties.add(parseSinglePropertyName(property));
        }
        return properties;
    }
    
    private void validateProperty(Map<String, String> toValidate){
        boolean valid = true;
        for (String expectedAttribute : getExpectedAttributeNames()){
            valid = valid && toValidate.containsKey(expectedAttribute);
        }
        valid = valid && additionalValidityPredicate(toValidate);
        if (! valid){
            //TODO: this error message is vague and does not tell what failed (even more so for specific errors).
            throw new IllegalArgumentException("Expected attributes %s but received %s"
                    .formatted(getExpectedAttributeNames(), toValidate.keySet()));
        }
    }
    
    protected abstract Set<String> getExpectedAttributeNames();
    
    /**
     * Perform the validity checks specific to the type of PropertyName of the parser.
     * @param toValidate map representing the property to parse.
     * @return true if and only if the property conforms to its specific contract.
     */
    protected abstract boolean additionalValidityPredicate(Map<String, String> toValidate);
    
    /**
     * Parse a single valid PropertyName.
     * @param toParse map representing the property to parse.
     * @return a valid PropertyName.
     */
    protected abstract E parseSinglePropertyName(Map<String, String> toParse);
    
}

class SimplePropertyNameParser extends PropertyNameParser<PropertyName<?>>{

    final static Set<String> EXPECTED_PROPERTY_ATTRIBUTES = Set.of("name", "headerName", 
            "type");
    
    @Override
    protected Set<String> getExpectedAttributeNames(){
        return EXPECTED_PROPERTY_ATTRIBUTES;
    }
    
    @Override
    protected boolean additionalValidityPredicate(Map<String, String> toValidate) {
        return toValidate.size() == getExpectedAttributeNames().size();
    }

    @Override
    protected PropertyName<?> parseSinglePropertyName(Map<String, String> toParse) {
        return new PropertyName<>(toParse.get("name"), toParse.get("headerName"), 
                PropertyType.valueOf(toParse.get("type")));
    }
}

class MultiplePropertyNameParser extends PropertyNameParser<MultiplePropertyName<?,?>>{
    //TODO: this feels desperately clumsy
    private final static Set<String> EXPECTED_PROPERTY_ATTRIBUTES = new HashSet<>(
            SimplePropertyNameParser.EXPECTED_PROPERTY_ATTRIBUTES);
    static {
        EXPECTED_PROPERTY_ATTRIBUTES.add("aggregation");
    }
    
    @Override
    protected Set<String> getExpectedAttributeNames(){
        return EXPECTED_PROPERTY_ATTRIBUTES;
    }
    
    @Override
    protected boolean additionalValidityPredicate(Map<String, String> toValidate) {
        return toValidate.size() == getExpectedAttributeNames().size();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected MultiplePropertyName<?,?> parseSinglePropertyName(Map<String, String> toParse) {
        String name = toParse.get("name");
        String headerName = toParse.get("headerName");
        PropertyType<?> type = PropertyType.valueOf(toParse.get("type"));
        return switch(toParse.get("aggregation").toLowerCase()){
            case "set" -> new SetPropertyName(name, headerName, type);
            case "indexed" -> new IndexedPropertyName(name, headerName, type);
            default -> throw new IllegalArgumentException("invalid aggregation");
        };
    }    
    
}
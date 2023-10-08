package mentoring.datastructure;

import java.util.Map;
import java.util.function.Function;

/**
 * Immutable description of a property consisting of a key-value pair.
 * @param <K> type of the key in the key-value pairs stored in the property
 * @param <V> type of the value in the key-value pairs stored in the property
 */
public class MultiplePropertyName<K,V> extends PropertyName<K>{
    /*FIXME: refactor MultiplePropertyName should not extend but contain a PropertyName to avoid 
    complications with equals.
    Do the same for MultiplePropertyNameBuilder
    Other option: make SimplePropertyName and MultiplePropertyName be two implementations of a 
    common PropertyName interface
    */
    private final PropertyType<V> valueType;
    private final Function<String[], Map<K, V>> parser;
    
    /**
     * Initialises a new MultiplePropertyName.
     * @param name of the property in {@link Person} objects
     * @param headerName of the property in the header of data files
     * @param keyType expected type of the property key
     * @param valueType expected type of the value key
     * @param parser function building the key-value pairs from an array of unparsed values
     */
    MultiplePropertyName(String name, String headerName, PropertyType<K> keyType,
            PropertyType<V> valueType,
            Function<String[], Map<K,V>> parser){
        super(name, headerName, keyType);
        this.parser = parser;
        this.valueType = valueType;
    }
    
    /**
     * Associates values to parsed simple properties.
     * @param keys unparsed keys
     * @return a map with values associated to the input properties
     */
    public Map<K,V> buildMap(String[] keys){
        return parser.apply(keys);
    }
    
    /** Returns the type of the property values. */
    public PropertyType<V> getValueType(){
        return valueType;
    }
    
    @Override
    public MultiplePropertyName<K,V> withHeaderName(String headerName){
        return new MultiplePropertyName<>(getName(), headerName, getType(), valueType, parser);
    }
    
    @Override
    public String getStringRepresentation(Person person){
        return person.getPropertyAsMapOf(getName(), getType().getType(), valueType.getType())
                .toString();
    }
    
    @Override
    public boolean equals(Object o){
        return o instanceof MultiplePropertyName cast && attributeEquals(cast) 
                && valueType.equals(cast.valueType) && parser.equals(cast.parser);
    }
    
    @Override
    public int hashCode(){
        return (attributeHashCode() * 31 + valueType.hashCode()) * 31 + parser.hashCode();
    }
}

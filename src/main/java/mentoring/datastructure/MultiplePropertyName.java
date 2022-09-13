package mentoring.datastructure;

import java.util.Map;
import java.util.function.Function;

/**
 * Immutable description of a property consisting of a key-value pair.
 * @param <K> type of the key in the key-value pairs stored in the property
 * @param <V> type of the value in the key-value pairs stored in the property
 */
public class MultiplePropertyName<K,V> extends PropertyName<K>{
    private final PropertyType<V> valueType;
    private final Function<String[], Map<? extends K,? extends V>> parser;
    
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
            Function<String[], Map<? extends K,? extends V>> parser){
        super(name, headerName, keyType);
        this.parser = parser;
        this.valueType = valueType;
    }
    
    /**
     * Associates values to parsed simple properties.
     * @param keys unparsed keys
     * @return a map with values associated to the input properties
     */
    public Map<? extends K,? extends V> buildMap(String[] keys){
        return parser.apply(keys);
    }
    
    /** Returns the type of the property values. */
    public PropertyType<V> getValueType(){
        return valueType;
    }
}

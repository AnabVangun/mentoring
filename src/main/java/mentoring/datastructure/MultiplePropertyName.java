package mentoring.datastructure;

import java.util.Map;

/**
 * Immutable description of a property consisting of a key-value pair.
 * @param <K> type of the key in the key-value pairs stored in the property
 * @param <V> type of the value in the key-value pairs stored in the property
 */
public abstract class MultiplePropertyName<K,V> extends PropertyName<K>{
    private final PropertyType<V> valueType;
    
    /**
     * Initialises a new MultiplePropertyName.
     * @param name of the property in {@link Person} objects
     * @param headerName of the property in the header of data files
     * @param keyType expected type of the property key
     * @param valueType expected type of the value key
     */
    MultiplePropertyName(String name, String headerName, PropertyType<K> keyType,
            PropertyType<V> valueType){
        super(name, headerName, keyType);
        this.valueType = valueType;
    }
    
    /**
     * Associates values to parsed simple properties.
     * @param keys unparsed keys
     * @return a map with values associated to the input properties
     */
    public abstract Map<K,V> buildMap(String[] keys);
    
    /** Returns the type of the property values. */
    public PropertyType<V> getValueType(){
        return valueType;
    }
    
    @Override
    public String getStringRepresentation(Person person){
        return person.getPropertyAsMapOf(getName(), getType().getType(), valueType.getType())
                .toString();
    }
}

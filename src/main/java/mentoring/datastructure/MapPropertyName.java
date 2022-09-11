package mentoring.datastructure;

/**
 * Immutable description of a property consisting of a key-value pair.
 */
public final class MapPropertyName extends PropertyName{
    private final PropertyType valueType;
    
    /**
     * Initialises a new MapPropertyName.
     * @param name of the property in {@link Person} objects
     * @param headerName of the property in the header of data files
     * @param keyType expected type of the property key
     * @param valueType expected type of the property value
     */
    public MapPropertyName(String name, String headerName, PropertyType keyType, 
            PropertyType valueType){
        super(name, headerName, keyType);
        this.valueType = valueType;
    }
    
    /**
     * Initialises a new MapPropertyName.
     * @param name of the property in {@link Person} objects and in the header of data files
     * @param keyType expected type of the property key
     * @param valueType expected type of the property value
     */
    public MapPropertyName(String name, PropertyType keyType, PropertyType valueType){
        this(name, name, keyType, valueType);
    }
    
    /** Returns the type of the property value. */
    public PropertyType getValueType(){
        return this.valueType;
    }
}

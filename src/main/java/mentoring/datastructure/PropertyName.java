package mentoring.datastructure;

/**
 * Immutable description of a property.
 * 
 * @param <T> the type of element stored in the property.
 */
public class PropertyName<T> {
    private final String headerName;
    private final String name;
    private final PropertyType<T> type;
    
    /**
     * Initialises a new PropertyName.
     * @param name of the property in {@link Person} objects
     * @param headerName of the property in the header of data files
     * @param type expected type of the property
     */
    public PropertyName(String name, String headerName, PropertyType<T> type){
        this.headerName = headerName;
        this.name = name;
        this.type = type;
    }
    
    /**
     * Initialises a new PropertyName.
     * @param name of the property in {@link Person} objects and in the header of data files
     * @param type expected type of the property
     */
    public PropertyName(String name, PropertyType<T> type){
        this(name, name, type);
    }
    
    /** Returns the name used for this property in {@link Person} objects. */
    public String getName(){
        return name;
    }
    
    /** Returns the name used for this property in the header of data files. */
    public String getHeaderName(){
        return headerName;
    }
    
    public PropertyType<T> getType(){
        return type;
    }
}

package mentoring.datastructure;
//TODO document this class
//TODO replace T with PropertyType
public final class PropertyName {
    private final String headerName;
    private final String name;
    private final PropertyType type;
    
    public PropertyName(String name, String headerName, PropertyType type){
        this.headerName = headerName;
        this.name = name;
        this.type = type;
    }
    
    public PropertyName(String name, PropertyType type){
        this(name, name, type);
    }
    
    public String getName(){
        return name;
    }
    
    public String getHeaderName(){
        return headerName;
    }
    
    public PropertyType getType(){
        return type;
    }
}

package mentoring.datastructure;

public final class PropertyName<T> {
    private final String headerName;
    private final String name;
    private final Class<T> type;
    
    public PropertyName(String name, String headerName, Class<T> type){
        this.headerName = headerName;
        this.name = name;
        this.type = type;
    }
    
    public PropertyName(String name, Class<T> type){
        this(name, name, type);
    }
    
    public String getName(){
        return name;
    }
    
    public String getHeaderName(){
        return headerName;
    }
    
    public Class<T> getType(){
        return type;
    }
}

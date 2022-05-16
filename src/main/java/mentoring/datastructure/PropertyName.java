package mentoring.datastructure;

public final class PropertyName {
    private final String headerName;
    private final String name;
    
    public PropertyName(String name, String headerName){
        this.headerName = headerName;
        this.name = name;
    }
    
    public PropertyName(String name){
        this(name, name);
    }
    
    public String getName(){
        return name;
    }
    
    public String getHeaderName(){
        return headerName;
    }
}

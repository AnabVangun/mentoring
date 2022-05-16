package mentoring.datastructure;
//TODO fix class name: it does not encapsulate a property but only its names.
public final class Property {
    private final String headerName;
    private final String name;
    
    public Property(String name, String headerName){
        this.headerName = headerName;
        this.name = name;
    }
    
    public Property(String name){
        this(name, name);
    }
    
    public String getName(){
        return name;
    }
    
    public String getHeaderName(){
        return headerName;
    }
}

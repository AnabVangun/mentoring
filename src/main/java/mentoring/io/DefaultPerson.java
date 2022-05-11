package mentoring.io;

public class DefaultPerson extends Person{
    private final String name;
    
    public DefaultPerson(String defaultName){
        this.name = defaultName;
    }
    
    @Override
    public String getFullName(){
        return this.name;
    }
}

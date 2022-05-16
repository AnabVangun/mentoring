package mentoring.datastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Class used to build {@link Person} objects. A single builder can build several unrelated objects:
 * the internal state is reinitialised after each creation to make sure that {@link Person} objects
 * remain immutable.
 * This class is not thread-safe.
 */
public final class PersonBuilder {
    public static final String DEFAULT_NAME = "DEFAULT_NAME";
    private Map<String, Integer> integerProperties;
    private Map<String, Boolean> booleanProperties;
    private Map<String, String> stringProperties;
    private Map<String, Set<String>> multipleStringProperties;
    private String name;
    
    public PersonBuilder(){
        clear();
    }
    
    private void clear(){
        integerProperties = new HashMap<>();
        booleanProperties = new HashMap<>();
        stringProperties = new HashMap<>();
        multipleStringProperties = new HashMap<>();
        name = DEFAULT_NAME;
    }
    
    public PersonBuilder withIntegerProperty(String property, int value){
        integerProperties.put(Objects.requireNonNull(property), value);
        return this;
    }
    
    public PersonBuilder withBooleanProperty(String property, boolean value){
        booleanProperties.put(Objects.requireNonNull(property), value);
        return this;
    }
    
    public PersonBuilder withStringProperty(String property, String value){
        stringProperties.put(Objects.requireNonNull(property), Objects.requireNonNull(value));
        return this;
    }
    
    public PersonBuilder withMultipleStringProperty(String property, Set<String> values){
        multipleStringProperties.put(Objects.requireNonNull(property), Set.copyOf(values));
        return this;
    }
    
    public PersonBuilder withFullName(String name){
        this.name = Objects.requireNonNull(name);
        return this;
    }
    
    public Person build(){
        Person result = new Person(integerProperties,
            booleanProperties,
            stringProperties,
            multipleStringProperties,
            name
        );
        clear();
        return result;
    }
}

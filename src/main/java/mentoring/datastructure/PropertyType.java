package mentoring.datastructure;

import java.util.Set;
import java.util.function.Function;

/**
 * All the types of properties that can be used for a {@link Person}.
 */
public enum PropertyType {
    BOOLEAN(Boolean.class, s -> Constants.TRUE_VALUES.contains(s)),
    INTEGER(Integer.class, s -> Integer.parseInt(s)),
    STRING(String.class, Function.identity());
    
    
    private static final class Constants{
        static final Set<String> TRUE_VALUES = Set.of("oui","vrai","yes","true");
    }
    
    private final Class<?> type;
    private final Function<String,? extends Object> parser;
    
    private <T> PropertyType(Class<T> type, Function<String,T> parser){
        this.type = type;
        this.parser = parser;
    }
    
    public Object parse(String entry){
        return parser.apply(entry);
    }
    
    public Class<?> getType(){
        return type;
    }
}

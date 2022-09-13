package mentoring.datastructure;

import java.util.Set;
import java.util.function.Function;

/**
 * All the types of properties that can be used for a {@link Person}.
 * 
 * <p>This class is not an {@code enum} so that it can be parameterized.
 * 
 * 
 * @param <T> class of the objects corresponding to this type.
 */
public final class PropertyType<T> {
    private static final Set<String> TRUE_VALUES = Set.of("oui","vrai","yes","true");
    
    public final static PropertyType<Boolean> BOOLEAN = 
            new PropertyType<>(Boolean.class, s -> TRUE_VALUES.contains(s));
    public final static PropertyType<Integer> INTEGER =
            new PropertyType<>(Integer.class, s -> Integer.parseInt(s));
    public final static PropertyType<String> STRING =
            new PropertyType<>(String.class, Function.identity());
    
    private final Class<T> type;
    private final Function<String,? extends T> parser;
    
    private PropertyType(Class<T> type, Function<String,? extends T> parser){
        /*It is actually necessary to store type: otherwise, retrieving it afterwards is much 
        more complex.*/
        this.type = type;
        this.parser = parser;
    }
    
    public T parse(String entry){
        return parser.apply(entry);
    }
    
    public Class<T> getType(){
        return type;
    }
}

package mentoring.datastructure;

import java.text.Normalizer;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * All the types of properties that can be used for a {@link Person}.
 * 
 * <p>This class is not an {@code enum} so that it can be parameterized.
 * 
 * 
 * @param <T> class of the objects corresponding to this type.
 */
public final class PropertyType<T> {
    private static final Set<String> TRUE_VALUES = Set.of("oui","vrai","yes","true","True");
    
    public final static PropertyType<Boolean> BOOLEAN = 
            new PropertyType<>(Boolean.class, TRUE_VALUES::contains);
    public final static PropertyType<Integer> INTEGER =
            new PropertyType<>(Integer.class, Integer::valueOf);
    public final static PropertyType<String> STRING =
            new PropertyType<>(String.class, Function.identity());
    public final static PropertyType<String> SIMPLIFIED_LOWER_STRING =
            new PropertyType<>(String.class, PropertyType::simplifyString);
    public final static PropertyType<Year> YEAR = 
            new PropertyType<>(Year.class, Year::getYear);
    //When adding a new type, add it to lookup.
    
    private static final Map<String, PropertyType<?>> lookup = Map.of("boolean", BOOLEAN,
            "integer", INTEGER,
            "string", STRING,
            "simplifiedlowerstring", SIMPLIFIED_LOWER_STRING,
            "year", YEAR);
    
    public static PropertyType<?> valueOf(String property){
        String simplified = property.toLowerCase().replace("_","");
        if (! lookup.containsKey(simplified)) {
            throw new IllegalArgumentException("Property %s could not be found, valid values are %s"
                    .formatted(property, lookup.keySet()));
        }
        return lookup.get(simplified);
    }
    
    private final Class<T> type;
    private final Function<String,T> parser;
    
    private PropertyType(Class<T> type, Function<String,T> parser){
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
    
    private final static Pattern SIMPLIFICATION_PATTERN = Pattern.compile("[\\s\\p{M}]");
    
    private static String simplifyString(String input){
        return SIMPLIFICATION_PATTERN.matcher(normalizeDiacriticRepresentation(input))
                .replaceAll("").toLowerCase();
    }
    
    private static String normalizeDiacriticRepresentation(String input){
        return Normalizer.normalize(input, Normalizer.Form.NFKD);
    }
}

package test.tools;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class providing a few static methods used for different tests.
 */
public class TestToolbox {
    private static final Predicate<String> mapPatternPredicate 
            = Pattern.compile("^\\{.+=.+(?:, .+=.+)*\\}$").asPredicate();
    /**
     * Parse a string representation of a Map to generate a new map.
     * @param <K> the type of the map keys
     * @param <V> the type of the map values
     * @param s the string representation to convert into a Map. It must follow the Map.toString()
     *          convention, that is look like {3=6, 5=false}.
     * @param keyType the class of the keys.
     * @param valueType the class of the values.
     * @return a Map object. It is not guaranteed that its string representation will be equal to
     * {@code s} because no guarantee is provided as to the order of the entries.
     */
    public static <K, V> Map<K,V> recreateMap(String s, Class<? extends K> keyType, 
            Class<? extends V> valueType){
        if(!mapPatternPredicate.test(s)){
            throw new IllegalArgumentException("Cannot recreate map from string " + s);
        }
        String data = s.substring(1, s.length() - 1);
        return Arrays.stream(data.split(", "))
                .map(sub -> sub.split("="))
                .collect(Collectors.toMap(
                        a -> keyType.cast(a[0]),  //key
                        a -> valueType.cast(a[1])   //value
        ));
    }
}

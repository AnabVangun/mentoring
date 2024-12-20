package mentoring.configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Utility class providing static methods to compute criteria.
 */
public final class CriteriaToolbox {
    
    final static int SET_PROXIMITY_MULTIPLIER = 100;
    
    private CriteriaToolbox(){
        throw new IllegalStateException(getClass().getCanonicalName() + 
            " should not be instantiated");
    }
    
    public static boolean logicalNotAOrB(boolean a, boolean b){
        return !a || b;
    }
    
    /**
     * Compute a distance between two sets.
     * 
     *<p>As a distance, if the result is 0, the sets are equal. The result is always positive, and
     * symmetry holds. There is no guarantee that the triangular inequality holds.
     * @param <E> common ancestor of the type of elements in both sets
     * @param first one of the two sets
     * @param second the second set
     * @return a non-negative integer
     */
    public static <E> int computeSetDistance(Set<? extends E> first, Set<? extends E> second){
        int commonValues = countCommonValues(first, second);
        int nonCommonValues = first.size() + second.size() - 2 * commonValues;
        return SET_PROXIMITY_MULTIPLIER * nonCommonValues / (commonValues+1);
    }
    
    private static <E> int countCommonValues(Set<? extends E> first, Set< ? extends E> second){
        int commonValues = 0;
        for (E val : first){
            if (second.contains(val)){
                commonValues++;
            }
        }
        return commonValues;
    }
    
    /**
     * Compute a similarity score between two maps representing an ordering of preferences.
     * The keys of the input maps are compared using {@link Object#equals(java.lang.Object) }.
     * The values of the input maps SHOULD all be strictly positive and distinct.
     * No fail-fast mechanism is guaranteed if these assumptions are not enforced by the caller.
     * The score is not a distance:
     * separation, symmetry and the triangle inequality are not guaranteed to be held.
     * <p>
     * On simple inputs, the returned value is 
     * {@code fromFactor*<from value> + toFactor*<to value>} where the two values are each the 
     * value associated with the property with the lowest value in {@code from} that is also 
     * contained in {@code to}.
     * @param <E> type of the keys of the maps
     * @param from first map to compare
     * @param to second map to compare
     * @param fromFactor the higher this value, the higher the impact of the values of {@code from} 
     *      on the result
     * @param toFactor the higher this value, the higher the impact of the values of {@code to} on 
     *      the result
     * @param defaultValue value used if either {@code from} or {@code to} is empty
     * @return a score that is minimal if the two input maps share the same key for their lowest 
     * value.
     */
    public static <E> int computePreferenceMapSimilarityScore(
            Map<? extends E, ? extends Integer> from,
            Map<? extends E, ? extends Integer> to,
            int fromFactor, int toFactor, int defaultValue) {
        Object[] sortedFromKeys = from.keySet().toArray();
        Arrays.sort(sortedFromKeys, (first, second) -> Integer.compare(from.get(first), 
                from.get(second)));
        for (Object fromKey : sortedFromKeys){
            if (to.containsKey(fromKey)){
                Object[] sortedToKeys = to.keySet().toArray();
                Arrays.sort(sortedToKeys, (first, second) -> Integer.compare(to.get(first), 
                        to.get(second)));
                for (Object toKey : sortedToKeys){
                    if (toKey.equals(fromKey)){
                        return fromFactor * from.get(fromKey) + toFactor * to.get(toKey);
                    }
                }
                throw new RuntimeException("Could not find common key " + fromKey + " in map " + to);
            }
        }
        int defaultFromWeight = from.isEmpty() ? defaultValue : 
                from.get(sortedFromKeys[sortedFromKeys.length-1])+1;
        int defaultToWeight = to.isEmpty() ? defaultValue :
                Collections.max(to.values()) + 1;
        return defaultFromWeight*fromFactor + defaultToWeight*toFactor;
    }
    
    /**
     * Computes an exponential cost between two elements of a sorted set of values.
     * @param <E> the type of the elements
     * @param indices the mapping between the elements and their associated numerical value
     * @param first the first element to compare
     * @param second the second element to compare
     * @param baseValue value that is returned if first and second have the same index
     * @return a value that grows exponentially with the difference between the numerical values 
     *         associated with {@code first} and {@code second}
     */
    public static <E> int exponentialDistance(Map<? extends E, ? extends Integer> indices, E first, 
            E second, int baseValue){
        return (int) Math.pow(baseValue, Math.abs(indices.get(first) - indices.get(second)));
    }
}

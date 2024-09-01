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
     * highest value in its map below that associated with the property with the lowest value in 
     * {@code from} that is also contained in {@code to}.
     * @param <E> type of the keys of the maps
     * @param from first map to compare
     * @param to second map to compare
     * @param fromFactor the higher this value, the higher the impact of the values of {@code from} 
     *      on the result
     * @param toFactor the higher this value, the higher the impact of the values of {@code to} on 
     *      the result
     * @param defaultValue value returned if either {@code from} or {@code to} is empty
     * @return a score that is minimal if the two input maps share the same key for their lowest 
     * value.
     */
    public static <E> int computePreferenceMapSimilarityScore(
            Map<? extends E, ? extends Integer> from,
            Map<? extends E, ? extends Integer> to,
            int fromFactor, int toFactor, int defaultValue) {
        if (from.isEmpty() || to.isEmpty()){
            return defaultValue;
        }
        int previousFromValue = 0;
        Object[] sortedFromKeys = from.keySet().toArray();
        Arrays.sort(sortedFromKeys, (first, second) -> Integer.compare(from.get(first), 
                from.get(second)));
        for (Object fromKey : sortedFromKeys){
            if (to.containsKey(fromKey)){
                Object[] sortedToKeys = to.keySet().toArray();
                Arrays.sort(sortedToKeys, (first, second) -> Integer.compare(to.get(first), 
                        to.get(second)));
                int previousToValue = 0;
                for (Object toKey : sortedToKeys){
                    if (toKey.equals(fromKey)){
                        return fromFactor * previousFromValue + toFactor * previousToValue;
                    }
                    else {
                        previousToValue = to.get(toKey);
                    }
                }
                throw new RuntimeException("Could not find common key " + fromKey + " in map " + to);
            }
            previousFromValue = from.get(fromKey);
        }
        return previousFromValue*fromFactor + Collections.max(to.values())*toFactor;
    }
    
    @Deprecated
    public static <E> int computeBrutalAsymetricDistance(Map<? extends E, ? extends Integer> map, 
            Set<? extends E> set){
        int result = 0;
        //1. Si je trouve le premier, alors je renvoie 0
        //2. Si je trouve le deuxième, alors je renvoie 1;
        //3. Si je trouve le troisième, alors je renvoie 2.
        boolean[] found = new boolean[3];
        for(E key: map.keySet()){
            if(set.contains(key)){
                found[map.get(key)] = true;
            }
        }
        if (found[0]){
            return 0;
        } else if (found[1]){
            return 1;
        } else if (found[2]){
            return 2;
        } else {
            return 3;
        }
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

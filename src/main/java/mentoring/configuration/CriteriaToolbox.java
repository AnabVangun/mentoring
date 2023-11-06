package mentoring.configuration;

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
     * the distance between a first set and a second one is the same as the distance between the
     * second set and the first. There is no guarantee that the triangular inequality holds.
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
     * Computes the distance between the map and the set, where the map values represent an 
     * ordering of the map keys.
     * @param <E> type of the keys of the map and the elements of the set
     * @param map that must be evaluated against the set, where each value is the index of the key
     * (on a 0-indexed basis)
     * @param set that must be evaluated against the map
     * @param spikeFactor the higher the spikeFactor, the farther from the mean distance the return
     * value for a given map and a given set will be. {@code spikeFactor} MUST be between 0 and 1,
     * both included.
     * @throws IllegalArgumentException if spikeFactor is out of bounds or if the values in map do
     * not correspond to an indexing of its keys.
     * @return an estimation of how close the map and the set are
     */
    public static <E> int computeWeightedAsymetricMapDistance(
            Map<? extends E, ? extends Integer> map, 
            Set<? extends E> set, double spikeFactor) throws IllegalArgumentException{
        if (spikeFactor < 0 || spikeFactor > 1){
            throw new IllegalArgumentException("Received spikeFactor " + spikeFactor 
                    + ", expected value between 0 and 1.");
        }
        double baseScore = (1.0 - spikeFactor * computeConfigurationScore(map, set));
        return (int) Math.round(SET_PROXIMITY_MULTIPLIER * baseScore);
    }
    
    private static <E> double computeConfigurationScore(Map<? extends E, ? extends Integer> map, 
            Set<? extends E> set){
        double result = 0;
        int size = map.size();
        if (size == 0){
            return 0;
        }
        for (Map.Entry<? extends E, ? extends Integer> entry: map.entrySet()){
            if(entry.getValue() < 0 || entry.getValue() >= size){
                throw new IllegalArgumentException("Map " + map + " contains entry " + entry 
                        + " with value out of bounds, expected between 0 and " + size + ".");
            }
            result += (set.contains(entry.getKey()) ? 1 : -1) 
                    * (1 << (size - 1 - entry.getValue()));
        }
        return result / ((1 << size) - 1);
    }
    
    //TODO document and test
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

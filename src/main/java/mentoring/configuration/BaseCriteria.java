package mentoring.configuration;

import java.util.Set;

public class BaseCriteria {
    public final static int SET_PROXIMITY_MULTIPLIER = 100;
    private BaseCriteria(){
    }
    
    public static boolean logicalNotAOrB(boolean a, boolean b){
        return !a || b;
    }
    
    public static <E> int computeSetProximity(Set<E> first, Set<E> second){
        int commonValues = countCommonValues(first, second);
        int nonCommonValues = first.size() + second.size() - 2 * commonValues;
        return SET_PROXIMITY_MULTIPLIER * nonCommonValues / (commonValues+1);
    }
    
    private static <E> int countCommonValues(Set<E> first, Set<E> second){
        int commonValues = 0;
        for (E val : first){
            if (second.contains(val)){
                commonValues++;
            }
        }
        return commonValues;
    }
}

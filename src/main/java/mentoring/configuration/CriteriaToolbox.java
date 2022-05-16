package mentoring.configuration;

import java.time.LocalDate;
import java.util.Set;

public final class CriteriaToolbox {
    public final static int SET_PROXIMITY_MULTIPLIER = 100;
    public final static int EXECUTIVE_OFFSET = 15;
    
    private CriteriaToolbox(){
        throw new IllegalStateException(getClass().getCanonicalName() + 
            " should not be instantiated");
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
    
    public static int getYear(String formattedYear){
        int currentYear = LocalDate.now().getYear();
        String cursus = formattedYear.substring(0,1);
        int extractedYear = Integer.parseInt(formattedYear.substring(1));
        if (extractedYear < 100){
            if (extractedYear <= currentYear % 100){
                extractedYear += (currentYear / 100) * 100;
            } else {
                extractedYear += (currentYear / 100 - 1) * 100;
            }
        }
        switch(cursus.toUpperCase()){
            case "X":
                break;
            case "E":
                extractedYear -= EXECUTIVE_OFFSET;
                break;
            default:
                throw new UnsupportedOperationException(
                        "Cannot parse year starting with letter " + cursus);
        }
        return extractedYear;
    }
}

package mentoring.configuration;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class providing static methods to compute criteria.
 */
public final class CriteriaToolbox {
    
    final static int SET_PROXIMITY_MULTIPLIER = 100;
    private final static Pattern YEAR_PATTERN = Pattern.compile(
            "^\\s*([\\w&&[^\\d]]*)\\s*(\\d+)\\s*$");
    private final static int CURSUS_GROUP = 1;
    private final static int YEAR_GROUP = 2;
    
    /**
     * Enumeration of the possible prefixes before a promotion number.
     */
    static enum Letter{
        DOCTEUR("D",-8),
        EXECUTIVE("E",-15),
        INGENIEUR("X",0);
        final String prefix;
        final int offset;
        private final static Map<String, Letter> lookup = new HashMap<>();
        static {
            for (Letter s: Letter.values()){
                lookup.put(s.prefix, s);
            }
        }
        
        private Letter(String prefix, int offset){
            this.prefix = prefix;
            this.offset = offset;
        }
        
        public static boolean isValidPrefix(String prefix){
            return lookup.containsKey(prefix);
        }
        
        public static int getOffset(String prefix) throws NullPointerException{
            return lookup.get(prefix).offset;
        }
        
        public static Letter getDefault(){
            return INGENIEUR;
        }
    }
    
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
     * Extracts the year from a String representation of a promotion.
     * @param formattedYear composed of two parts: a possibly-empty prefix of any number of letters 
     * indicating the type of degree of the promotion, and a non-zero number of digits. There might 
     * be any number of whitespace characters before and after each part.
     * @return  the year contained in the string modified by the offset associated with its prefix.
     * @throws IllegalArgumentException when the formattedYear does not conform to its specification
     * or when its prefix is unknown.
     */
    public static int getYear(String formattedYear) throws IllegalArgumentException{
        return getYear(formattedYear, LocalDate.now().getYear());
    }
    
    static int getYear(String formattedYear, int currentYear) throws IllegalArgumentException{
        Matcher matcher = YEAR_PATTERN.matcher(formattedYear);
        if (!matcher.matches()){
            throw new IllegalArgumentException("Could not parse " + formattedYear 
                    + " as a valid year");
        }
        return extractYear(matcher, currentYear) + computeOffset(matcher);
    }
    
    private static int computeOffset(Matcher matcher) throws IllegalArgumentException{
        String cursus = matcher.group(CURSUS_GROUP).toUpperCase();
        if (cursus.equals("")){
            return Letter.getDefault().offset;
        } else if (Letter.isValidPrefix(cursus)){
            return Letter.getOffset(cursus);
        } else {
            throw new IllegalArgumentException("Cursus " + cursus + " in year " + matcher.group(0)
                    + " is not valid");
        }
    }
    
    private static int extractYear(Matcher matcher, int currentYear) 
            throws IllegalArgumentException{
        int extractedYear = Integer.parseInt(matcher.group(YEAR_GROUP));
        int baseNumber = (int) Math.pow(10, matcher.group(YEAR_GROUP).length());
        int result = extractedYear;
        if (currentYear > baseNumber){
            result += inferMostSignificantDigits(extractedYear, currentYear, baseNumber);
        }
        return result;
    }
    
    private static int inferMostSignificantDigits(int extractedYear, int currentYear, 
            int baseNumber){
        int remaining = currentYear % baseNumber;
        int result = currentYear - remaining;
        if (extractedYear > remaining){
            result -= baseNumber;
        }
        return result;
    }
}

package mentoring.configuration;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CriteriaToolbox {
    
    public final static int SET_PROXIMITY_MULTIPLIER = 100;
    private final static Pattern YEAR_PATTERN = Pattern.compile(
            "^\\s*([\\w&&[^\\d]]*)\\s*(\\d+)\\s*$");
    private final static int CURSUS_GROUP = 1;
    private final static int YEAR_GROUP = 2;
    
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
        String cursus = matcher.group(CURSUS_GROUP);
        if (cursus.equals("")){
            return Letter.getDefault().offset;
        } else if (Letter.isValidPrefix(cursus)){
            return Letter.getOffset(cursus);
        } else {
            throw new IllegalArgumentException("Cursus " + cursus + "in year " + matcher.group(0)
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

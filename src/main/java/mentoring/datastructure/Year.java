package mentoring.datastructure;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *Immutable holder of the properties related to the entry year of a student or an alumni.
 */
public final class Year {
    private final Curriculum curriculum;
    private final int entryYear;
    private final int normalizedYear;
    
    private Year(Curriculum curriculum, int entryYear, int normalizedYear){
        this.curriculum = curriculum;
        this.entryYear = entryYear;
        this.normalizedYear = normalizedYear;
    }
    
    /** Get the type of curriculum the student is or was enrolled in. */
    public Curriculum getCurriculum(){
        return curriculum;
    }
    
    /** 
     * Get the calendar year during which the student enrolled. 
     * @return 2012 for an X2012, 2023 for an E2023.
     * @see #getNormalizedYear() for the normalized year.
     */
    public int getEntryYear(){
        return entryYear;
    }
    
    /**
     * Get the normalized year during which the student enrolled.
     * The difference between the entry year and the normalized one is that the latter can include a
     * correction as a very rough estimate of the student's professional experience. Executive 
     * alumni enrol much later in life than bachelor ones so their normalized year is offset from 
     * their entry year to reflect that.
     * @return the entry year offset by a number depending on the type of curriculum.
     */
    public int getNormalizedYear(){
        return normalizedYear;
    }
    
    private final static Pattern YEAR_PATTERN = Pattern.compile(
            "^\\s*([\\w&&[^\\d]]*)\\s*(\\d+)\\s*$");
    private final static int CURRICULUM_GROUP = 1;
    private final static int YEAR_GROUP = 2;
    
    /**
     * Enumeration of the possible prefixes before a promotion number.
     */
    public static enum Curriculum{
        DOCTEUR("D",-8),
        EXECUTIVE("E",-15),
        INGENIEUR("X",0);
        final String prefix;
        final int offset;
        private final static Map<String, Curriculum> lookup = new HashMap<>();
        static {
            for (Curriculum s: Curriculum.values()){
                lookup.put(s.prefix, s);
            }
        }
        
        private Curriculum(String prefix, int offset){
            this.prefix = prefix;
            this.offset = offset;
        }
        
        public static boolean isValidPrefix(String prefix){
            return lookup.containsKey(prefix);
        }
        
        public static int getOffset(String prefix) throws NullPointerException{
            return lookup.get(prefix).offset;
        }
        
        public static Curriculum getDefault(){
            return INGENIEUR;
        }
        
        static Curriculum parseCurriculum(String input){
            return lookup.get(input);
        }
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
    public static Year getYear(String formattedYear) throws IllegalArgumentException{
        return getYear(formattedYear, LocalDate.now().getYear());
    }
    
    static Year getYear(String formattedYear, int currentYear) throws IllegalArgumentException{
        Matcher matcher = YEAR_PATTERN.matcher(formattedYear);
        if (!matcher.matches()){
            throw new IllegalArgumentException("Could not parse " + formattedYear 
                    + " as a valid year");
        }
        Curriculum curriculum = extractLetter(matcher);
        int entryYear = extractYear(matcher, currentYear);
        //TODO: Use Cache to reuse Year objects.
        return new Year(curriculum, entryYear, entryYear + curriculum.offset);
    }
    
    private static Curriculum extractLetter(Matcher matcher) throws IllegalArgumentException{
        String cursus = matcher.group(CURRICULUM_GROUP).toUpperCase();
        if (cursus.equals("")){
            return Curriculum.getDefault();
        } else if (Curriculum.isValidPrefix(cursus)){
            return Curriculum.parseCurriculum(cursus);
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
    
    @Override
    public String toString(){
        return curriculum.prefix + entryYear;
    }
    
    @Override
    public boolean equals(Object other){
        if (!(other instanceof Year cast)){
            return false;
        }
        return curriculum == cast.curriculum && entryYear == cast.entryYear;
    }
    
    @Override
    public int hashCode(){
        return curriculum.hashCode() * 31 + Integer.hashCode(entryYear);
    }
}

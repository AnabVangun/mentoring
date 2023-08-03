package mentoring.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import mentoring.match.Match;

/**
 * The specification for outputting matches in a CSV-like format.
 * 
 * @param <Mentee> type of the first element of a {@link Match}.
 * @param <Mentor> type of the second element of a {@link Match}.
 */
public abstract class ResultConfiguration<Mentee, Mentor> 
        extends Configuration<ResultConfiguration<Mentee, Mentor>>{
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<String> resultHeader;
    
    /**
     * Create a ResultConfiguration instance. This factory method generates instances specialised 
     * for the {@link #getResultLine(mentoring.match.Match) } method.
     * @param <Mentee> type of the first element of a {@link Match}
     * @param <Mentor> type of the second element of a {@link Match}
     * @param configurationName name of the configuration to createForArrayLine
     * @param resultHeader the header of the result generated by this configuration
     * @param resultLineFormatter the function generating a line of result
     * @return the corresponding ResultConfiguration instance.
     */
    public static <Mentee, Mentor> ResultConfiguration<Mentee, Mentor> createForArrayLine(
            String configurationName, List<String> resultHeader,
            Function<Match<Mentee, Mentor>, String[]> resultLineFormatter){
        requireArgumentsNonNull(configurationName, resultHeader, resultLineFormatter);
        return new ArrayResultConfiguration<>(configurationName, resultHeader, resultLineFormatter);
    }
    
    /**
     * Create a ResultConfiguration instance. This factory method generates instances specialised 
     * for the {@link #getResultMap(mentoring.match.Match) } method.
     * @param <Mentee> type of the first element of a {@link Match}
     * @param <Mentor> type of the second element of a {@link Match}
     * @param configurationName name of the configuration to createForArrayLine
     * @param resultHeader the header of the result generated by this configuration
     * @param resultLineFormatter the function generating a line of result
     * @return the corresponding ResultConfiguration instance.
     */
    public static <Mentee, Mentor> ResultConfiguration<Mentee, Mentor> createForMapLine(
            String configurationName, List<String> resultHeader, 
            Function<Match<Mentee, Mentor>, Map<String, String>> resultLineFormatter){
        requireArgumentsNonNull(configurationName, resultHeader, resultLineFormatter);
        return new MapResultConfiguration<>(configurationName, resultHeader, resultLineFormatter);
    }
    
    private static void requireArgumentsNonNull(Object... arguments){
        for (Object arg : arguments){
            Objects.requireNonNull(arg);
        }
    }
    
    private ResultConfiguration(String configurationName, List<String> resultHeader){
        super(configurationName);
        this.resultHeader = List.copyOf(resultHeader);
    }
    
    /**
     * Generates the header of the output.
     * @return a list containing each of the properties expected in the output.
     */
    public List<String> getResultHeader(){
        return resultHeader;
    }
    
    /**
     * Generates the line corresponding to an input {@link Match} object.
     * @param match to print.
     * @return an array containing each of the properties expected in the output, in the same order 
     * as that defined by {@link #getResultHeader() }.
     */
    public abstract String[] getResultLine(Match<Mentee, Mentor> match);
    
    /**
     * Generates the map corresponding to an input {@link Match} object.
     * @param match to print
     * @return a map containing, for each property defined in {@link #getResultHeader() }, 
     * the corresponding output
     */
    public abstract Map<String, String> getResultMap(Match<Mentee, Mentor> match);
    
    private static class ArrayResultConfiguration<Mentee, Mentor>
            extends ResultConfiguration<Mentee, Mentor> {
        private final Function<Match<Mentee, Mentor>, String[]> resultLineFormatter;

        ArrayResultConfiguration(String configurationName, List<String> resultHeader,
                Function<Match<Mentee, Mentor>, String[]> resultLineFormatter){
            super(configurationName, resultHeader);
            this.resultLineFormatter = resultLineFormatter;
        }
        
        @Override
        public String[] getResultLine(Match<Mentee, Mentor> match){
            return resultLineFormatter.apply(match);
        }

        @Override
        public Map<String, String> getResultMap(Match<Mentee, Mentor> match){
            String[] line = getResultLine(match);
            Map<String, String> result = new HashMap<>();
            for (int i = 0; i < line.length; i++){
                result.put(super.resultHeader.get(i), line[i]);
            }
            return result;
        }
    }
    
    private static class MapResultConfiguration<Mentee, Mentor>
            extends ResultConfiguration<Mentee, Mentor> {
        private final Function<Match<Mentee, Mentor>, Map<String, String>> resultLineFormatter;
        
        MapResultConfiguration(String configurationName, List<String> resultHeader,
                Function<Match<Mentee, Mentor>, Map<String, String>> resultLineFormatter){
            super(configurationName, resultHeader);
            this.resultLineFormatter = resultLineFormatter;
        }
        
        @Override
        public String[] getResultLine(Match<Mentee, Mentor> match){
            Map<String, String> map = getResultMap(match);
            String[] result = new String[super.resultHeader.size()];
            for (int i = 0; i < result.length ; i++){
                result[i] = map.get(super.resultHeader.get(i));
            }
            return result;
        }
        
        @Override
        public Map<String, String> getResultMap(Match<Mentee, Mentor> match){
            return resultLineFormatter.apply(match);
        }
    }
}
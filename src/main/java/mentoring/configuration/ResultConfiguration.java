package mentoring.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import mentoring.match.Match;

/**
 * The specification for outputting matches in a CSV-like format.
 * 
 * @param <Mentee> type of the first element of a {@link Match}.
 * @param <Mentor> type of the second element of a {@link Match}.
 */
public final class ResultConfiguration<Mentee, Mentor> {
    //TODO: overload factory to create ResultConfiguration objects optimised for getMapResult()
    private final String configurationName;
    private final List<String> resultHeader;
    private final Function<Match<Mentee, Mentor>, String[]> resultLineFormatter;
    
    /**
     * Create a ResultConfiguration instance. This factory method generates instances specialised 
     * for the {@link #getResultLine(mentoring.match.Match) } method.
     * @param <Mentee> type of the first element of a {@link Match}
     * @param <Mentor> type of the second element of a {@link Match}
     * @param configurationName name of the configuration to create
     * @param resultHeader the header of the result generated by this configuration
     * @param resultLineFormatter the function generating a line of result
     * @return the corresponding ResultConfiguration instance.
     */
    public static <Mentee, Mentor> ResultConfiguration<Mentee, Mentor> 
        create(String configurationName, List<String> resultHeader,
                Function<Match<Mentee, Mentor>, String[]> resultLineFormatter){
        return new ResultConfiguration<>(configurationName, resultHeader, resultLineFormatter);
    }
    
    private ResultConfiguration(String configurationName, List<String> resultHeader,
            Function<Match<Mentee, Mentor>, String[]> resultLineFormatter){
        this.configurationName = configurationName;
        this.resultHeader = resultHeader;
        this.resultLineFormatter = resultLineFormatter;
    }
    
    @Override
    public String toString(){
        return configurationName;
    }
    
    /**
     * Generates the header of the output.
     * @return an array containing each of the properties expected in the output.
     */
    public String[] getResultHeader(){
        return resultHeader.toArray(String[]::new);
    }
    
    /**
     * Generates the line corresponding to an input {@link Match} object.
     * @param match to print.
     * @return an array containing each of the properties expected in the output, in the same order 
     * as that defined by {@link #getResultHeader() }.
     */
    public String[] getResultLine(Match<Mentee, Mentor> match){
        return resultLineFormatter.apply(match);
    }
    
    /**
     * Generates the map corresponding to an input {@link Match} object.
     * @param match to print
     * @return a map containing, for each property defined in {@link #getResultHeader() }, 
     * the corresponding output
     */
    public Map<String, String> getResultMap(Match<Mentee, Mentor> match){
        String[] line = getResultLine(match);
        Map<String, String> result = new HashMap<>();
        for (int i = 0; i < line.length; i++){
            result.put(resultHeader.get(i), line[i]);
        }
        return result;
    }
}

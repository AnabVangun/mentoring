package mentoring.configuration;

import mentoring.match.Match;

/**
 * The specification for outputting matches in a CSV-like format.
 * 
 * @param <Mentee> type of the first element of a {@link Match}.
 * @param <Mentor> type of the second element of a {@link Match}.
 */
public interface ResultConfiguration<Mentee, Mentor> {
    
    /**
     * Generates the header of the output.
     * @return an array containing each of the properties expected in the output.
     */
    String[] getResultHeader();
    
    /**
     * Generates the line corresponding to an input {@link Match} object.
     * @param match to print.
     * @return an array containing each of the properties expected in the output, in the same order 
     * as that defined by {@link #getResultHeader() }.
     */
    String[] getResultLine(Match<Mentee, Mentor> match);
}

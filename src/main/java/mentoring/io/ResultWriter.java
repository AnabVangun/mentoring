package mentoring.io;

import com.opencsv.CSVWriter;
import java.io.Writer;
import java.util.Objects;
import mentoring.configuration.ResultConfiguration;
import mentoring.match.Match;
import mentoring.match.Matches;

/**
 * A writer for CSV files describing optimal matches.
 * 
 * <p>This class is thread-safe as long as the destination {@link Writer} of each call to 
 * {@link #writeMatches(java.io.Writer, mentoring.match.Matches) } is different.
 * 
 * @param <Mentee> type of the first element of the {@link Matches} object to write.
 * @param <Mentor> type of the second element of a {@link Matches} object to write.
 */
public final class ResultWriter<Mentee, Mentor> {
    private final ResultConfiguration<Mentee, Mentor> resultConfiguration;
    
    /**
     * Initialises a new ResultWriter.
     * @param resultConfiguration definition of the output file format.
     */
    public ResultWriter(ResultConfiguration<Mentee, Mentor> resultConfiguration){
        this.resultConfiguration = Objects.requireNonNull(resultConfiguration);
    }
    
    /**
     * Writes the results to the destination in CSV format.
     * @param results matches to write.
     * @param destination writer that will receive the formatted data.
     */
    public void writeMatches(Matches<Mentee, Mentor> results, Writer destination){
        CSVWriter writer = new CSVWriter(destination);
        writer.writeNext(resultConfiguration.getResultHeader());
        for (Match<Mentee, Mentor> match : results){
            writer.writeNext(resultConfiguration.getResultLine(match));
        }
    }
}

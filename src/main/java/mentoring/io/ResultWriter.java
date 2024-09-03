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
 * @param <Mentee> type of the first element of the {@link Matches} object to write
 * @param <Mentor> type of the second element of a {@link Matches} object to write
 */
public final class ResultWriter<Mentee, Mentor> {
    private final ResultConfiguration<Mentee, Mentor> resultConfiguration;
    
    /**
     * Initialises a new ResultWriter.
     * @param resultConfiguration definition of the output file format
     */
    public ResultWriter(ResultConfiguration<Mentee, Mentor> resultConfiguration){
        this.resultConfiguration = Objects.requireNonNull(resultConfiguration);
    }
    
    /**
     * Writes the results to the destination in CSV format.
     * @param results matches to write
     * @param destination writer that will receive the formatted data
     * @param writeHeader if true, the header is written before the data
     */
    public void writeMatches(Matches<Mentee, Mentor> results, Writer destination, 
            boolean writeHeader){
        CSVWriter writer = new CSVWriter(destination);
        if(writeHeader){
            writer.writeNext(resultConfiguration.getResultHeader().toArray(String[]::new));
        }
        for (Match<Mentee, Mentor> match : results){
            Object[] line = resultConfiguration.getResultLine(match);
            String[] formattedLine = new String[line.length];
            for (int i = 0; i < line.length; i++){
                formattedLine[i] = line[i].toString();
            }
            writer.writeNext(formattedLine);
        }
    }
    
    /**
     * Writes the results to the destination in CSV format. The header is written before the data.
     * @param results matches to write
     * @param destination writer that will receive the formatted data
     */
    public void writeMatches(Matches<Mentee, Mentor> results, Writer destination){
        writeMatches(results, destination, true);
    }
}

package mentoring.io;

import com.opencsv.CSVWriter;
import java.io.Writer;
import mentoring.configuration.ResultConfiguration;
import mentoring.match.Match;
import mentoring.match.Matches;

public class ResultWriter<Mentee, Mentor> {
    private final CSVWriter writer;
    private final ResultConfiguration<Mentee, Mentor> resultConfiguration;
    
    public ResultWriter(Writer writer, ResultConfiguration<Mentee, Mentor> resultConfiguration){
        this.writer = new CSVWriter(writer);
        this.resultConfiguration = resultConfiguration;
    }
    
    public void writeMatches(Matches<Mentee, Mentor> results){
        writer.writeNext(resultConfiguration.getResultHeader());
        for (Match<Mentee, Mentor> match : results){
            writer.writeNext(resultConfiguration.getResultLine(match));
        }
    }
}

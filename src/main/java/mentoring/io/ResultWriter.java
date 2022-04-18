package mentoring.io;

import com.opencsv.CSVWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import mentoring.match.Match;
import mentoring.match.Matches;

public class ResultWriter {
    private final Collection<Person> associatedMentor = new HashSet<>();
    private final CSVWriter writer;
    private final static String[] HEADER = new String[]{"Mentoré", "Mentor", "Coût"};
    
    public ResultWriter(Writer writer){
        this.writer = new CSVWriter(writer);
    }
    
    public void writeMatches(Collection<Person> mentees, Collection<Person> mentors,
            Matches<Person, Person> results){
        associatedMentor.clear();
        writer.writeNext(HEADER);
        for (Match<Person, Person> match : results){
            writeMatch(match.getMentee().getFullName(), 
                match.getMentor().getFullName(), 
                match.getCost());
        }
    }
    private void writeMatch(String menteeName, String mentorName, int cost){
        writer.writeNext(new String[]{menteeName, mentorName, Integer.toString(cost)});
    }
}

package mentoring.io;

import com.opencsv.CSVWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;

public class ResultWriter {
    private final Collection<Person> associatedMentor = new HashSet<>();
    private final CSVWriter writer;
    private final static String[] HEADER = new String[]{"Mentoré", "Mentor", "Coût"};
    private final String NO_MENTEE_NAME = "PAS_DE_MENTORÉ";
    private final String NO_MENTOR_NAME = "PAS_DE_MENTOR";
    
    public ResultWriter(Writer writer){
        this.writer = new CSVWriter(writer);
    }
    
    public void writeMatches(Collection<Person> mentees, Collection<Person> mentors,
            Matches<Person, Person> results){
        associatedMentor.clear();
        writer.writeNext(HEADER);
        //TODO this can be greatly simplified if results contains missing associations
        for(Person mentee: mentees){
            if (results.isMentee(mentee)){
                Match<Person, Person> match = results.getMenteeMatch(mentee);
                writeMatch(mentee.getFullName(), match.getMentor().getFullName(), match.getCost());
                associatedMentor.add(match.getMentor());
            } else {
                writeMatch(mentee.getFullName(), NO_MENTOR_NAME, MatchesBuilder.PROHIBITIVE_VALUE);
            }
        }
        for(Person mentor: mentors){
            if (!associatedMentor.contains(mentor)){
                writeMatch(NO_MENTEE_NAME, mentor.getFullName(), MatchesBuilder.PROHIBITIVE_VALUE);
            }
        }
    }
    private void writeMatch(String menteeName, String mentorName, int cost){
        writer.writeNext(new String[]{menteeName, mentorName, Integer.toString(cost)});
    }
}

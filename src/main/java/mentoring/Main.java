package mentoring;

import mentoring.io.Person;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.PersonConfiguration;
import mentoring.io.DefaultPerson;
import mentoring.io.PersonFileParser;
import mentoring.io.ResultWriter;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;

/**
 * Proof of concept of the mentoring application.
 */
public class Main {
    /**
     * Parse an example file representing a mentoring problem and print the resulting assignment.
     * 
     * @param args the command line arguments, ignored for now.
     */
    public static void main(String[] args) {
        System.out.println("Build and solve cost matrix");
        try(
                FileReader menteesFile = new FileReader("resources\\main\\Filleul_Trivial.csv",
                        Charset.forName("utf-8"));
                FileReader mentorsFile = new FileReader("resources\\main\\Mentor_Trivial.csv",
                        Charset.forName("utf-8"));
                //Option 1: output to std.out
                Writer resultDestination = new PrintWriter(System.out, true, 
                    Charset.forName("utf-8"));
                //Option 2: output to file
                //Writer resultDestination = new PrintWriter(
                //        new FileOutputStream("resources\\main\\Results_Trivial.csv"), true, 
                //        Charset.forName("utf-8"));
                ){
            List<Person> mentees = new PersonFileParser(menteesFile, 
                    PersonConfiguration.MENTEE_CONFIGURATION)
                    .parse();
            List<Person> mentors = new PersonFileParser(mentorsFile,
                    PersonConfiguration.MENTOR_CONFIGURATION)
                    .parse();
            CriteriaConfiguration criteria = new CriteriaConfiguration();
            MatchesBuilder<Person, Person> solver = new MatchesBuilder<>(mentees, mentors,
                    criteria.getProgressiveCriteria());
            solver.withNecessaryCriteria(criteria.getNecessaryCriteria())
                .withPlaceholderPersons(new DefaultPerson("PAS DE MENTORÉ"), 
                        new DefaultPerson("PAS DE MENTOR"));
            Matches<Person, Person> results = solver.build();
            
            ResultWriter writer = new ResultWriter(resultDestination);
            writer.writeMatches(mentees, mentors, results);
            resultDestination.flush();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}

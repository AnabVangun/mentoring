package mentoring;

import mentoring.datastructure.Person;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import mentoring.configuration.PojoCriteriaConfiguration;
import mentoring.configuration.PojoPersonConfiguration;
import mentoring.configuration.PojoResultConfiguration;
import mentoring.datastructure.PersonBuilder;
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
        String menteeFilePath;
        String mentorFilePath;
        String destinationFilePath;
        PojoPersonConfiguration menteeConfiguration;
        PojoPersonConfiguration mentorConfiguration;
        PojoCriteriaConfiguration criteriaConfiguration;
        Person defaultMentor = new PersonBuilder().withFullName("PAS DE MENTOR").build();
        Person defaultMentee = new PersonBuilder().withFullName("PAS DE MENTOR�").build();
        switch("REAL"){
            case "TEST":
                menteeFilePath = "resources\\main\\Filleul_Trivial.csv";
                menteeConfiguration = PojoPersonConfiguration.MENTEE_CONFIGURATION;
                mentorFilePath = "resources\\main\\Mentor_Trivial.csv";
                mentorConfiguration = PojoPersonConfiguration.MENTOR_CONFIGURATION;
                criteriaConfiguration = PojoCriteriaConfiguration.CRITERIA_CONFIGURATION;
                destinationFilePath = "resources\\\\main\\\\Results_Trivial.csv";
                break;
            case "REAL":
                menteeFilePath = "..\\..\\..\\AX\\2022_Mentoring\\palmares metiers X_V2_simplifie.csv";
                menteeConfiguration = PojoPersonConfiguration.MENTEE_CONFIGURATION_REAL_DATA;
                mentorFilePath = "..\\..\\..\\AX\\2022_Mentoring\\mentors eleves travail_tlm.csv";
                mentorConfiguration = PojoPersonConfiguration.MENTOR_CONFIGURATION_REAL_DATA;
                criteriaConfiguration = PojoCriteriaConfiguration.CRITERIA_CONFIGURATION_REAL_DATA;
                destinationFilePath = "..\\..\\..\\AX\\2022_Mentoring\\result.csv";
                break;
            default:
                throw new RuntimeException("Invalid value for parameter");
        }
        try(
                FileReader menteesFile = new FileReader(menteeFilePath, Charset.forName("utf-8"));
                FileReader mentorsFile = new FileReader(mentorFilePath, Charset.forName("utf-8"));
                //Option 1: output to std.out
                Writer resultDestination = new PrintWriter(System.out, true, 
                    Charset.forName("utf-8"));
                //Option 2: output to file
                //Writer resultDestination = new PrintWriter(
                //        new FileOutputStream(destinationFilePath), true, Charset.forName("utf-8"));
                ){
            List<Person> mentees = new PersonFileParser(menteeConfiguration).parse(menteesFile);
            List<Person> mentors = new PersonFileParser(mentorConfiguration).parse(mentorsFile);
            PojoCriteriaConfiguration criteria = criteriaConfiguration;
            MatchesBuilder<Person, Person> solver = new MatchesBuilder<>(mentees, mentors,
                    criteria.getProgressiveCriteria());
            solver.withNecessaryCriteria(criteria.getNecessaryCriteria())
                    .withPlaceholderPersons(defaultMentee, defaultMentor);
            Matches<Person, Person> results = solver.build();
            
            ResultWriter<Person,Person> writer = new ResultWriter<>(resultDestination, 
                PojoResultConfiguration.NAMES_EMAILS_AND_SCORE);
            writer.writeMatches(results);
            resultDestination.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
}

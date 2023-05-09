package mentoring;

import java.io.FileNotFoundException;
import mentoring.datastructure.Person;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.PersonConfiguration;
import mentoring.configuration.ResultConfiguration;
import mentoring.io.PersonFileParser;
import mentoring.io.ResultWriter;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;
import mentoring.viewmodel.RunConfiguration;

/**
 * Proof of concept of the mentoring application.
 */
public class Main {
    /**
     * TODO: link GUI to code.
     * 2. Print results in tableview
     * 2b. Internationalize GUI
     * 3. Add export button to save results in file
     * 3b. Add view to choose global configuration through RunConfiguration enum.
     * 3c. Allow manual modification of matching results and reruns excluding the manual modifications.
     * 3d. Add global configuration parameters for magic numbers.
     * 4. Choose mentees file
     * 5. Choose mentees configuration (file or POJO)
     * 6. Choose mentors file
     * 7. Choose mentors configuration (file or POJO)
     * 8. Choose criteria configuration (POJO)
     * 9. Choose result configuration (file or POJO)
     * 10. Make some manual amendments to matches and recompute the rest
     * 11. Alert if configuration is not consistent with data file:
     * for person conf, missing columns in file header
     * 12. Alert if criteria configuration is not consistent with person configuration
     * 13. Choose criteria configuration (file)
     * 14. Modify assignmentproblem to handle cancellation and offer progress status
     */
    private final static Mode MODE = Mode.GUI;
    public static void main(String[] args) {
        switch(MODE){
            case CONSOLE -> runInConsole(args);
            case GUI -> MainApplication.launch(MainApplication.class, args);
        }
    }
    
    /**
     * Parse an example file representing a mentoring problem and print the resulting assignment.
     * 
     * @param args the command line arguments, ignored for now.
     */
    public static void runInConsole(String[] args){
        final RunConfiguration data = RunConfiguration.TEST;
        String destinationFilePath = data.getDestinationFilePath();
        boolean writeToFile = false;
        try (OutputStream outputStream = chooseOutputStream(writeToFile, destinationFilePath)){
            run(System.out, outputStream, data);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void run(PrintStream stateStream, OutputStream outputStream, 
            RunConfiguration data) throws IOException{
        stateStream.println("Build and solve cost matrix");
        //Parse mentees
        String menteeFilePath = data.getMenteeFilePath();
        PersonConfiguration menteeConfiguration = data.getMenteeConfiguration();
        List<Person> mentees = parsePersonList(menteeConfiguration, menteeFilePath);
        Person defaultMentee = data.getDefaultMentee();
        //Parse mentors
        String mentorFilePath = data.getMentorFilePath();
        PersonConfiguration mentorConfiguration = data.getMentorConfiguration();
        List<Person> mentors = parsePersonList(mentorConfiguration, mentorFilePath);
        Person defaultMentor = data.getDefaultMentor();
        //Get criteria configuration
        CriteriaConfiguration<Person, Person> criteriaConfiguration = data.getCriteriaConfiguration();
        //Build matches
        Matches<Person, Person> results = matchMenteesAndMentors(mentees, mentors, 
                criteriaConfiguration, defaultMentee, defaultMentor);
        //Get result configuration
        ResultConfiguration<Person, Person> resultConfiguration = data.getResultConfiguration();
        try(Writer resultDestination = new PrintWriter(outputStream, 
                true, Charset.forName("utf-8"))){
            ResultWriter<Person,Person> writer = new ResultWriter<>(resultConfiguration);
            writer.writeMatches(results, resultDestination);
            resultDestination.flush();
        }
    }
    
    private static List<Person> parsePersonList(PersonConfiguration personConfiguration, 
            String personFilePath) throws IOException {
        try (FileReader personFile = new FileReader(personFilePath, Charset.forName("utf-8"))){
            return new PersonFileParser(personConfiguration).parse(personFile);
        }
    }
    
    private static Matches<Person, Person> matchMenteesAndMentors(List<Person> mentees,
            List<Person> mentors, CriteriaConfiguration<Person, Person> criteriaConfiguration, 
            Person defaultMentee, Person defaultMentor) {
        MatchesBuilder<Person, Person> solver = new MatchesBuilder<>(mentees, mentors,
                    criteriaConfiguration.getProgressiveCriteria());
        solver.withNecessaryCriteria(criteriaConfiguration.getNecessaryCriteria())
                .withPlaceholderPersons(defaultMentee, defaultMentor);
        return solver.build();
    }
    
    static enum Mode {GUI, CONSOLE}
    
    private static OutputStream chooseOutputStream(boolean writeToFile, String destinationFilePath)
            throws FileNotFoundException {
        return (writeToFile ? new FileOutputStream(destinationFilePath) : System.out);
    }
}

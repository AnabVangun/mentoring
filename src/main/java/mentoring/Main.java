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
import mentoring.configuration.PojoCriteriaConfiguration;
import mentoring.configuration.PojoPersonConfiguration;
import mentoring.configuration.PojoResultConfiguration;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.PersonBuilder;
import mentoring.io.Parser;
import mentoring.io.PersonConfigurationParser;
import mentoring.io.PersonFileParser;
import mentoring.io.ResultConfigurationParser;
import mentoring.io.ResultWriter;
import mentoring.io.datareader.YamlReader;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;

/**
 * Proof of concept of the mentoring application.
 */
public class Main {
    /**
     * TODO: link GUI to code.
     * 1. Extract Data to its own file and add methods to get everything that depends on it.
     * 2. Print results in tableview
     * 2b. Internationalize GUI
     * 3. Add export button to save results in file
     * 3b. Add view to choose global configuration through Data enum.
     * 3c. Modify assignmentproblem to handle cancellation and offer progress status
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
     */
    private static final Data DATA = Data.TEST_CONFIGURATION_FILE;
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
        String destinationFilePath;
        switch(DATA) {
            case TEST:
                destinationFilePath = "resources\\main\\Results_Trivial.csv";
                break;
            case TEST_CONFIGURATION_FILE:
                destinationFilePath = "resources\\main\\Results_Trivial.csv";
                break;
            case REAL2023:
                destinationFilePath = "..\\..\\..\\AX\\2023_Mentoring\\Adapter\\20221016_result.csv";
                break;
            default:
                throw new RuntimeException("Invalid value for parameter");
        }
        boolean writeToFile = false;
        try (OutputStream outputStream = chooseOutputStream(writeToFile, destinationFilePath)){
            run(System.out, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void run(PrintStream stateStream, OutputStream outputStream) throws IOException{
        stateStream.println("Build and solve cost matrix");
        PersonConfigurationParser personConfParser = new PersonConfigurationParser(new YamlReader());
        //Parse mentees
        String menteeFilePath = switch(DATA){
            case TEST -> "resources\\main\\Filleul_Trivial.csv";
            case TEST_CONFIGURATION_FILE -> "resources\\main\\Filleul_Trivial.csv";
            case REAL2023 -> "..\\..\\..\\AX\\2023_Mentoring\\Adapter\\20221016_new_eleves.csv";
        };
        PersonConfiguration menteeConfiguration = switch(DATA){
            case TEST -> PojoPersonConfiguration.TEST_CONFIGURATION.getConfiguration();
            case TEST_CONFIGURATION_FILE -> parseConfigurationFile(personConfParser, 
                        "resources\\main\\testPersonConfiguration.yaml");
            case REAL2023 -> PojoPersonConfiguration.MENTEE_CONFIGURATION_2023_DATA
                        .getConfiguration();
        };
        List<Person> mentees = parsePersonList(menteeConfiguration, menteeFilePath);
        Person defaultMentee = new PersonBuilder().withProperty("Email", "")
                .withFullName("PAS DE MENTORÉ").build();
        //Parse mentors
        String mentorFilePath = switch(DATA){
            case TEST -> "resources\\main\\Mentor_Trivial.csv";
            case TEST_CONFIGURATION_FILE -> "resources\\main\\Mentor_Trivial.csv";
            case REAL2023 -> "..\\..\\..\\AX\\2023_Mentoring\\Adapter\\20221016_new_mentors.csv";
        };
        PersonConfiguration mentorConfiguration = switch(DATA){
            case TEST -> PojoPersonConfiguration.TEST_CONFIGURATION.getConfiguration();
            case TEST_CONFIGURATION_FILE -> parseConfigurationFile(personConfParser, 
                        "resources\\main\\testPersonConfiguration.yaml");
            case REAL2023 -> PojoPersonConfiguration.MENTOR_CONFIGURATION_2023_DATA
                        .getConfiguration();
        };
        List<Person> mentors = parsePersonList(mentorConfiguration, mentorFilePath);
        Person defaultMentor = new PersonBuilder().withProperty("Email", "")
                .withFullName("PAS DE MENTOR").build();
        //Get criteria configuration
        CriteriaConfiguration<Person, Person> criteriaConfiguration = switch(DATA){
            case TEST -> PojoCriteriaConfiguration.CRITERIA_CONFIGURATION;
            case TEST_CONFIGURATION_FILE -> PojoCriteriaConfiguration.CRITERIA_CONFIGURATION;
            case REAL2023 -> PojoCriteriaConfiguration.CRITERIA_CONFIGURATION_2023_DATA;
        };
        //Build matches
        Matches<Person, Person> results = matchMenteesAndMentors(mentees, mentors, 
                criteriaConfiguration, defaultMentee, defaultMentor);
        //Get result configuration
        ResultConfiguration<Person, Person> resultConfiguration = switch(DATA){
            case TEST -> PojoResultConfiguration.NAMES_AND_SCORE.getConfiguration();
            case TEST_CONFIGURATION_FILE -> parseConfigurationFile(
                    new ResultConfigurationParser(new YamlReader()), 
                        "resources\\main\\testResultConfiguration.yaml");
            case REAL2023 -> PojoResultConfiguration.NAMES_EMAILS_AND_SCORE.getConfiguration();
        };
        try(Writer resultDestination = new PrintWriter(outputStream, 
                true, Charset.forName("utf-8"))){
            ResultWriter<Person,Person> writer = new ResultWriter<>(resultConfiguration);
            writer.writeMatches(results, resultDestination);
            resultDestination.flush();
        }
    }
    
    private static <T> T parseConfigurationFile(Parser<T> parser, String filePath) throws IOException{
        try (FileReader configurationFile = new FileReader(filePath, Charset.forName("utf-8"))){
            return parser.parse(configurationFile);
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
    
    public static enum Data {TEST, TEST_CONFIGURATION_FILE, REAL2023}
    static enum Mode {GUI, CONSOLE}
    
    private static OutputStream chooseOutputStream(boolean writeToFile, String destinationFilePath)
            throws FileNotFoundException {
        return (writeToFile ? new FileOutputStream(destinationFilePath) : System.out);
    }
}

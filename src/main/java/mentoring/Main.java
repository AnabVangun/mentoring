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
    public static void main(String[] args) {
        Mode mode = Mode.CONSOLE;
        switch(mode){
            case CONSOLE -> runInConsole(args);
            case GUI -> runGui(args);
        }
    }
    
    /**
     * Parse an example file representing a mentoring problem and print the resulting assignment.
     * 
     * @param args the command line arguments, ignored for now.
     */
    public static void runInConsole(String[] args){
        String destinationFilePath;
        Data data = Data.TEST_CONFIGURATION_FILE;
        switch(data) {
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
        try {
            OutputStream outputStream = chooseOutputStream(writeToFile, destinationFilePath);
            run(System.out, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static void runGui(String[] args){
        MainFxGui.main(args);
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    public static void run(PrintStream stateStream, OutputStream outputStream){
        stateStream.println("Build and solve cost matrix");
        String menteeFilePath;
        String mentorFilePath;
        Data data = Data.TEST_CONFIGURATION_FILE;
        PersonConfiguration menteeConfiguration = null;
        PersonConfiguration mentorConfiguration = null;
        String menteeConfigurationFilePath = "";
        String mentorConfigurationFilePath = "";
        String resultConfigurationFilePath = "";
        CriteriaConfiguration<Person, Person> criteriaConfiguration = null;
        ResultConfiguration<Person, Person> resultConfiguration = null;
        Person defaultMentor = new PersonBuilder().withProperty("Email", "")
                .withFullName("PAS DE MENTOR").build();
        Person defaultMentee = new PersonBuilder().withProperty("Email", "")
                .withFullName("PAS DE MENTORÉ").build();
        PersonConfigurationParser personConfParser = new PersonConfigurationParser(new YamlReader());
        ResultConfigurationParser resultConfParser = new ResultConfigurationParser(new YamlReader());
        switch(data){
            case TEST:
                menteeFilePath = "resources\\main\\Filleul_Trivial.csv";
                menteeConfiguration = PojoPersonConfiguration.TEST_CONFIGURATION.getConfiguration();
                mentorFilePath = "resources\\main\\Mentor_Trivial.csv";
                mentorConfiguration = PojoPersonConfiguration.TEST_CONFIGURATION.getConfiguration();
                criteriaConfiguration = PojoCriteriaConfiguration.CRITERIA_CONFIGURATION;
                resultConfiguration = PojoResultConfiguration.NAMES_AND_SCORE.getConfiguration();
                break;
            case TEST_CONFIGURATION_FILE:
                menteeFilePath = "resources\\main\\Filleul_Trivial.csv";
                menteeConfigurationFilePath = "resources\\main\\testPersonConfiguration.yaml";
                mentorFilePath = "resources\\main\\Mentor_Trivial.csv";
                mentorConfigurationFilePath = "resources\\main\\testPersonConfiguration.yaml";
                criteriaConfiguration = PojoCriteriaConfiguration.CRITERIA_CONFIGURATION;
                resultConfigurationFilePath = "resources\\main\\testResultConfiguration.yaml";
                break;
            case REAL2023:
                menteeFilePath = "..\\..\\..\\AX\\2023_Mentoring\\Adapter\\20221016_new_eleves.csv";
                menteeConfiguration = PojoPersonConfiguration.MENTEE_CONFIGURATION_2023_DATA
                        .getConfiguration();
                mentorFilePath = "..\\..\\..\\AX\\2023_Mentoring\\Adapter\\20221016_new_mentors.csv";
                mentorConfiguration = PojoPersonConfiguration.MENTOR_CONFIGURATION_2023_DATA
                        .getConfiguration();
                criteriaConfiguration = PojoCriteriaConfiguration.CRITERIA_CONFIGURATION_2023_DATA;
                resultConfiguration = PojoResultConfiguration.NAMES_EMAILS_AND_SCORE.getConfiguration();
                break;
            default:
                throw new RuntimeException("Invalid value for parameter");
        }
        try(
                FileReader menteesFile = new FileReader(menteeFilePath, Charset.forName("utf-8"));
                FileReader mentorsFile = new FileReader(mentorFilePath, Charset.forName("utf-8"));
                Writer resultDestination = new PrintWriter(outputStream,
                        true, Charset.forName("utf-8"));
                FileReader menteeConfigurationFile = 
                        menteeConfiguration == null 
                        ? new FileReader(menteeConfigurationFilePath, Charset.forName("utf-8"))
                        : menteesFile;
                FileReader mentorConfigurationFile =
                        mentorConfiguration == null
                        ? new FileReader(mentorConfigurationFilePath, Charset.forName("utf-8"))
                        : mentorsFile;
                FileReader resultConfigurationFile =
                        resultConfiguration == null
                        ? new FileReader(resultConfigurationFilePath, Charset.forName("utf-8"))
                        : null
                ){
            if (menteeConfiguration == null){
                menteeConfiguration = personConfParser.parse(menteeConfigurationFile);
            }
            if (mentorConfiguration == null){
                mentorConfiguration = personConfParser.parse(mentorConfigurationFile);
            }
            if (resultConfiguration == null){
                resultConfiguration = resultConfParser.parse(resultConfigurationFile);
            }
            if (criteriaConfiguration == null){
                throw new NullPointerException("Forgot to read criteria configuration from file");
            }
            List<Person> mentees = parsePersonList(menteeConfiguration, menteesFile);
            List<Person> mentors = parsePersonList(mentorConfiguration, mentorsFile);
            Matches<Person, Person> results = matchMenteesAndMentors(mentees, mentors, 
                    criteriaConfiguration, defaultMentee, defaultMentor);
            
            ResultWriter<Person,Person> writer = new ResultWriter<>(resultConfiguration);
            writer.writeMatches(results, resultDestination);
            resultDestination.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    private static List<Person> parsePersonList(PersonConfiguration personConfiguration, 
            FileReader dataFile) throws IOException {
        return new PersonFileParser(personConfiguration).parse(dataFile);
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
    
    static enum Data {TEST, TEST_CONFIGURATION_FILE, REAL2023}
    static enum Mode {GUI, CONSOLE}
    
    private static OutputStream chooseOutputStream(boolean writeToFile, String destinationFilePath)
            throws FileNotFoundException {
        return (writeToFile ? new FileOutputStream(destinationFilePath) : System.out);
    }
}

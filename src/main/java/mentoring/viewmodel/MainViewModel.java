package mentoring.viewmodel;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import mentoring.Main;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.PersonConfiguration;
import mentoring.configuration.PojoCriteriaConfiguration;
import mentoring.configuration.PojoPersonConfiguration;
import mentoring.configuration.PojoResultConfiguration;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
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
 * ViewModel responsible for handling the main window of the application.
 */
public class MainViewModel {
    private final ReadOnlyStringWrapper privateStatus = new ReadOnlyStringWrapper();
    public final ReadOnlyStringProperty status = privateStatus.getReadOnlyProperty();
    
    /**
     * Run the application: get the relevant data, make matches and update the {@code status} 
     * property.
     * @param executor Executor service that will receive the task to perform.
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> makeMatches(ExecutorService executor){
        return executor.submit(() -> {
            try{
                makeMatchesWithException();
            } catch (IOException e){
                privateStatus.setValue(Arrays.toString(e.getStackTrace()));
            }
        });
    }
    
    private void makeMatchesWithException() throws IOException{
        Main.Data data = Main.Data.TEST;
        privateStatus.setValue("Fetching data...");
        PersonConfigurationParser personConfParser = new PersonConfigurationParser(new YamlReader());
        //Parse mentees
        String menteeFilePath = switch(data){
            case TEST -> "resources\\main\\Filleul_Trivial.csv";
            case TEST_CONFIGURATION_FILE -> "resources\\main\\Filleul_Trivial.csv";
            case REAL2023 -> "..\\..\\..\\AX\\2023_Mentoring\\Adapter\\20221016_new_eleves.csv";
        };
        PersonConfiguration menteeConfiguration = switch(data){
            case TEST -> PojoPersonConfiguration.TEST_CONFIGURATION.getConfiguration();
            case TEST_CONFIGURATION_FILE -> parseConfigurationFile(personConfParser, 
                        "resources\\main\\testPersonConfiguration.yaml");
            case REAL2023 -> PojoPersonConfiguration.MENTEE_CONFIGURATION_2023_DATA
                        .getConfiguration();
        };
        List<Person> mentees = parsePersonList(menteeConfiguration, menteeFilePath);
        Person defaultMentee = new PersonBuilder().withProperty("Email", "")
                .withFullName("PAS DE MENTORÉ").build();
        privateStatus.setValue(privateStatus.get() + "\nMentees OK");
        //Parse mentors
        String mentorFilePath = switch(data){
            case TEST -> "resources\\main\\Mentor_Trivial.csv";
            case TEST_CONFIGURATION_FILE -> "resources\\main\\Mentor_Trivial.csv";
            case REAL2023 -> "..\\..\\..\\AX\\2023_Mentoring\\Adapter\\20221016_new_mentors.csv";
        };
        PersonConfiguration mentorConfiguration = switch(data){
            case TEST -> PojoPersonConfiguration.TEST_CONFIGURATION.getConfiguration();
            case TEST_CONFIGURATION_FILE -> parseConfigurationFile(personConfParser, 
                        "resources\\main\\testPersonConfiguration.yaml");
            case REAL2023 -> PojoPersonConfiguration.MENTOR_CONFIGURATION_2023_DATA
                        .getConfiguration();
        };
        List<Person> mentors = parsePersonList(mentorConfiguration, mentorFilePath);
        Person defaultMentor = new PersonBuilder().withProperty("Email", "")
                .withFullName("PAS DE MENTOR").build();
        privateStatus.setValue(privateStatus.get() + "\nMentors OK");
        //Get criteria configuration
        CriteriaConfiguration<Person, Person> criteriaConfiguration = switch(data){
            case TEST -> PojoCriteriaConfiguration.CRITERIA_CONFIGURATION;
            case TEST_CONFIGURATION_FILE -> PojoCriteriaConfiguration.CRITERIA_CONFIGURATION;
            case REAL2023 -> PojoCriteriaConfiguration.CRITERIA_CONFIGURATION_2023_DATA;
        };
        privateStatus.setValue(privateStatus.get() + "\nCriteria OK\nSolving matrix...");
        //Build matches
        Matches<Person, Person> results = matchMenteesAndMentors(mentees, mentors, 
                criteriaConfiguration, defaultMentee, defaultMentor);
        privateStatus.setValue(privateStatus.get() + "\nMatrix solved.");
        //Get result configuration
        ResultConfiguration<Person, Person> resultConfiguration = switch(data){
            case TEST -> PojoResultConfiguration.NAMES_AND_SCORE.getConfiguration();
            case TEST_CONFIGURATION_FILE -> parseConfigurationFile(
                    new ResultConfigurationParser(new YamlReader()), 
                        "resources\\main\\testResultConfiguration.yaml");
            case REAL2023 -> PojoResultConfiguration.NAMES_EMAILS_AND_SCORE.getConfiguration();
        };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try(Writer resultDestination = new PrintWriter(out, 
                true, Charset.forName("utf-8"))){
            ResultWriter<Person,Person> writer = new ResultWriter<>(resultConfiguration);
            writer.writeMatches(results, resultDestination);
            resultDestination.flush();
        }
        privateStatus.setValue(out.toString(Charset.forName("utf-8")));
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
}

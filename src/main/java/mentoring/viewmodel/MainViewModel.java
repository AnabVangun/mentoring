package mentoring.viewmodel;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javax.inject.Inject;
import mentoring.concurrency.ConcurrencyHandler;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.PersonConfiguration;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.io.PersonFileParser;
import mentoring.io.ResultWriter;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

/**
 * ViewModel responsible for handling the main window of the application.
 */
public class MainViewModel {
    private final ConcurrencyHandler executor;
    private final ReadOnlyStringWrapper privateStatus = new ReadOnlyStringWrapper();
    public final ReadOnlyStringProperty status = privateStatus.getReadOnlyProperty();
    
    /**
     * Create a new {@code MainViewModel}.
     * @param executor Executor service that will receive the task to run the application.
     */
    @Inject
    MainViewModel(ConcurrencyHandler executor){
        this.executor = executor;
    }
    
    /**
     * Run the application: get the relevant data, make matches and update the {@code status} 
     * property.
     * @param resultVM the ViewModel to update with the results
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> makeMatches(PersonMatchesViewModel resultVM){
        return executor.submit(() -> {
            try{
                makeMatchesWithException(resultVM);
            } catch (IOException e){
                privateStatus.setValue(Arrays.toString(e.getStackTrace()));
            }
        });
    }
    
    private void makeMatchesWithException(PersonMatchesViewModel resultVM) throws IOException{
        RunConfiguration data = RunConfiguration.TEST;
        privateStatus.setValue("Fetching data...");
        //Parse mentees
        List<Person> mentees = parsePersonList(data.getMenteeConfiguration(), 
                data.getMenteeFilePath());
        Person defaultMentee = data.getDefaultMentee();
        privateStatus.setValue(privateStatus.get() + "\nMentees OK");
        //Parse mentors
        String mentorFilePath = data.getMentorFilePath();
        PersonConfiguration mentorConfiguration = data.getMentorConfiguration();
        List<Person> mentors = parsePersonList(mentorConfiguration, mentorFilePath);
        Person defaultMentor = data.getDefaultMentor();
        privateStatus.setValue(privateStatus.get() + "\nMentors OK");
        //Get criteria configuration
        CriteriaConfiguration<Person, Person> criteriaConfiguration = 
                data.getCriteriaConfiguration();
        privateStatus.setValue(privateStatus.get() + "\nCriteria OK\nSolving matrix...");
        //Build matches
        Matches<Person, Person> results = matchMenteesAndMentors(mentees, mentors, 
                criteriaConfiguration, defaultMentee, defaultMentor);
        privateStatus.setValue(privateStatus.get() + "\nMatrix solved.");
        //Get result configuration
        ResultConfiguration<Person, Person> resultConfiguration = data.getResultConfiguration();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try(Writer resultDestination = new PrintWriter(out, 
                true, Charset.forName("utf-8"))){
            ResultWriter<Person,Person> writer = new ResultWriter<>(resultConfiguration);
            writer.writeMatches(results, resultDestination);
            resultDestination.flush();
        }
        privateStatus.setValue(out.toString(Charset.forName("utf-8")));
        resultVM.update(resultConfiguration, results);
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

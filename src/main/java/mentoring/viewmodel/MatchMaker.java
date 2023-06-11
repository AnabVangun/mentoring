package mentoring.viewmodel;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import javafx.concurrent.Task;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.PersonConfiguration;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.io.PersonFileParser;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

/**
 * Class used to make matches and update an input view model.
 */
class MatchMaker extends Task<Void> {
    
    private final PersonMatchesViewModel resultVM;
    private ResultConfiguration<Person, Person> resultConfiguration;
    private Matches<Person, Person> results;

    /**
     * Initialise a {@code MatchMaker} object.
     * @param resultVM the view model that will be updated when the task completes.
     */
    MatchMaker(PersonMatchesViewModel resultVM) {
        this.resultVM = resultVM;
    }

    @Override
    protected Void call() throws Exception {
        RunConfiguration data = RunConfiguration.TEST;
        try {
            results = makeMatchesWithException(data);
            resultConfiguration = getResultConfiguration(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        resultVM.update(resultConfiguration, results);
    }

    private static Matches<Person, Person> makeMatchesWithException(RunConfiguration data) 
            throws IOException {
        //Parse mentees
        List<Person> mentees = parsePersonList(data.getMenteeConfiguration(), 
                data.getMenteeFilePath());
        Person defaultMentee = data.getDefaultMentee();
        //Parse mentors
        String mentorFilePath = data.getMentorFilePath();
        PersonConfiguration mentorConfiguration = data.getMentorConfiguration();
        List<Person> mentors = parsePersonList(mentorConfiguration, mentorFilePath);
        Person defaultMentor = data.getDefaultMentor();
        //Get criteria configuration
        CriteriaConfiguration<Person, Person> criteriaConfiguration = 
                data.getCriteriaConfiguration();
        //Build matches
        return matchMenteesAndMentors(mentees, mentors, criteriaConfiguration, defaultMentee, 
                defaultMentor);
    }

    private static ResultConfiguration<Person, Person> getResultConfiguration(RunConfiguration data)
            throws IOException {
        return data.getResultConfiguration();
    }

    private static List<Person> parsePersonList(PersonConfiguration personConfiguration, 
            String personFilePath) throws IOException {
        try (final FileReader personFile = 
                new FileReader(personFilePath, Charset.forName("utf-8"))) {
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

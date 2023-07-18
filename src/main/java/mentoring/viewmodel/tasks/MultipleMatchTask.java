package mentoring.viewmodel.tasks;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.concurrent.Task;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

public class MultipleMatchTask extends Task<Void> {
    
    private final PersonMatchesViewModel resultVM;
    private final RunConfiguration data;
    private final List<Person> mentees;
    private final List<Person> mentors;
    private ResultConfiguration<Person, Person> resultConfiguration;
    private Matches<Person, Person> results;

    /**
     * Initialise a MultipleMatchTask object.
     * @param resultVM the view model that will be updated when the task completes
     * @param data where to get data from
     * @param mentees the list of mentees to match
     * @param mentors the list of mentors to match
     */
    public MultipleMatchTask(PersonMatchesViewModel resultVM, RunConfiguration data, List<Person> mentees, 
            List<Person> mentors) {
        this.resultVM = resultVM;
        this.data = data;
        this.mentees = mentees;
        this.mentors = mentors;
    }

    @Override
    protected Void call() throws Exception {
        List<Person> filteredMentees = filterAvailablePerson(mentees, resultVM.getTransferredItems(),
                t -> t.getMentee());
        List<Person> filteredMentors = filterAvailablePerson(mentors, resultVM.getTransferredItems(),
                t -> t.getMentor());
        results = makeMatchesWithException(data, filteredMentees, filteredMentors);
        resultConfiguration = data.getResultConfiguration();
        return null;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        resultVM.update(resultConfiguration, results);
    }

    private static List<Person> filterAvailablePerson(List<Person> toFilter, 
            List<PersonMatchViewModel> matches, 
            Function<Match<Person, Person>, Person> personExtractor) {
        Set<Person> unavailable = matches.stream()
                .map(element -> personExtractor.apply(element.getData()))
                .collect(Collectors.toSet());
        return toFilter.stream().filter(e -> !unavailable.contains(e)).toList();
    }

    private static Matches<Person, Person> makeMatchesWithException(RunConfiguration data, 
            List<Person> mentees, List<Person> mentors) throws IOException {
        Person defaultMentee = data.getDefaultMentee();
        Person defaultMentor = data.getDefaultMentor();
        //Get criteria configuration
        CriteriaConfiguration<Person, Person> criteriaConfiguration = 
                data.getCriteriaConfiguration();
        //Build matches
        return matchMenteesAndMentors(mentees, mentors, criteriaConfiguration, defaultMentee, 
                defaultMentor);
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

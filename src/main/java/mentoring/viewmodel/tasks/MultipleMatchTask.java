package mentoring.viewmodel.tasks;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.concurrent.Task;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

public class MultipleMatchTask extends Task<Void> {
    
    private final PersonMatchesViewModel resultVM;
    private final PersonMatchesViewModel excludedMatchesVM;
    private final ConfigurationPickerViewModel<CriteriaConfiguration<Person,Person>> criteriaVM;
    private final List<Person> mentees;
    private final List<Person> mentors;
    private Matches<Person, Person> results;

    /**
     * Initialise a MultipleMatchTask object.
     * @param resultVM the ViewModel that will be updated when the task completes
     * @param excludedMatchesVM the optional ViewModel encapsulating matches that should be excluded
     *      from the match-making process, this argument MAY be null
     * @param criteriaVM the ViewModel that will be used to get the configuration
     * @param mentees the list of mentees to match
     * @param mentors the list of mentors to match
     */
    public MultipleMatchTask(PersonMatchesViewModel resultVM, PersonMatchesViewModel excludedMatchesVM,
            ConfigurationPickerViewModel<CriteriaConfiguration<Person,Person>> criteriaVM, 
            List<Person> mentees, 
            List<Person> mentors) {
        //TODO check in tests that constructor fail on null input like the other tasks
        this.resultVM = Objects.requireNonNull(resultVM);
        this.excludedMatchesVM = excludedMatchesVM;
        this.criteriaVM = Objects.requireNonNull(criteriaVM);
        this.mentees = Objects.requireNonNull(mentees);
        this.mentors = Objects.requireNonNull(mentors);
    }

    @Override
    protected Void call() throws Exception {
        List<Person> filteredMentees;
        List<Person> filteredMentors;
        if (excludedMatchesVM == null){
            filteredMentees = mentees;
            filteredMentors = mentors;
        } else {
            filteredMentees = filterAvailablePerson(mentees, excludedMatchesVM.getContent(),
                t -> t.getMentee());
            filteredMentors = filterAvailablePerson(mentors, excludedMatchesVM.getContent(),
                t -> t.getMentor());
        }
        results = makeMatchesWithException(criteriaVM, filteredMentees, filteredMentors);
        return null;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        resultVM.setAll(results);
    }

    private static List<Person> filterAvailablePerson(List<Person> toFilter, 
            List<PersonMatchViewModel> matches, 
            Function<Match<Person, Person>, Person> personExtractor) {
        Set<Person> unavailable = matches.stream()
                .map(element -> personExtractor.apply(element.getData()))
                .collect(Collectors.toSet());
        return toFilter.stream().filter(e -> !unavailable.contains(e)).toList();
    }

    private static Matches<Person, Person> makeMatchesWithException(
            ConfigurationPickerViewModel<CriteriaConfiguration<Person, Person>> criteriaVM,
            List<Person> mentees, List<Person> mentors) throws IOException {
        /*FIXME: defaultMentee and defaultMentor should be configured somewhere
        (probably in result configuration)
        */
        Person defaultMentee = new PersonBuilder().withProperty("Email", "")
                .withFullName("PAS DE MENTORÉ").build();
        Person defaultMentor = new PersonBuilder().withProperty("Email", "")
                .withFullName("PAS DE MENTOR").build();
        //Get criteria configuration
        CriteriaConfiguration<Person, Person> criteriaConfiguration = 
                criteriaVM.getConfiguration();
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

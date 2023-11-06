package mentoring.viewmodel.tasks;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;
import mentoring.match.NecessaryCriterion;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.datastructure.ForbiddenMatchListViewModel;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

public class MultipleMatchTask extends AbstractTask<Void> {
    //TODO this class has become too complex, refactor to simplify and refactor test class accordingly
    /*
    In MainViewModel, when configuration is selected, generate a MatchesBuilder
        <-- not so easy, need to fuse the results of PersonGetter and ConfigurationGetter
    Modify forbiddenMatchTask and forbiddenMatchRemovalTask to modify MatchesBuilder
    Modify SingleMatchTask to use MatchesBuilder
    Here, Replace mentees, mentors, criteriaVM and forbiddenMatchesVM with a MatchesBuilder
    */
    private final PersonMatchesViewModel resultVM;
    private final PersonMatchesViewModel excludedMatchesVM;
    private final ConfigurationPickerViewModel<CriteriaConfiguration<Person,Person>> criteriaVM;
    private final ForbiddenMatchListViewModel forbiddenMatchesVM;
    private final List<Person> mentees;
    private final List<Person> mentors;
    private Matches<Person, Person> results;

    /**
     * Initialise a MultipleMatchTask object.
     * @param resultVM the ViewModel that will be updated when the task completes
     * @param excludedMatchesVM an optional ViewModel encapsulating matches that should be excluded
     *      from the match-making process, this argument MAY be null
     * @param criteriaVM the ViewModel that will be used to get the configuration
     * @param forbiddenMatchesVM an optional ViewModel encapsulating a list of forbidden matches
     * @param mentees the list of mentees to match
     * @param mentors the list of mentors to match
     * @param callback the method to call when the task has run
     */
    public MultipleMatchTask(PersonMatchesViewModel resultVM, PersonMatchesViewModel excludedMatchesVM,
            ConfigurationPickerViewModel<CriteriaConfiguration<Person,Person>> criteriaVM, 
            ForbiddenMatchListViewModel forbiddenMatchesVM,
            List<Person> mentees, 
            List<Person> mentors,
            TaskCompletionCallback<? super Void> callback) {
        super(callback);
        this.resultVM = Objects.requireNonNull(resultVM);
        this.excludedMatchesVM = excludedMatchesVM;
        this.criteriaVM = Objects.requireNonNull(criteriaVM);
        this.forbiddenMatchesVM = Objects.requireNonNullElseGet(forbiddenMatchesVM, 
                () -> new ForbiddenMatchListViewModel());
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
        results = makeMatchesWithException(criteriaVM, forbiddenMatchesVM, 
                filteredMentees, filteredMentors);
        return null;
    }

    @Override
    protected void specificActionOnSuccess() {
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
            ForbiddenMatchListViewModel forbiddenMatchesVM,
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
        return matchMenteesAndMentors(mentees, mentors, criteriaConfiguration, 
                forbiddenMatchesVM.getCriterion(), defaultMentee, 
                defaultMentor);
    }

    private static Matches<Person, Person> matchMenteesAndMentors(List<Person> mentees, 
            List<Person> mentors, CriteriaConfiguration<Person, Person> criteriaConfiguration,
            NecessaryCriterion<Person, Person> extraNecessaryCriterion,
            Person defaultMentee, Person defaultMentor) {
        MatchesBuilder<Person, Person> solver = new MatchesBuilder<>(mentees, mentors, 
                criteriaConfiguration.getProgressiveCriteria());
        Collection<NecessaryCriterion<Person,Person>> completeNecessaryCriteria =
                new LinkedList<>(criteriaConfiguration.getNecessaryCriteria());
        completeNecessaryCriteria.add(extraNecessaryCriterion);
        solver.withNecessaryCriteria(completeNecessaryCriteria)
                .withPlaceholderPersons(defaultMentee, defaultMentor);
        return solver.build();
    }
}

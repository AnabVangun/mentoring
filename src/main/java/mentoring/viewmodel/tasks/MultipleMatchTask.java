package mentoring.viewmodel.tasks;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import mentoring.datastructure.Person;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;
import mentoring.match.MatchesBuilderHandler;
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
    private final List<Person> mentees;
    private final List<Person> mentors;
    private final MatchesBuilderHandler<Person, Person> builderHandler;
    private Matches<Person, Person> results;

    /**
     * Initialise a MultipleMatchTask object.
     * @param resultVM the ViewModel that will be updated when the task completes
     * @param excludedMatchesVM an optional ViewModel encapsulating matches that should be excluded
     *      from the match-making process, this argument MAY be null
     * @param builderHandler the handler that will supply the {@link MatchesBuilder}
     * @param mentees the list of mentees to match
     * @param mentors the list of mentors to match
     * @param callback the method to call when the task has run
     */
    public MultipleMatchTask(PersonMatchesViewModel resultVM, 
            PersonMatchesViewModel excludedMatchesVM,
            MatchesBuilderHandler<Person, Person> builderHandler,
            List<Person> mentees, 
            List<Person> mentors,
            TaskCompletionCallback<? super Void> callback) {
        super(callback);
        this.resultVM = Objects.requireNonNull(resultVM);
        this.excludedMatchesVM = excludedMatchesVM;
        this.builderHandler = Objects.requireNonNull(builderHandler);
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
        results = builderHandler.get().build(filteredMentees, filteredMentors);
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
}

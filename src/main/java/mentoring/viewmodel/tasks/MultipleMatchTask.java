package mentoring.viewmodel.tasks;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.match.MatchesBuilder;
import mentoring.match.MatchesBuilderHandler;
import mentoring.viewmodel.datastructure.MatchStatus;
import mentoring.viewmodel.datastructure.PersonListViewModel;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.datastructure.PersonViewModel;

public class MultipleMatchTask extends AbstractTask<Void> {
    //TODO consider getting the mentees and mentors from the builderHandler rather than as args.
    private final PersonMatchesViewModel resultVM;
    private final PersonMatchesViewModel excludedMatchesVM;
    private final PersonListViewModel mentees;
    private final PersonListViewModel mentors;
    private final MatchesBuilderHandler<Person, Person> builderHandler;
    private Matches<Person, Person> results;

    /**
     * Initialise a MultipleMatchTask object.
     * @param resultVM the ViewModel that will be updated when the task completes
     * @param excludedMatchesVM an optional ViewModel encapsulating matches that should be excluded
     *      from the match-making process, this argument MAY be null
     * @param builderHandler the handler that will supply the {@link MatchesBuilder}
     * @param mentees the VM containing the list of mentees to match
     * @param mentors the VM containing the list of mentors to match
     * @param callback the method to call when the task has run
     */
    public MultipleMatchTask(PersonMatchesViewModel resultVM, 
            PersonMatchesViewModel excludedMatchesVM,
            MatchesBuilderHandler<Person, Person> builderHandler,
            PersonListViewModel mentees, 
            PersonListViewModel mentors,
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
            filteredMentees = mentees.getUnderlyingData();
            filteredMentors = mentors.getUnderlyingData();
        } else {
            filteredMentees = filterAvailablePerson(mentees.getUnderlyingData(), 
                    excludedMatchesVM.getContent(), t -> t.getMentee());
            filteredMentors = filterAvailablePerson(mentors.getUnderlyingData(), 
                    excludedMatchesVM.getContent(), t -> t.getMentor());
        }
        results = builderHandler.get().build(filteredMentees, filteredMentors);
        return null;
    }

    @Override
    protected void specificActionOnSuccess() {
        resultVM.setAll(results);
        //TODO improve performance, modifying twice most persons should not be needed
        for (PersonViewModel vm : mentees.getContent()){
            vm.getStatus().remove(MatchStatus.MatchFlag.COMPUTED_MATCH);
        }
        for (PersonViewModel vm : mentors.getContent()){
            vm.getStatus().remove(MatchStatus.MatchFlag.COMPUTED_MATCH);
        }
        //FIXME when default mentee and default mentor are defined somewhere reasonnable, link to it
        Person defaultMentee = new PersonBuilder().withProperty("Email", "")
                .withFullName("PAS DE MENTORÉ").build();
        Person defaultMentor = new PersonBuilder().withProperty("Email", "")
                .withFullName("PAS DE MENTOR").build();
        for (Match<Person, Person> match : results){
            if(!match.getMentee().equals(defaultMentee) && !match.getMentor().equals(defaultMentor)){
                mentees.getPersonViewModel(match.getMentee()).getStatus()
                        .add(MatchStatus.MatchFlag.COMPUTED_MATCH);
                mentors.getPersonViewModel(match.getMentor()).getStatus()
                        .add(MatchStatus.MatchFlag.COMPUTED_MATCH);
            }
        }
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

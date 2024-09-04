package mentoring.viewmodel.tasks;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import mentoring.datastructure.Person;
import mentoring.match.Match;
import mentoring.match.MatchesBuilder;
import mentoring.match.MatchesBuilderHandler;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

public class SingleMatchTask extends AbstractTask<Match<Person, Person>> {
    
    private final PersonMatchesViewModel resultVM;
    private final MatchesBuilderHandler<Person, Person> builderHandler;
    private final Person mentee;
    private final Person mentor;
    private Match<Person, Person> result;

    /**
     * Initialise a {@code SingleMatchTask} object.
     * @param resultVM the view model that will be updated when the task completes
     * @param builderHandler the handler that will supply the {@link MatchesBuilder}
     * @param mentee the mentee to match
     * @param mentor the mentor to match
     * @param callback the method to call when the task has run
     */
    public SingleMatchTask(PersonMatchesViewModel resultVM,
            MatchesBuilderHandler<Person, Person> builderHandler,
            Person mentee, Person mentor, 
            TaskCompletionCallback<? super Match<Person, Person>> callback) {
        super(callback);
        this.resultVM = Objects.requireNonNull(resultVM);
        this.builderHandler = Objects.requireNonNull(builderHandler);
        this.mentee = Objects.requireNonNull(mentee);
        this.mentor = Objects.requireNonNull(mentor);
    }

    @Override
    protected Match<Person, Person> call() throws Exception {
        result = makeMatchWithException(builderHandler, mentee, mentor);
        return result;
    }

    @Override
    protected void specificActionOnSuccess() {
        resultVM.add(result);
    }

    private static Match<Person, Person> makeMatchWithException(
            MatchesBuilderHandler<Person, Person> builderHandler, 
            Person mentee,
            Person mentor) throws InterruptedException, ExecutionException {
        return builderHandler.get().buildSingleMatch(mentee, mentor);
    }
}

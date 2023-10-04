package mentoring.viewmodel.tasks;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import static javafx.concurrent.Worker.State.FAILED;
import static javafx.concurrent.Worker.State.READY;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import javafx.scene.control.Alert;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.datastructure.Person;
import mentoring.match.Match;
import mentoring.match.MatchesBuilder;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

public class SingleMatchTask extends AbstractTask<Match<Person, Person>> {
    
    private final PersonMatchesViewModel resultVM;
    private final ConfigurationPickerViewModel<CriteriaConfiguration<Person,Person>> criteriaVM;
    private final Person mentee;
    private final Person mentor;
    private Match<Person, Person> result;

    /**
     * Initialise a {@code SingleMatchTask} object.
     * @param resultVM the view model that will be updated when the task completes
     * @param criteriaVM the ViewModel that will be used to get the configuration
     * @param mentee the mentee to match
     * @param mentor the mentor to match
     */
    public SingleMatchTask(PersonMatchesViewModel resultVM, 
            ConfigurationPickerViewModel<CriteriaConfiguration<Person,Person>> criteriaVM, 
            Person mentee, Person mentor) {
        //TODO refactor: move to View layer
        super(task -> {
            State state = task.getState();
            switch(state){
                case READY, SUCCEEDED -> {/*no-op, excluded from default*/}//FIXME READY should be deleted (it erroneously fails a test)
                case FAILED -> new Alert(Alert.AlertType.ERROR, 
                        task.getException().getLocalizedMessage()).show();
                default -> new Alert(Alert.AlertType.WARNING,
                            "Callback was called before task was finished: " + state).show();
            }
        });
        this.resultVM = Objects.requireNonNull(resultVM);
        this.criteriaVM = Objects.requireNonNull(criteriaVM);
        this.mentee = Objects.requireNonNull(mentee);
        this.mentor = Objects.requireNonNull(mentor);
    }

    @Override
    protected Match<Person, Person> call() throws Exception {
        result = makeMatchWithException(criteriaVM, mentee, mentor);
        return result;
    }

    @Override
    protected void specificActionOnSuccess() {
        resultVM.add(result);
    }

    private static Match<Person, Person> makeMatchWithException(
            ConfigurationPickerViewModel<CriteriaConfiguration<Person,Person>> criteriaVM, 
            Person mentee,
            Person mentor) throws IOException {
        //Get criteria configuration
        CriteriaConfiguration<Person, Person> criteriaConfiguration = criteriaVM.getConfiguration();
        //Build match
        return matchMenteeAndMentor(mentee, mentor, criteriaConfiguration);
    }

    private static Match<Person, Person> matchMenteeAndMentor(Person mentee, Person mentor, 
            CriteriaConfiguration<Person, Person> criteriaConfiguration) {
        //TODO: MatchMaker should handle the MatchesBuilder object so that there is no need to
        //recreate one each time.
        MatchesBuilder<Person, Person> solver = new MatchesBuilder<>(List.of(mentee), List.of(mentor),
                criteriaConfiguration.getProgressiveCriteria());
        solver.withNecessaryCriteria(criteriaConfiguration.getNecessaryCriteria());
        return solver.buildSingleMatch(mentee, mentor);
    }
}

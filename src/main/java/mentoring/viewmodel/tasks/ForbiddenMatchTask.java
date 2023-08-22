package mentoring.viewmodel.tasks;

import java.util.Objects;
import static javafx.concurrent.Worker.State.FAILED;
import static javafx.concurrent.Worker.State.READY;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import javafx.scene.control.Alert;
import mentoring.datastructure.Person;
import mentoring.viewmodel.datastructure.ForbiddenMatchListViewModel;

/**
 * Class used to add forbidden matches to a {@link ForbiddenMatchListViewModel}.
 */
public class ForbiddenMatchTask extends AbstractTask<Void, ForbiddenMatchTask>{
    private final ForbiddenMatchListViewModel viewModel;
    private final Person mentee;
    private final Person mentor;
    
    public ForbiddenMatchTask(ForbiddenMatchListViewModel viewModel, Person mentee, Person mentor){
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
        this.viewModel = Objects.requireNonNull(viewModel);
        this.mentee = Objects.requireNonNull(mentee);
        this.mentor = Objects.requireNonNull(mentor);
    }
    
    @Override
    protected Void call(){
        return null;
    }
    
    @Override
    protected void specificActionOnSuccess(){
        viewModel.addForbiddenMatch(mentee, mentor);
    }
    
    @Override
    protected ForbiddenMatchTask self(){
        return this;
    }
}

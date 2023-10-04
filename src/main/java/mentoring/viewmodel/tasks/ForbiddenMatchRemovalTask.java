package mentoring.viewmodel.tasks;

import java.util.Objects;
import static javafx.concurrent.Worker.State.FAILED;
import static javafx.concurrent.Worker.State.READY;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import javafx.scene.control.Alert;
import mentoring.viewmodel.datastructure.ForbiddenMatchListViewModel;
import mentoring.viewmodel.datastructure.ForbiddenMatchViewModel;

/**
 * Class used to remove forbidden matches from a {@link ForbiddenMatchListViewModel}.
 */
public class ForbiddenMatchRemovalTask extends AbstractTask<Void>{
    private final ForbiddenMatchListViewModel list;
    private final ForbiddenMatchViewModel toRemove;
    
    public ForbiddenMatchRemovalTask(ForbiddenMatchListViewModel list, 
            ForbiddenMatchViewModel toRemove) {
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
        this.list = Objects.requireNonNull(list);
        this.toRemove = Objects.requireNonNull(toRemove);
    }
    
    @Override
    protected Void call(){
        return null;
    }
    
    @Override
    public void specificActionOnSuccess(){
        list.removeForbiddenMatch(toRemove);
    }
}

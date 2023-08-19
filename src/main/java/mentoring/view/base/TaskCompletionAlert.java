package mentoring.view.base;

import java.util.Map;
import java.util.function.Function;
import javafx.concurrent.Worker.State;
import javafx.scene.control.Alert;
import mentoring.viewmodel.tasks.AbstractTask;
import mentoring.viewmodel.tasks.AbstractTask.TaskCompletionCallback;
import mentoring.viewmodel.tasks.MatchExportTask;

/**
 * Builder of {@link TaskCompletionCallback} objects.
 * @param <U> the type of value returned by the task when successful
 * @param <V> the type of task handled
 */
public class TaskCompletionAlert<U,V extends AbstractTask<U,V>> 
        implements TaskCompletionCallback<U, V>{
    /*TODO improve: fix documentation, internationalise strings, improve readability, 
    have a builder, tests
    */
    private final Map<State, Function<V, Alert>> stateToAlertMap;
    private final Function<State, Alert> defaultAlertGenerator = state -> 
            new Alert(Alert.AlertType.WARNING,
                    "Callback was called before task was finished: " + state);
    private TaskCompletionAlert(Map<State, Function<V, Alert>> map){
        stateToAlertMap = map;
    }

    @Override
    public void accept(V task) {
        State state = task.getState();
        if(stateToAlertMap.containsKey(state)){
            stateToAlertMap.get(state).apply(task).show();
        } else {
            defaultAlertGenerator.apply(state).show();
        }
    }
    
    public static final TaskCompletionAlert<Void, MatchExportTask> MATCH_EXPORT_ALERT = new 
        TaskCompletionAlert<>(Map.of(
                State.SUCCEEDED, task -> new Alert(Alert.AlertType.INFORMATION, "Export completed"),
                State.FAILED, task -> new Alert(Alert.AlertType.ERROR, 
                        task.getException().getLocalizedMessage())));
}

package mentoring.view.base;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.concurrent.Worker.State;
import javafx.scene.control.Alert;
import mentoring.viewmodel.tasks.AbstractTask.TaskCompletionCallback;

/**
 * Class used to build {@link TaskCompletionCallback} objects that alert the user upon completion.
 * <p> This class is thread-safe.
 */
public final class TaskCompletionAlertFactory {
    
    private TaskCompletionAlertFactory(){
        throw new UnsupportedOperationException("static factory should not be instantiated");
    }
    
    /**
     * Build a TaskCompletionCallback that alerts the user when the task succeeds or fails.
     * @param successMessageProvider method to generate the message to display to the user if the
     *      task succeeds
     * @param failureMessageGenerator method to generate the message to display to the user if the
     *      task fails
     * @return the configured TaskCompletionCallback.
     */
    public static TaskCompletionCallback<Object> alertOnSuccessAndFailure(
            Supplier<String> successMessageProvider, 
            Function<Throwable, String> failureMessageGenerator){
        Objects.requireNonNull(successMessageProvider);
        Objects.requireNonNull(failureMessageGenerator);
        return task -> {
            State state = task.getState();
            Alert alert = switch(state){
                case FAILED -> new Alert(Alert.AlertType.ERROR, 
                        failureMessageGenerator.apply(task.getException()));
                case SUCCEEDED -> new Alert(Alert.AlertType.INFORMATION,
                        successMessageProvider.get());
                case CANCELLED, READY, RUNNING, SCHEDULED -> new Alert(Alert.AlertType.ERROR,
                        "Callback was called before task was finished: " + state)  ;  
            };
            alert.show();
        };
    }
    
    /**
     * Build a TaskCompletionCallback that alerts the user when the task succeeds or fails.
     * @param failureMessageGenerator method to generate the message to display to the user if the
     *      task fails
     * @return the configured TaskCompletionCallback.
     */
    public static TaskCompletionCallback<Object> alertOnFailure(
            Function<Throwable, String> failureMessageGenerator){
        Objects.requireNonNull(failureMessageGenerator);
        return task -> {
            State state = task.getState();
            Alert alert = switch(state){
                case FAILED -> new Alert(Alert.AlertType.ERROR, 
                        failureMessageGenerator.apply(task.getException()));
                case SUCCEEDED -> null;
                case CANCELLED, READY, RUNNING, SCHEDULED -> new Alert(Alert.AlertType.ERROR,
                        "Callback was called before task was finished: " + state)  ;  
            };
            if(alert != null){
                alert.show();
            }
        };
    }
}

package mentoring.viewmodel.tasks;

import java.util.Objects;
import java.util.function.Consumer;
import javafx.concurrent.Task;

/**
 * Abstract class defining common behaviour for all tasks regarding their completion. This class 
 * is not thread-safe: a task is supposed to be a single unit of work done in a single thread. When 
 * completed (either successfully or not), a different thread can call the corresponding method 
 * ({@link #succeeded()} or {@link #failed()}).
 * @param <T> the type of value returned by the task when successful
 */
public abstract class AbstractTask<T> extends Task<T> {
    
    /**
     * Interface for the callback method called when a task completes or fails.
     * @param <V> the type of value returned by the task.
     */
    @FunctionalInterface
    public static interface TaskCompletionCallback<V> extends Consumer<AbstractTask<? extends V>>{}
    
    private final TaskCompletionCallback<? super T> callback;
    
    /**
     * Build a new instance.
     * @param callback method that will be called when the task completes (either successfully or 
     *      not).
     */
    protected AbstractTask(TaskCompletionCallback<? super T> callback){
        this.callback = Objects.requireNonNull(callback);
    }
    
    @Override
    protected final void succeeded(){
        super.succeeded();
        specificActionOnSuccess();
        callback.accept(this);
    }
    
    /**
     * The action to perform when the task is successful, before alerting the user if appropriate.
     * This method is a no-op by default.
     */
    protected void specificActionOnSuccess(){}
    
    @Override
    protected final void failed(){
        super.failed();
        specificActionOnFailure();
        callback.accept(this);
    }
    
    /**
     * The action to perform when the task is failed, before alerting the user if appropriate. This
     * method is a no-op by default.
     */
    protected void specificActionOnFailure(){}
}

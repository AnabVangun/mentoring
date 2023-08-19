package mentoring.viewmodel.tasks;

import java.util.Objects;
import java.util.function.Consumer;
import javafx.concurrent.Task;

/**
 * Abstract class defining common behaviour for all tasks regarding their completion.This class 
 is not thread-safe: a task is supposed to be a single unit of work done in a single thread. When 
 * completed (either successfully or not), a different thread can call the corresponding method 
 * ({@link #succeeded()} or {@link #failed()}).
 * @param <T> the type of value returned by the task when successful
 * @param <E> the self-type of each implementing subclass
 */
public abstract class AbstractTask<T, E extends AbstractTask<T,E>> extends Task<T> {
    
    public static interface TaskCompletionCallback<U, V extends AbstractTask<U,V>> 
            extends Consumer<V>{}
    //TODO simplify interface: U might be optional, V could be a superclass of the self type
    
    private final TaskCompletionCallback<T,E> callback;
    
    /**
     * Build a new instance.
     * @param callback method that will be called when the task completes (either successfully or 
     *      not).
     */
    protected AbstractTask(TaskCompletionCallback<T, E> callback){
        this.callback = Objects.requireNonNull(callback);
    }
    
    @Override
    protected final void succeeded(){
        super.succeeded();
        specificActionOnSuccess();
        callback.accept(self());
    }
    
    /**
     * The action to perform when the task is successful, before alerting the user if appropriate.
     * This method is a no-op by default.
     */
    protected void specificActionOnSuccess(){}
    
    @Override
    protected final void failed(){
        super.succeeded();
        specificActionOnFailure();
        callback.accept(self());
    }
    
    /**
     * The action to perform when the task is failed, before alerting the user if appropriate. This
     * method is a no-op by default.
     */
    protected void specificActionOnFailure(){}
    
    /**
     * All subclasses MUST implement this method to return this.
     * @return this
     */
    protected abstract E self();
    
}

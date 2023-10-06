package mentoring.viewmodel.tasks;

import java.util.Objects;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

public class SingleMatchRemovalTask extends AbstractTask<Void> {
    
    private final PersonMatchesViewModel resultVM;
    private final PersonMatchViewModel toRemove;

    /**
     * Initialise a {@code SingleMatchDeletionTask} object.
     * @param resultVM the view model that will be updated when the task completes
     * @param toRemove the view model representing the match to remove
     * @param callback the method to call when the task has run
     */
    public SingleMatchRemovalTask(PersonMatchesViewModel resultVM, PersonMatchViewModel toRemove,
            TaskCompletionCallback<? super Void> callback) {
        super(callback);
        this.resultVM = Objects.requireNonNull(resultVM);
        this.toRemove = Objects.requireNonNull(toRemove);
    }

    @Override
    protected Void call() throws Exception {
        return null;
    }

    @Override
    protected void specificActionOnSuccess() {
        resultVM.remove(toRemove);
    }
}

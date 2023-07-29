package mentoring.viewmodel.tasks;

import java.util.Objects;
import javafx.concurrent.Task;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

public class SingleMatchRemovalTask extends Task<Void> {
    
    private final PersonMatchesViewModel resultVM;
    private final PersonMatchViewModel toRemove;

    /**
     * Initialise a {@code SingleMatchDeletionTask} object.
     * @param resultVM the view model that will be updated when the task completes
     * @param toRemove the view model representing the match to remove
     */
    public SingleMatchRemovalTask(PersonMatchesViewModel resultVM, PersonMatchViewModel toRemove) {
        this.resultVM = Objects.requireNonNull(resultVM);
        this.toRemove = Objects.requireNonNull(toRemove);
    }

    @Override
    protected Void call() throws Exception {
        return null;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        resultVM.remove(toRemove);
    }
    
}

package mentoring.viewmodel.tasks;

import java.util.Objects;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

public class SingleMatchRemovalTask extends AbstractTask<Void, SingleMatchRemovalTask> {
    
    private final PersonMatchesViewModel resultVM;
    private final PersonMatchViewModel toRemove;

    /**
     * Initialise a {@code SingleMatchDeletionTask} object.
     * @param resultVM the view model that will be updated when the task completes
     * @param toRemove the view model representing the match to remove
     */
    public SingleMatchRemovalTask(PersonMatchesViewModel resultVM, PersonMatchViewModel toRemove) {
        //TODO refactor: move to View layer
        super(task -> {});
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
    
    @Override
    protected SingleMatchRemovalTask self(){
        return this;
    }
}

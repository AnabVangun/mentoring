package mentoring.viewmodel.tasks;

import java.util.Objects;
import mentoring.viewmodel.datastructure.MatchStatus;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.datastructure.PersonViewModel;

public class SingleMatchRemovalTask extends AbstractTask<Void> {
    
    private final PersonMatchesViewModel resultVM;
    private final PersonMatchViewModel toRemove;
    private final MatchStatus selectedMenteeStatus;
    private final MatchStatus selectedMentorStatus;

    /**
     * Initialise a {@link SingleMatchRemovalTask} object. 
     * This task consists in deleting a manual match and propagating the effects of the deletion to
     * the rest of the system.
     * @param resultVM the view model that will be updated when the task completes
     * @param toRemove the view model representing the match to remove
     * @param selectedMentee the view model that represent the mentee of the match to remove
     * @param selectedMentor the view model that represent the mentor of the match to remove
     * @param callback the method to call when the task has run
     */
    public SingleMatchRemovalTask(PersonMatchesViewModel resultVM, PersonMatchViewModel toRemove,
            PersonViewModel selectedMentee, PersonViewModel selectedMentor,
            TaskCompletionCallback<? super Void> callback) {
        super(callback);
        this.resultVM = Objects.requireNonNull(resultVM);
        this.toRemove = Objects.requireNonNull(toRemove);
        this.selectedMenteeStatus = selectedMentee.getStatus();
        this.selectedMentorStatus = selectedMentor.getStatus();
    }

    @Override
    protected Void call() throws Exception {
        return null;
    }

    @Override
    protected void specificActionOnSuccess() {
        resultVM.remove(toRemove);
        selectedMenteeStatus.remove(MatchStatus.MatchFlag.MANUAL_MATCH);
        selectedMentorStatus.remove(MatchStatus.MatchFlag.MANUAL_MATCH);
    }
}

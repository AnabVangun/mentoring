package mentoring.viewmodel.tasks;

import java.util.Objects;
import mentoring.datastructure.Person;
import mentoring.viewmodel.datastructure.ForbiddenMatchListViewModel;

/**
 * Class used to add forbidden matches to a {@link ForbiddenMatchListViewModel}.
 */
public class ForbiddenMatchTask extends AbstractTask<Void>{
    private final ForbiddenMatchListViewModel viewModel;
    private final Person mentee;
    private final Person mentor;
    
    public ForbiddenMatchTask(ForbiddenMatchListViewModel viewModel, Person mentee, Person mentor,
            TaskCompletionCallback<? super Void> callback){
        super(callback);
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
}

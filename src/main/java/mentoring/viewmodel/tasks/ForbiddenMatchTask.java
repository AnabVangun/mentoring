package mentoring.viewmodel.tasks;

import java.util.Objects;
import mentoring.datastructure.Person;
import mentoring.match.MatchesBuilderHandler;
import mentoring.viewmodel.datastructure.ForbiddenMatchListViewModel;

/**
 * Class used to add forbidden matches to a {@link ForbiddenMatchListViewModel}.
 */
public class ForbiddenMatchTask extends AbstractTask<Void>{
    private final ForbiddenMatchListViewModel list;
    private final Person mentee;
    private final Person mentor;
    private final MatchesBuilderHandler<Person, Person> handler;
    
    public ForbiddenMatchTask(ForbiddenMatchListViewModel list, Person mentee, Person mentor,
            MatchesBuilderHandler<Person, Person> handler,
            TaskCompletionCallback<? super Void> callback){
        super(callback);
        this.list = Objects.requireNonNull(list);
        this.mentee = Objects.requireNonNull(mentee);
        this.mentor = Objects.requireNonNull(mentor);
        this.handler = handler;
    }
    
    @Override
    protected Void call(){
        return null;
    }
    
    @Override
    protected void specificActionOnSuccess(){
        list.addForbiddenMatch(mentee, mentor, handler);
    }
}

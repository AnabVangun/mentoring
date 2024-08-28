package mentoring.viewmodel.tasks;

import java.util.Objects;
import mentoring.datastructure.Person;
import mentoring.match.MatchesBuilderHandler;
import mentoring.viewmodel.datastructure.ForbiddenMatchListViewModel;
import mentoring.viewmodel.datastructure.ForbiddenMatchViewModel;

/**
 * Class used to remove forbidden matches from a {@link ForbiddenMatchListViewModel}.
 */
public class ForbiddenMatchRemovalTask extends AbstractTask<Void>{
    private final ForbiddenMatchListViewModel list;
    private final ForbiddenMatchViewModel toRemove;
    private final MatchesBuilderHandler<Person, Person> handler;
    
    public ForbiddenMatchRemovalTask(ForbiddenMatchListViewModel list, 
            ForbiddenMatchViewModel toRemove, MatchesBuilderHandler<Person, Person> handler,
            TaskCompletionCallback<? super Void> callback) {
        super(callback);
        this.list = Objects.requireNonNull(list);
        this.toRemove = Objects.requireNonNull(toRemove);
        this.handler = Objects.requireNonNull(handler);
    }
    
    @Override
    protected Void call(){
        return null;
    }
    
    @Override
    public void specificActionOnSuccess(){
        list.removeForbiddenMatch(toRemove, handler);
    }
}

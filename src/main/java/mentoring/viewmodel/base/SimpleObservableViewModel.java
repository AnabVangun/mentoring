package mentoring.viewmodel.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * Abstract class with a basic implementation of the {@link Observable} interface.
 */
public abstract class SimpleObservableViewModel implements Observable{
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<InvalidationListener> listeners = new ArrayList<>();
    
    @Override
    public final void addListener(InvalidationListener il) {
        Objects.requireNonNull(il);
        listeners.add(il);
    }

    @Override
    public final void removeListener(InvalidationListener il) {
        Objects.requireNonNull(il);
        listeners.remove(il);
    }
    
    protected final void notifyListeners(){
        //FIXME: possible concurrency exception if addListener or removeListener is called during notifyListeners
        for(InvalidationListener listener : listeners){
            listener.invalidated(this);
        }
    }
}

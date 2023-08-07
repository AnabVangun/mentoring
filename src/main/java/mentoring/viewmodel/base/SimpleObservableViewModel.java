package mentoring.viewmodel.base;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * Abstract class with a basic implementation of the {@link Observable} interface.
 */
public abstract class SimpleObservableViewModel implements Observable{
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<InvalidationListener> listeners = new CopyOnWriteArrayList<>();
    
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
        for(InvalidationListener listener : listeners){
            listener.invalidated(this);
        }
    }
}

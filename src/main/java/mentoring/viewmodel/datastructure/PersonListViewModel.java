package mentoring.viewmodel.match;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import mentoring.configuration.ResultConfiguration;
import mentoring.match.Match;
import mentoring.match.Matches;

/**
 * Viewmodel responsible for representing a {@link Matches} object. The object is not ready until
 * {@link #update(mentoring.configuration.ResultConfiguration, mentoring.match.Matches) } has been
 * called.
 * 
 * @param <Mentee> type of the first element of a {@link Match}.
 * @param <Mentor> type of the second element of a {@link Match}.
 * @param <VM> type of the {@link MatchViewModel} used to represent each match.
 */
public class MatchesViewModel<Mentee, Mentor, VM extends MatchViewModel<Mentee, Mentor>> 
        implements Observable {
    private final List<String> headerContent = new ArrayList<>();
    private final List<VM> batchUpdateItems = new ArrayList<>();
    private final List<VM> transferredItems = new ArrayList<>();
    private ResultConfiguration<Mentee, Mentor> configuration = null;
    private Matches<Mentee, Mentor> pendingMatches = null;
    private boolean ready = false;
    private final BiFunction<ResultConfiguration<Mentee, Mentor>, Match<Mentee, Mentor>, 
            VM> vmFactory;
    private final List<InvalidationListener> listeners = new ArrayList<>();
    private boolean invalidated = false;
    private boolean invalidatedHeader = false;
    
    /**
     * Builds a new {@code MatchesViewModel} object.
     * @param vmFactory factory building the viewmodel encapsulating the individual {@link Match}
     *      objects of the {@link Matches} object represented by this viewmodel.
     */
    protected MatchesViewModel(BiFunction<ResultConfiguration<Mentee, Mentor>, 
            Match<Mentee, Mentor>, VM> vmFactory){
        this.vmFactory = vmFactory;
    }
    
    /**
     * Returns true if the viewmodel represents valid data.
     */
    public boolean isValid(){
        return ready || invalidated;
    }
    
    /**
     * Returns the header of the {@link Matches} object.
     * The header contains the attributes kept in the {@link ResultConfiguration} argument of the 
     * last call to 
     * {@link #update(mentoring.configuration.ResultConfiguration, mentoring.match.Matches) }.
     */
    public List<String> getHeaderContent(){
        updateIfNecessary();
        return headerContent;
    }
    
    /**
     * Returns the content of the represented {@link Matches} object.
     */
    public List<VM> getBatchItems(){
        updateIfNecessary();
        return batchUpdateItems;
    }
    
    /**
     * Returns the representation of the {@link Match} objects that have been transferred.
     */
    public List<VM> getTransferredItems(){
        updateIfNecessary();
        return transferredItems;
    }
    
    /**
     * Updates this viewmodel to represent a {@link Matches} object.
     * @param configuration used to select the attributes to represent.
     * @param matches the data to represent.
     */
    public synchronized void update(ResultConfiguration<Mentee, Mentor> configuration,
            Matches<Mentee, Mentor> matches){
        Objects.requireNonNull(configuration);
        if(!configuration.equals(this.configuration)){
            this.configuration = configuration;
            invalidatedHeader = true;
        }
        this.pendingMatches = matches;
        invalidated = true;
        notifyListeners();
    }
    
    private void updateIfNecessary(){
        if(invalidated){
            actuallyUpdate();
        }
    }
    
    private synchronized void actuallyUpdate(){
        if(invalidated){
            if(invalidatedHeader){
                prepareHeader(configuration);
                updateManualItems();
                invalidatedHeader = false;
            }
            prepareItems(configuration, pendingMatches);
            ready = true;
            invalidated = false;
        }
    }
    
    private void prepareHeader(ResultConfiguration<Mentee, Mentor> configuration) {
        this.configuration = configuration;
        headerContent.clear();
        headerContent.addAll(Arrays.asList(configuration.getResultHeader()));
    }
    
    private void updateManualItems(){
        transferredItems.replaceAll(vm -> vmFactory.apply(configuration, vm.getData()));
    }
            
    private void prepareItems(
            ResultConfiguration<Mentee, Mentor> configuration, Matches<Mentee, Mentor> matches) {
        batchUpdateItems.clear();
        for (Match<Mentee, Mentor> match : matches){
            batchUpdateItems.add(vmFactory.apply(configuration, match));
        }
    }
    
    private void notifyListeners(){
        for(InvalidationListener listener : listeners){
            listener.invalidated(this);
        }
    }

    @Override
    public void addListener(InvalidationListener il) {
        Objects.requireNonNull(il);
        listeners.add(il);
        if(invalidated){
            il.invalidated(this);
        }
    }

    @Override
    public void removeListener(InvalidationListener il) {
        Objects.requireNonNull(il);
        listeners.remove(il);
    }
    
    /**
     * Transfer item from the batch list to the manual one.
     * @param item to transfer between the two lists.
     */
    public void transferItem(VM item){
        Objects.requireNonNull(item);
        transferredItems.add(item);
        batchUpdateItems.remove(item);
        notifyListeners();
    }
}

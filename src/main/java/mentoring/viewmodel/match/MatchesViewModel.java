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
    /*
    TODO Implement lazy evaluation
     */
    private final List<String> headerContent = new ArrayList<>();
    private final List<VM> items = new ArrayList<>();
    private boolean ready = false;
    private final BiFunction<ResultConfiguration<Mentee, Mentor>, Match<Mentee, Mentor>, 
            VM> vmFactory;
    private final List<InvalidationListener> listeners = new ArrayList<>();
    
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
        return ready;
    }
    
    /**
     * Returns the header of the {@link Matches} object.
     * The header contains the attributes kept in the {@link ResultConfiguration} argument of the 
     * last call to 
     * {@link #update(mentoring.configuration.ResultConfiguration, mentoring.match.Matches) }.
     */
    public List<String> getHeaderContent(){
        return headerContent;
    }
    
    /**
     * Returns a property that describes the content of the {@link Matches} object.
     */
    public List<VM> getItems(){
        return items;
    }
    
    /**
     * Updates this viewmodel to represent a {@link Matches} object.
     * @param configuration used to select the attributes to represent.
     * @param matches the data to represent.
     */
    public synchronized void update(ResultConfiguration<Mentee, Mentor> configuration,
            Matches<Mentee, Mentor> matches){
        prepareHeader(configuration);
        prepareItems(configuration, matches);
        ready = true;
        for(InvalidationListener listener : listeners){
            listener.invalidated(this);
        }
    }
    
    private void prepareHeader(ResultConfiguration<Mentee, Mentor> configuration) {
        headerContent.clear();
        headerContent.addAll(Arrays.asList(configuration.getResultHeader()));
    }
            
    private void prepareItems(
            ResultConfiguration<Mentee, Mentor> configuration, Matches<Mentee, Mentor> matches) {
        items.clear();
        for (Match<Mentee, Mentor> match : matches){
            items.add(vmFactory.apply(configuration, match));
        }
    }

    @Override
    public void addListener(InvalidationListener il) {
        Objects.requireNonNull(il);
        listeners.add(il);
    }

    @Override
    public void removeListener(InvalidationListener il) {
        Objects.requireNonNull(il);
        listeners.remove(il);
    }
}

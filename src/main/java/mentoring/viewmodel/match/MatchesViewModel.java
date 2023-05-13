package mentoring.viewmodel.match;

import java.util.function.BiFunction;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
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
public class MatchesViewModel<Mentee, Mentor, VM extends MatchViewModel<Mentee, Mentor>> {
    //FIXME: this potentially smells, TableColumn is more a View thing than a ViewModel...
    private final ObservableList<TableColumn<VM, String>> 
            modifiableHeaderContent = FXCollections.observableArrayList();
    private final ObservableList<TableColumn<VM, String>> headerContent =
            FXCollections.unmodifiableObservableList(modifiableHeaderContent);
    private final ObservableList<VM> 
            modifiableItems = FXCollections.observableArrayList();
    private final ObservableList<VM> items =
            FXCollections.unmodifiableObservableList(modifiableItems);
    private final ReadOnlyBooleanWrapper modifiableReady = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanProperty ready = modifiableReady.getReadOnlyProperty();
    private final BiFunction<ResultConfiguration<Mentee, Mentor>, Match<Mentee, Mentor>, 
            VM> vmFactory;
    
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
     * Returns a property that is true when the object encompasses valid data.
     */
    public ReadOnlyBooleanProperty readyProperty(){
        return ready;
    }
    
    /**
     * Returns a property that describes the header of the {@link Matches} object.
     * The header contains the attributes kept in the {@link ResultConfiguration} argument of the 
     * last call to 
     * {@link #update(mentoring.configuration.ResultConfiguration, mentoring.match.Matches) }.
     */
    public ObservableList<TableColumn<VM, String>> headerContentProperty(){
        return headerContent;
    }
    
    /**
     * Returns a property that describes the content of the {@link Matches} object.
     */
    public ObservableList<VM> itemsProperty(){
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
        modifiableReady.set(true);
    }
    
    private void prepareHeader(ResultConfiguration<Mentee, Mentor> configuration) {
        modifiableHeaderContent.clear();
        for (String headerItem : configuration.getResultHeader()){
            modifiableHeaderContent.add(new TableColumn<>(headerItem));
        }
    }
            
    private void prepareItems(
            ResultConfiguration<Mentee, Mentor> configuration, Matches<Mentee, Mentor> matches) {
        modifiableItems.clear();
        for (Match<Mentee, Mentor> match : matches){
            modifiableItems.add(vmFactory.apply(configuration, match));
        }
    }
}

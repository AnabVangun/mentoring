package mentoring.viewmodel.datastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.viewmodel.base.SimpleObservable;
import mentoring.viewmodel.base.TabularDataViewModel;

/**
 * ViewModel responsible for representing a list of {@link Person} objects.
 */
public class PersonListViewModel extends SimpleObservable 
        implements TabularDataViewModel<PersonViewModel>{
    //TODO once PersonViewModel represents more than just the name, fix this initialisation.
    private final List<String> headerContent = List.of("Name");
    private final ObservableList<PersonViewModel> items = 
            FXCollections.observableArrayList();
    private List<Person> pendingItems;
    private final List<Person> underlyingData = new ArrayList<>();
    private PersonConfiguration configuration = null;
    private final List<InvalidationListener> listeners = new ArrayList<>();
    private boolean invalidated = false;
    
    /**
     * Returns the header of the {@link Person} object.
     * The header contains the attributes from the {@link PersonConfiguration} argument of the 
     * last call to 
     * {@link #update(mentoring.configuration.ResultConfiguration, mentoring.match.Matches) }.
     */
    @Override
    public List<String> getHeaders(){
        //TODO: check if lazy update actually does anything useful
        updateIfNecessary();
        return headerContent;
    }
    
    /**
     * Returns the content of the represented {@link Person} objects.
     */
    @Override
    public ObservableList<PersonViewModel> getContent(){
        updateIfNecessary();
        return items;
    }
    
    public List<Person> getUnderlyingData(){
        //TODO: there might be a layer issue, a viewModel should not expose data objects.
        updateIfNecessary();
        return underlyingData;
    }
    
    /**
     * Updates this viewmodel to represent a list of {@link Person} objects.
     * @param configuration used to select the attributes to represent.
     * @param persons the data to represent.
     */
    public synchronized void update(PersonConfiguration configuration, List<Person> persons){
        Objects.requireNonNull(configuration);
        this.configuration = configuration;
        this.pendingItems = persons;
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
            prepareHeader(configuration);
            setUnderlyingData(pendingItems);
            prepareItems(configuration);
            invalidated = false;
        }
    }
    
    private void prepareHeader(PersonConfiguration configuration) {
        //TODO: once PersonViewModel represent more than just the name, fix this method.
        this.configuration = configuration;
    }
    
    private void setUnderlyingData(List<Person> matches) {
        this.underlyingData.clear();
        this.underlyingData.addAll(matches);
    }
            
    private void prepareItems(PersonConfiguration configuration) {
        items.clear();
        for (Person person : underlyingData){
            items.add(new PersonViewModel(configuration, person));
        }
    }
}

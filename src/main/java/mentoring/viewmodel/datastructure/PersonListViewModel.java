package mentoring.viewmodel.datastructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.match.Match;
import mentoring.viewmodel.base.SimpleObservable;
import mentoring.viewmodel.base.TabularDataViewModel;

/**
 * ViewModel responsible for representing a list of {@link Person} objects.
 */
public class PersonListViewModel extends SimpleObservable 
        implements TabularDataViewModel<PersonViewModel>{
    private final List<String> modifiableHeaderContent = new ArrayList<>();
    private final List<String> headerContent = Collections.unmodifiableList(modifiableHeaderContent);
    private final ObservableList<PersonViewModel> items = 
            FXCollections.observableArrayList();
    private List<Person> pendingItems;
    private final List<Person> underlyingData = new ArrayList<>();
    private PersonConfiguration configuration = null;
    private boolean invalidated = false;
    
    /**
     * Returns the header of the {@link Person} object.
     * The header contains the attributes from the {@link PersonConfiguration} argument of the 
     * last call to 
     * {@link #update(mentoring.configuration.ResultConfiguration, mentoring.match.Matches) }.
     */
    @Override
    public List<String> getHeaders(){
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
        //TODO: there might be a layer issue, a viewModel should not publicly expose data objects.
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
        this.configuration = configuration;
        this.modifiableHeaderContent.clear();
        this.modifiableHeaderContent.add("Name");
        this.modifiableHeaderContent.addAll(
                configuration.getPropertiesNames().stream().map(prop -> prop.getName())
                        .collect(Collectors.toList()));
        this.modifiableHeaderContent.addAll(
                configuration.getMultiplePropertiesNames().stream().map(prop -> prop.getName())
                        .collect(Collectors.toList()));
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
    
    /**
     * Get the PersonViewModel contained in this list corresponding to the requested person.
     * @param match ViewModel containing the requested person
     * @param type of person to get from the match
     * @return the PersonViewModel corresponding to the requested person or null if the person is 
     * not in this list
     */
    public PersonViewModel getPersonViewModel(PersonMatchViewModel match, PersonType type){
        Match<Person, Person> actualMatch = match.getData();
        Person person = switch(type){
            case MENTEE -> actualMatch.getMentee();
            case MENTOR -> actualMatch.getMentor();
        };
        PersonViewModel result = null;
        for (PersonViewModel vm : getContent()) {
            if (vm.getPerson().equals(person)){
                result = vm;
                break;
            }
        }
        return result;
    }
}

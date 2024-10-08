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
    //TODO check performance gain of lazy initialization and possibly get rid of it
    private final List<String> modifiableHeaderContent = new ArrayList<>();
    private final List<String> headerContent = Collections.unmodifiableList(modifiableHeaderContent);
    private final ObservableList<PersonViewModel> items = 
            FXCollections.observableArrayList();
    private List<Person> pendingItems;
    private final List<Person> underlyingData = new ArrayList<>();
    private PersonConfiguration configuration = null;
    private boolean invalidated = false;
    private PersonViewModelFactory viewModelFactory = null;
    
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
            prepareItems();
            invalidated = false;
        }
    }
    
    private void prepareHeader(PersonConfiguration configuration) {
        this.configuration = configuration;
        modifiableHeaderContent.clear();
        viewModelFactory = new PersonViewModelFactory(configuration);
        modifiableHeaderContent.add(viewModelFactory.getFullNamePropertyName());
        modifiableHeaderContent.addAll(
                configuration.getSimplePropertiesNames().stream().map(prop -> prop.getName())
                        .collect(Collectors.toList()));
        modifiableHeaderContent.addAll(
                configuration.getMultiplePropertiesNames().stream().map(prop -> prop.getName())
                        .collect(Collectors.toList()));
    }
    
    private void setUnderlyingData(List<Person> persons) {
        this.underlyingData.clear();
        this.underlyingData.addAll(persons);
    }
            
    private void prepareItems() {
        items.clear();
        items.addAll(viewModelFactory.create(underlyingData));
    }
    
    /**
     * Get the PersonViewModel contained in this list corresponding to the requested person.
     * @param match ViewModel containing the requested person
     * @param type of person to get from the match
     * @return the PersonViewModel corresponding to the requested person or null if the person is 
     * not in this list
     */
    public PersonViewModel getPersonViewModel(PersonMatchViewModel match, PersonType type){
        Person person = getPerson(match, type);
        PersonViewModel result = null;
        for (PersonViewModel vm : getContent()) {
            if (vm.getData().equals(person)){
                result = vm;
                break;
            }
        }
        return result;
    }
    
    /**
     * Get the index of the PersonViewModel contained in this list corresponding to the requested 
     * person.
     * @param match ViewModel containing the requested person
     * @param type of person to get from the match
     * @return the index of the PersonViewModel corresponding to the requested person in this list 
     * or -1 if the person is not in this list
     */
    public int getPersonViewModelIndex(PersonMatchViewModel match, PersonType type){
        Person person = getPerson(match, type);
        int result = -1;
        List<PersonViewModel> persons = getContent();
        for (int i = 0; i < persons.size(); i++) {
            if (persons.get(i).getData().equals(person)){
                result = i;
                break;
            }
        }
        return result;
    }
    
    private Person getPerson(PersonMatchViewModel match, PersonType type){
        Match<Person, Person> actualMatch = match.getData();
        return switch(type){
            case MENTEE -> actualMatch.getMentee();
            case MENTOR -> actualMatch.getMentor();
        };
    }
}

package mentoring.view.datastructure;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javax.inject.Inject;
import mentoring.view.base.TabularDataViewTools;
import mentoring.viewmodel.datastructure.PersonListViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonType;
import mentoring.viewmodel.datastructure.PersonViewModel;

/**
 * View responsible for displaying a table of {@link MatchViewModel} object.
 */
public class MatchesTableView implements Initializable {
    
    @FXML
    private TableView<PersonViewModel> menteeTable;
    @FXML
    private TableView<PersonViewModel> mentorTable;
    @FXML
    private TableView<PersonMatchViewModel> computedTable;
    @FXML
    private TableView<PersonMatchViewModel> manualTable;
    @FXML
    private SplitPane personPane;
    @FXML
    private SplitPane matchPane;
    
    private final PersonMatchesViewModel batchVM;
    private final PersonMatchesViewModel oneAtATimeVM;
    private final PersonListViewModel menteeVM;
    private final PersonListViewModel mentorVM;
    private final InvalidationListener batchMatchesListener;
    private final InvalidationListener oneAtATimeListener;
    private final InvalidationListener menteeListener;
    private final InvalidationListener mentorListener;
    private final InvalidationListener matchSelectionListener;
    private final InvalidationListener personSelectionListener;
    
    @Inject
    MatchesTableView(PersonMatchesViewModel computedVM, PersonMatchesViewModel oneAtATimeVM,
            PersonListViewModel menteeVM, PersonListViewModel mentorVM){
        this.batchVM = computedVM;
        this.oneAtATimeVM = oneAtATimeVM;
        this.menteeVM = menteeVM;
        this.mentorVM = mentorVM;
        batchMatchesListener = observable -> 
                TabularDataViewTools.updateTable(computedTable, batchVM, e -> e.observableMatch());
        oneAtATimeListener = observable -> 
                TabularDataViewTools.updateTable(manualTable, oneAtATimeVM, e -> e.observableMatch());
        menteeListener = observable -> TabularDataViewTools.updateTable(menteeTable, menteeVM, 
                e -> e.getFormattedData());
        mentorListener = observable -> TabularDataViewTools.updateTable(mentorTable, mentorVM, 
                e -> e.getFormattedData());
        matchSelectionListener = observable -> {
            @SuppressWarnings("unchecked")
            PersonMatchViewModel match = ((ReadOnlyObjectProperty<PersonMatchViewModel>) observable)
                    .get();
            if(match != null){
                selectPersons(match, selectMatchTableToClear(match));
            }
        };
        personSelectionListener = observable -> {
            @SuppressWarnings("unchecked")
            PersonViewModel person = ((ReadOnlyObjectProperty<PersonViewModel>) observable).get();
            if (person != null){
                unselectMatchInTableIfAppropriate(computedTable, person);
                unselectMatchInTableIfAppropriate(manualTable, person);
            }
        };
    }
    
    /**
     * Returns this view's underlying ViewModel for matches made in batches.
     * @return a ViewModel encapsulating Matches objects.
     */
    public PersonMatchesViewModel getBatchMatchesViewModel(){
        return batchVM;
    }
    
    /**
     * Returns this view's underlying ViewModel for matches made one at a time.
     * @return a ViewModel encapsulating Matches objects.
     */
    public PersonMatchesViewModel getOneAtATimeMatchesViewModel(){
        return oneAtATimeVM;
    }
    
    /**
     * Returns this view's underlying view model for a person list.
     * @param type determines which of the person lists to return
     * @return the underlying person list view model corresponding to the input type
     */
    public PersonListViewModel getPersonViewModel(PersonType type){
        return switch(type){
            case MENTEE -> menteeVM;
            case MENTOR -> mentorVM;
        };
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Callback<TableView<PersonViewModel>, TableRow<PersonViewModel>> personRowFactory =
                TabularDataViewTools.boundStyleRowFactory(person -> 
                        person.getStatus().getStyleClass());
        menteeTable.setRowFactory(personRowFactory);
        mentorTable.setRowFactory(personRowFactory);
        batchVM.addListener(new WeakInvalidationListener(batchMatchesListener));
        oneAtATimeVM.addListener(new WeakInvalidationListener(oneAtATimeListener));
        menteeVM.addListener(new WeakInvalidationListener(menteeListener));
        mentorVM.addListener(new WeakInvalidationListener(mentorListener));
        matchPane.getDividers().get(0).positionProperty()
                .bindBidirectional(personPane.getDividers().get(0).positionProperty());
        //This is supposed to be the default but this application explicitly needs it to hold.
        menteeTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        mentorTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        computedTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        manualTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        computedTable.getSelectionModel().selectedItemProperty()
                .addListener(new WeakInvalidationListener(matchSelectionListener));
        manualTable.getSelectionModel().selectedItemProperty()
                .addListener(new WeakInvalidationListener(matchSelectionListener));
        menteeTable.getSelectionModel().selectedItemProperty()
                .addListener(new WeakInvalidationListener(personSelectionListener));
        mentorTable.getSelectionModel().selectedItemProperty()
                .addListener(new WeakInvalidationListener(personSelectionListener));
    }
    
    private TableView<?> selectMatchTableToClear(PersonMatchViewModel match){
        //if match was from computedTable, unselect manualTable, and the other way around
        if(match == computedTable.getSelectionModel().getSelectedItem()){
            return manualTable;
        } else {
            return computedTable;
        }
    }
    
    private void selectPersons(PersonMatchViewModel match, TableView<?> tableToClear){
        int selectedMenteeIndex = 
                menteeVM.getPersonViewModelIndex(match, PersonType.MENTEE);
        TabularDataViewTools.selectAndScrollTo(menteeTable, selectedMenteeIndex);
        int selectedMentorIndex = 
                mentorVM.getPersonViewModelIndex(match, PersonType.MENTOR);
        TabularDataViewTools.selectAndScrollTo(mentorTable, selectedMentorIndex);
        if(tableToClear != null){
            tableToClear.getSelectionModel().clearSelection();
        }
    }
    
    /**
     * Return this view's selected person as a read-only property.
     * @param type determines which of the person list to check for selection
     * @return a property encapsulating the selected person view model
     */
    public ReadOnlyObjectProperty<PersonViewModel> getSelectedPersonProperty(PersonType type){
        TableView<PersonViewModel> personVM = switch(type) {
            case MENTEE -> menteeTable;
            case MENTOR -> mentorTable;
        };
        return personVM.getSelectionModel().selectedItemProperty();
    }
    
    /**
     * Returns this view's selected manual match as a read-only property.
     * @return a property encapsulating the selected manual match
     */
    public ReadOnlyObjectProperty<PersonMatchViewModel> getSelectedManualMatchProperty(){
        return manualTable.getSelectionModel().selectedItemProperty();
    }
    
    /**
     * Returns this view's selected computed match as a read-only property.
     * @return a property encapsulating the selected computed match
     */
    public ReadOnlyObjectProperty<PersonMatchViewModel> getSelectedComputedMatchProperty(){
        return computedTable.getSelectionModel().selectedItemProperty();
    }
    
    private void unselectMatchInTableIfAppropriate(TableView<PersonMatchViewModel> table, 
            PersonViewModel person){
        PersonMatchViewModel match = table.getSelectionModel().getSelectedItem();
        if(match != null && !(match.containsMentee(person) || match.containsMentor(person))) {
            table.getSelectionModel().clearSelection();
        }
    }
}

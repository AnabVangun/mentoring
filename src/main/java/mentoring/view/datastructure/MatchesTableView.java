package mentoring.view.datastructure;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
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
                e -> e.getPersonData());
        mentorListener = observable -> TabularDataViewTools.updateTable(mentorTable, mentorVM, 
                e -> e.getPersonData());
        matchSelectionListener = observable -> {
            @SuppressWarnings("unchecked")
            PersonMatchViewModel match = ((ReadOnlyObjectProperty<PersonMatchViewModel>) observable)
                    .get();
            if(match != null){
                selectPersons(match, selectMatchTableToClear(match));
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
        batchVM.addListener(new WeakInvalidationListener(batchMatchesListener));
        oneAtATimeVM.addListener(new WeakInvalidationListener(oneAtATimeListener));
        menteeVM.addListener(new WeakInvalidationListener(menteeListener));
        mentorVM.addListener(new WeakInvalidationListener(mentorListener));
        matchPane.getDividers().get(0).positionProperty()
                .bindBidirectional(personPane.getDividers().get(0).positionProperty());
        computedTable.getSelectionModel().selectedItemProperty().addListener(matchSelectionListener);
        manualTable.getSelectionModel().selectedItemProperty().addListener(matchSelectionListener);
    }
    
    private TableView<?> selectMatchTableToClear(PersonMatchViewModel match){
        //if match was from computedTable, unselect manualTable, and the other way around
        if(match == computedTable.getSelectionModel().selectedItemProperty().get()){
            return manualTable;
        } else {
            return computedTable;
        }
    }
    
    private void selectPersons(PersonMatchViewModel match, TableView<?> tableToClear){
        PersonViewModel selectedMentee = 
                menteeVM.getPersonViewModel(match, PersonType.MENTEE);
        TabularDataViewTools.selectAndScrollTo(menteeTable, selectedMentee);
        PersonViewModel selectedMentor = 
                mentorVM.getPersonViewModel(match, PersonType.MENTOR);
        TabularDataViewTools.selectAndScrollTo(mentorTable, selectedMentor);
        if(tableToClear != null){
            tableToClear.getSelectionModel().clearSelection();
        }
    }
    
    /**
     * Returns this view's selected person.
     * @param type determines which of the person list to check for selection
     * @return the selected person view model corresponding to the input type
     */
    public PersonViewModel getSelectedPerson(PersonType type){
        return switch(type){
            case MENTEE -> menteeTable.getSelectionModel().getSelectedItem();
            case MENTOR -> mentorTable.getSelectionModel().getSelectedItem();
        };
    }
    
    /**
     * Returns this view's selected manual match.
     * @return the match that is selected in the table corresponding to the manual matches.
     */
    public PersonMatchViewModel getSelectedManualMatch(){
        return manualTable.getSelectionModel().getSelectedItem();
    }
}

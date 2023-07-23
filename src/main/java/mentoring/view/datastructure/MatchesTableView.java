package mentoring.view.datastructure;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
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

package mentoring.view.datastructure;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import javax.inject.Inject;
import mentoring.viewmodel.base.TabularDataViewModel;
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
    
    private final PersonMatchesViewModel vm;
    private final PersonListViewModel menteeVM;
    private final PersonListViewModel mentorVM;
    private final InvalidationListener matchesListener;
    private final InvalidationListener menteeListener;
    private final InvalidationListener mentorListener;
    
    @Inject
    MatchesTableView(PersonMatchesViewModel computedVM, 
            PersonListViewModel menteeVM, PersonListViewModel mentorVM){
        this.vm = computedVM;
        this.menteeVM = menteeVM;
        this.mentorVM = mentorVM;
        //TODO refactor: when vm has been split, use non-deprecated version of updateTable
        matchesListener = observable -> {
            updateTable(computedTable, vm, e -> e.observableMatch());
            updateTable(manualTable, vm.getHeaders(), vm.getTransferredItems(), 
                    e -> e.observableMatch());
        };
        menteeListener = observable -> {
            updateTable(menteeTable, menteeVM, e -> e.getPersonData());
        };
        mentorListener = observable -> {
            updateTable(mentorTable, mentorVM, e -> e.getPersonData());
        };
    }
    
    /**
     * Returns this view's underlying view model for matches.
     */
    public PersonMatchesViewModel getMatchesViewModel(){
        return vm;
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
        vm.addListener(new WeakInvalidationListener(matchesListener));
        menteeVM.addListener(menteeListener);
        mentorVM.addListener(mentorListener);
        matchPane.getDividers().get(0).positionProperty()
                .bindBidirectional(personPane.getDividers().get(0).positionProperty());
    }
    @Deprecated
    private static <E> void updateTable(TableView<E> table, 
            List<String> headers, Collection<E> content,
            Function<E, Map<String, String>> propertyGetter){
        table.getColumns().clear();
        for (String header : headers){
            addColumn(table, header, p -> propertyGetter.apply(p.getValue()));
        }
        table.itemsProperty().get().setAll(content);
    }
    
    //TODO extract into tested utility class for TableView
    private static <E> void updateTable(TableView<E> table, TabularDataViewModel<E> viewModel,
            Function<E, Map<String, String>> propertyGetter){
        table.getColumns().clear();
        List<String> headers = viewModel.getHeaders();
        for (String header : headers){
            addColumn(table, header, p -> propertyGetter.apply(p.getValue()));
        }
        table.itemsProperty().get().setAll(viewModel.getContent());
    }
    
    //TODO extract into tested utility class for TableView
    private static <E> void addColumn(TableView<E> table, String header, 
            Callback<CellDataFeatures<E, String>, Map<String, String>> propertiesGetter){
        TableColumn<E, String> column = new TableColumn<>(header);
        column.setCellValueFactory(p -> new SimpleStringProperty(
                propertiesGetter.call(p).get(header)));
        table.getColumns().add(column);
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

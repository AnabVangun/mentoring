package mentoring.view.datastructure;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javax.inject.Inject;
import mentoring.viewmodel.datastructure.ForbiddenMatchListViewModel;
import mentoring.viewmodel.datastructure.ForbiddenMatchViewModel;

/**
 * View responsible for displaying a {@link ForbiddenMatchListViewModel} object.
 */
public class ForbiddenMatchesView implements Initializable{
    //TODO document class
    @FXML
    private TableView<ForbiddenMatchViewModel> forbiddenMatchesTable;
    @FXML
    private Button forbiddenMatchRemovalButton;
    
    @Inject
    public ForbiddenMatchesView(){}
    
    @Override
    @SuppressWarnings("unchecked")
    public void initialize(URL url, ResourceBundle rb){
        //TODO internationalise string
        TableColumn<ForbiddenMatchViewModel, String> menteeColumn = new TableColumn<>("Mentee");
        menteeColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getMenteeName()));
        TableColumn<ForbiddenMatchViewModel, String> mentorColumn = new TableColumn<>("Mentor");
        mentorColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getMentorName()));
        forbiddenMatchesTable.getColumns().setAll(menteeColumn, mentorColumn);
    }
    
    public void setViewModel(ForbiddenMatchListViewModel vm){
        forbiddenMatchesTable.setItems(vm.getContent());
        //TODO move action to ConcurrencyHandler
        forbiddenMatchRemovalButton.setText("Allow match");
        forbiddenMatchRemovalButton.setOnAction(event -> 
                vm.removeForbiddenMatch(forbiddenMatchesTable.getSelectionModel().getSelectedItem()));
    }
}

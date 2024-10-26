package mentoring.view.datastructure;

import jakarta.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import mentoring.viewmodel.datastructure.ForbiddenMatchListViewModel;
import mentoring.viewmodel.datastructure.ForbiddenMatchViewModel;

/**
 * View responsible for displaying a {@link ForbiddenMatchListViewModel} object.
 */
public class ForbiddenMatchesView implements Initializable{
    @FXML
    private TableView<ForbiddenMatchViewModel> forbiddenMatchesTable;
    @FXML
    private Button selectedButton;
    
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
        selectedButton.setText("Allow match");
    }
    
    /**
     * Set the ViewModel underlying this model.
     * @param viewModel the new ViewModel to use
     */
    public void setViewModel(ForbiddenMatchListViewModel viewModel){
        forbiddenMatchesTable.setItems(viewModel.getContent());
    }
    
    /**
     * Set the handler managing the selected item button.
     * @param handler to attach to the selected item button
     */
    public void setRemovalButtonAction(BiConsumer<ActionEvent, ForbiddenMatchViewModel> handler){
        selectedButton.setOnAction(event -> handler.accept(event, 
                forbiddenMatchesTable.getSelectionModel().getSelectedItem()));
    }
}

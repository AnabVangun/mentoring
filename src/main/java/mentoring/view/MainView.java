package mentoring.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javax.inject.Inject;
import mentoring.view.datastructure.MatchesTableView;
import mentoring.viewmodel.MainViewModel;

public class MainView implements Initializable {
    
    private final MainViewModel vm;
    
    @FXML
    private MatchesTableView tableViewController;
    
    @FXML
    private Button runButton;
    
    @Inject
    MainView(MainViewModel vm){
        this.vm = vm;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO internationalize string
        runButton.textProperty().set("Run");
        runButton.setOnAction(event -> vm.makeMatches(tableViewController.getViewModel()));
    }    
}

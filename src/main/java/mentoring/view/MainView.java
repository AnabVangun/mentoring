package mentoring.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javax.inject.Inject;
import mentoring.view.datastructure.MatchesTableView;
import mentoring.viewmodel.MainViewModel;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.datastructure.PersonType;

public class MainView implements Initializable {
    
    private final MainViewModel vm;
    
    @FXML
    private MatchesTableView tableViewController;
    @FXML
    private Button runButton;
    @FXML
    private Button addManualMatchButton;
    @FXML
    private Button deleteManualMatchButton;
    
    @Inject
    MainView(MainViewModel vm){
        this.vm = vm;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO internationalize string
        RunConfiguration data = RunConfiguration.TEST;
        vm.getPersons(tableViewController.getPersonViewModel(PersonType.MENTEE), 
                data, PersonType.MENTEE);
        vm.getPersons(tableViewController.getPersonViewModel(PersonType.MENTOR), 
                data, PersonType.MENTOR);
        runButton.textProperty().set("Run");
        runButton.setOnAction(event -> vm.makeMatches(
                tableViewController.getPersonViewModel(PersonType.MENTEE),
                tableViewController.getPersonViewModel(PersonType.MENTOR),
                tableViewController.getMatchesViewModel(), data));
        addManualMatchButton.textProperty().set("Set as match");
        //FIXME: manualMatchButton should only be enabled when a mentee and a mentor have been selected
        addManualMatchButton.setOnAction(event -> vm.makeSingleMatch(
                //FIXME: protect against illegal use: no selection, confirmation for multiple selection...
                tableViewController.getSelectedPerson(PersonType.MENTEE),
                tableViewController.getSelectedPerson(PersonType.MENTOR), 
                tableViewController.getMatchesViewModel(), data));
        deleteManualMatchButton.textProperty().set("Delete manual match");
        deleteManualMatchButton.setOnAction(event -> vm.removeSingleMatch(
                tableViewController.getSelectedManualMatch(), 
                tableViewController.getMatchesViewModel()));
    }    
}

package mentoring.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javax.inject.Inject;
import mentoring.viewmodel.MainViewModel;

public class MainView implements Initializable {
    
    private final MainViewModel vm;
    
    @FXML
    private TextArea textarea;
    
    @Inject
    MainView(MainViewModel vm){
        this.vm = vm;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textarea.textProperty().bind(vm.status);
        vm.makeMatches();
    }    
}

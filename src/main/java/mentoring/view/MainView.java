package mentoring.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import mentoring.concurrency.ConcurrencyHandler;
import mentoring.viewmodel.MainViewModel;

public class MainView implements Initializable {
    
    @FXML
    private TextArea textarea;
    
    private final MainViewModel vm = new MainViewModel();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textarea.textProperty().bind(vm.status);
        vm.makeMatches(ConcurrencyHandler.globalHandler);
    }    
}

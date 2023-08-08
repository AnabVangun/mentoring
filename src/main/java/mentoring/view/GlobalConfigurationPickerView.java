package mentoring.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import mentoring.view.base.ConfigurationPickerView;
import mentoring.view.base.FilePickerView;
import mentoring.view.base.ViewTools;

/**
 * View used to pick the global configuration of the application.
 */
public class GlobalConfigurationPickerView implements Initializable{
    //TODO internationalise String
    //TODO document class
    @FXML
    private Button configurationValidationButton;
    @FXML
    private FilePickerView menteeSourceController;
    @FXML
    private Label menteeSourceLabel;
    @FXML
    private ConfigurationPickerView resultConfigurationController;
    @FXML
    private Label resultConfigurationLabel;
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        menteeSourceLabel.setText("Mentee source");
        resultConfigurationLabel.setText("Result configuration");
    }
    
    public FilePickerView getMenteeSourceView(){
        return menteeSourceController;
    }
    
    public ConfigurationPickerView getResultConfigurationView(){
        return resultConfigurationController;
    }
    
    public void setValidationAction(Runnable action){
        ViewTools.configureButton(configurationValidationButton, "Validate",
                event -> {
                    action.run();
                    ViewTools.closeContainingWindow(configurationValidationButton);
                });
    }
}

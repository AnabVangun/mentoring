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
import mentoring.viewmodel.datastructure.PersonType;

/**
 * View used to pick the global configuration of the application.
 * <p> This class is not thread-safe.
 */
public class GlobalConfigurationPickerView implements Initializable{
    //TODO internationalise String
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
    
    /**
     * Return the view used to select a person list.
     * @param type of persons selected by the requested view
     * @return the FilePickerView used to select the data source for the input type of persons
     */
    public FilePickerView getPersonSourceView(PersonType type){
        return switch(type) {
            case MENTEE -> menteeSourceController;
            default -> throw new UnsupportedOperationException(
                    "Person type %s not supported yet".formatted(type));
        };
    }
    
    /**
     * Return the view used to configure how results should be displayed.
     * @return the requested view.
     */
    public ConfigurationPickerView getResultConfigurationView(){
        return resultConfigurationController;
    }
    
    /**
     * Set the action to perform before closing the configuration window.
     * The action MUST include its own multi-threading mechanism if any is requested: if the call is
     * blocking, it will block the view before closing it.
     * @param action to perform as the window is validated.
     */
    public void setValidationAction(Runnable action){
        ViewTools.configureButton(configurationValidationButton, "Validate",
                event -> {
                    action.run();
                    ViewTools.closeContainingWindow(configurationValidationButton);
                });
    }
}

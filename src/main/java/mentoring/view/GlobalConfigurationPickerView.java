package mentoring.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
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
    private ConfigurationPickerView menteeConfigurationController;
    @FXML
    private Label menteeConfigurationLabel;
    @FXML
    private FilePickerView mentorSourceController;
    @FXML
    private Label mentorSourceLabel;
    @FXML
    private ConfigurationPickerView mentorConfigurationController;
    @FXML
    private Label mentorConfigurationLabel;
    @FXML
    private ConfigurationPickerView matchConfigurationController;
    @FXML
    private Label matchConfigurationLabel;
    @FXML
    private ConfigurationPickerView resultConfigurationController;
    @FXML
    private Label resultConfigurationLabel;
    @FXML
    private ConfigurationPickerView exportConfigurationController;
    @FXML
    private Label exportConfigurationLabel;
    @FXML
    private ToggleButton exportConfigurationToggle;
    private final ChangeListener<Boolean> exportConfigurationToggleListener = 
            (observable, old, current) -> {
                if (! old && current){
                    exportConfigurationController.getViewModel()
                            .bind(resultConfigurationController.getViewModel());
                    exportConfigurationController.disableProperty().set(true);
                } else if (old && ! current){
                    exportConfigurationController.getViewModel()
                            .unbind(resultConfigurationController.getViewModel());
                    exportConfigurationController.disableProperty().set(false);
                } else {
                    throw new IllegalStateException("Toggle " + observable 
                            + " fired a changed event without changing");
                }
            };
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        menteeSourceLabel.setText("Mentee source");
        menteeConfigurationLabel.setText("Mentee configuration");
        mentorSourceLabel.setText("Mentor source");
        mentorConfigurationLabel.setText("Mentor configuration");
        matchConfigurationLabel.setText("Match configuration");
        resultConfigurationLabel.setText("Result configuration");
        exportConfigurationLabel.setText("Export configuration");
        exportConfigurationToggle.setText("Identical to result configuration");
        exportConfigurationToggle.selectedProperty()
                .addListener(new WeakChangeListener<>(exportConfigurationToggleListener));
    }
    
    /**
     * Return the view used to select a person list.
     * @param type of persons selected by the requested view
     * @return the FilePickerView used to select the data source for the input type of persons
     */
    public FilePickerView getPersonSourceView(PersonType type){
        return switch(type) {
            case MENTEE -> menteeSourceController;
            case MENTOR -> mentorSourceController;
            default -> throw new UnsupportedOperationException(
                    "Person type %s not supported yet".formatted(type));
        };
    }
    
    /**
     * Return the view used to configure how persons should be displayed.
     * @param type of persons configured by the requested view
     * @return the requested view.
     */
    public ConfigurationPickerView getPersonConfigurationView(PersonType type){
        return switch(type) {
            case MENTEE -> menteeConfigurationController;
            case MENTOR -> mentorConfigurationController;
            default -> throw new UnsupportedOperationException(
                    "Person type %s not supported yet".formatted(type));
        };
    }
    
    /**
     * Return the view used to configure how to match the mentees and mentors.
     * @return the requested view
     */
    public ConfigurationPickerView getMatchConfigurationView(){
        return matchConfigurationController;
    }
    
    /**
     * Return the view used to configure how results should be displayed.
     * @return the requested view
     */
    public ConfigurationPickerView getResultConfigurationView(){
        return resultConfigurationController;
    }
    
    /**
     * Return the view used to configure how results should be exported.
     * @return the requested view
     */
    public ConfigurationPickerView getExportConfigurationView(){
        return exportConfigurationController;
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

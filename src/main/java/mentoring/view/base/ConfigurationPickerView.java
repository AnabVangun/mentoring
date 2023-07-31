package mentoring.view.base;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.binding.ObjectBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.base.ConfigurationPickerViewModel.ConfigurationType;

/**
 * View responsible for selecting a configuration.
 */
public class ConfigurationPickerView implements Initializable{
    
    @FXML
    private ComboBox<String> configurationSelector;
    @FXML
    private TextField configurationFileSelector;
    @FXML
    private ToggleGroup configurationSelectionGroup;
    @FXML
    private Button configurationFileButton;
    @FXML
    private Button configurationValidationButton;
    @FXML
    private RadioButton knownConfigurationRadioButton;
    @FXML
    private RadioButton fileConfigurationRadioButton;
    
    private final ConfigurationPickerViewModel<?> viewModel;
    private final FileChooser chooser;
    private final Consumer<ConfigurationPickerViewModel<?>> validationButtonAction;
    private final Map<Toggle, ConfigurationType> configurationTypeMap = new HashMap<>();
    
    ConfigurationPickerView(ConfigurationPickerViewModel<?> viewModel,
            Consumer<ConfigurationPickerViewModel<?>> validationButtonAction){
        this.viewModel = viewModel;
        chooser = ViewTools.configureFileChooser("Choose configuration file",
            List.of(new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                    new FileChooser.ExtensionFilter("All files", "*.*")));
        chooser.setInitialDirectory(viewModel.getCurrentFile().getValue().getParentFile());
        this.validationButtonAction = validationButtonAction;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO make sure toggle group is properly updated
        //TODO internationalise strings
        configureTypeMap();
        configurationSelector.setItems(viewModel.getContent());
        configurationSelector.valueProperty().bindBidirectional(viewModel.getSelectedItem());
        configurationFileSelector.textProperty().bind(viewModel.getCurrentFilePath());
        viewModel.getConfigurationSelectionType().bind(typeOfConfigurationBinding);
        ViewTools.configureButton(configurationFileButton, "Pick file", event -> {
            File inputFile = chooser.showOpenDialog(
                    ((Node) event.getSource()).getScene().getWindow());
            viewModel.setCurrentFile(inputFile);
        });
        ViewTools.configureButton(configurationValidationButton, "Validate", event ->
                validationButtonAction.accept(viewModel));
    }
    
    private void configureTypeMap(){
        configurationTypeMap.put(knownConfigurationRadioButton, ConfigurationType.KNOWN);
        configurationTypeMap.put(fileConfigurationRadioButton, ConfigurationType.FILE);
    }
    
    //TODO extract and refactor
    private final ObjectBinding<ConfigurationType> typeOfConfigurationBinding = new ObjectBinding<>(){
        {
            super.bind(configurationSelectionGroup.selectedToggleProperty());
        }
        @Override
        protected ConfigurationType computeValue(){
            Toggle toggle = configurationSelectionGroup.selectedToggleProperty().getValue();
            if(configurationTypeMap.containsKey(toggle)){
                return configurationTypeMap.get(toggle);
            } else {
                throw new UnsupportedOperationException("Toggle " + toggle.toString() + " is unknown");
            }
        }
    };
}

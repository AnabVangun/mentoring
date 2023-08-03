package mentoring.view.base;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import javafx.stage.Stage;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.base.ConfigurationPickerViewModel.ConfigurationType;

/**
 * View responsible for selecting a configuration.
 */
public class ConfigurationPickerView implements Initializable{
    //TODO: make it easy to select one of the example configurations from resources.
    
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
    private ObjectBinding<ConfigurationType> typeOfConfigurationBinding;
    
    private final ConfigurationPickerViewModel<?> viewModel;
    private final FileChooser chooser;
    private final Consumer<ConfigurationPickerViewModel<?>> validationButtonAction;
    private final Map<Toggle, ConfigurationType> configurationTypeMap = new HashMap<>();
    
    public ConfigurationPickerView(ConfigurationPickerViewModel<?> viewModel,
            Consumer<ConfigurationPickerViewModel<?>> validationButtonAction){
        this.viewModel = viewModel;
        chooser = ViewTools.createFileChooser("Choose configuration file",
            List.of(new FileChooser.ExtensionFilter("YAML Files", "*.yaml"),
                    new FileChooser.ExtensionFilter("All files", "*.*")));
        chooser.initialDirectoryProperty().bind(viewModel.getCurrentFileDirectory());
        this.validationButtonAction = validationButtonAction;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO make sure toggle group is properly updated
        //TODO internationalise strings
        configureTypeMap();
        configurationSelector.setItems(viewModel.getKnownContent());
        configurationSelector.valueProperty().bindBidirectional(viewModel.getSelectedItem());
        configurationFileSelector.textProperty().bind(viewModel.getCurrentFilePath());
        configurationSelectionGroup.selectToggle(
                switch(viewModel.getConfigurationSelectionType().getValue()) {
                    case KNOWN -> knownConfigurationRadioButton;
                    case FILE -> fileConfigurationRadioButton;
                });
        typeOfConfigurationBinding = forgeConfigurationTypeGetterBinding(
                configurationSelectionGroup, configurationTypeMap);
        viewModel.getConfigurationSelectionType().bind(typeOfConfigurationBinding);
        ViewTools.configureButton(configurationFileButton, "Pick file", event -> {
            File inputFile = chooser.showOpenDialog(
                    ((Node) event.getSource()).getScene().getWindow());
            if(inputFile != null){
                viewModel.setCurrentFile(inputFile);
            }
        });
        //TODO refactor: extract method to ViewTools
        ViewTools.configureButton(configurationValidationButton, "Validate", event ->
                validationButtonAction
                        .andThen(input -> ((Stage) configurationValidationButton.getScene().getWindow()).close())
                        .accept(viewModel));
    }
    
    private void configureTypeMap(){
        configurationTypeMap.put(knownConfigurationRadioButton, ConfigurationType.KNOWN);
        configurationTypeMap.put(fileConfigurationRadioButton, ConfigurationType.FILE);
    }
    
    static ObjectBinding<ConfigurationType> forgeConfigurationTypeGetterBinding(ToggleGroup group, 
            Map<Toggle, ConfigurationType> map){
        Objects.requireNonNull(map);
        return new ObjectBinding<>(){
            {
                super.bind(group.selectedToggleProperty());
            }
            @Override
            protected ConfigurationType computeValue(){
                Toggle toggle = group.selectedToggleProperty().getValue();
                if (toggle == null){
                    throw new IllegalStateException("Illegal state: no toggle is selected");
                }
                if(map.containsKey(toggle)){
                    return map.get(toggle);
                } else {
                    throw new UnsupportedOperationException("Toggle " + toggle.toString() + " is unknown");
                }
            }
        };
    }
}

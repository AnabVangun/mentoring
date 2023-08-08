package mentoring.view.base;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
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
    private ToggleGroup configurationSelectionGroup;
    @FXML
    private RadioButton knownConfigurationRadioButton;
    @FXML
    private RadioButton fileConfigurationRadioButton;
    @FXML
    private FilePickerView fileSelectionViewController;
    private ObjectBinding<ConfigurationType> typeOfConfigurationBinding;
    
    private final InvalidationListener fileRadioButtonSelector = observable -> 
            configurationSelectionGroup.selectToggle(fileConfigurationRadioButton);
    
    private ConfigurationPickerViewModel<?> viewModel;
    private final Map<Toggle, ConfigurationType> configurationTypeMap = new HashMap<>();
    
    public void setViewModel(ConfigurationPickerViewModel<?> viewModel) {
        //TODO internationalise strings
        //TODO refactor to explicit structure
        this.viewModel = viewModel;
        configurationSelector.setItems(viewModel.getKnownContent());
        configurationSelector.valueProperty().bindBidirectional(viewModel.getSelectedItem());
        configurationSelector.setOnMouseClicked(event -> 
                configurationSelectionGroup.selectToggle(knownConfigurationRadioButton));
        configurationSelectionGroup.selectToggle(
                switch(viewModel.getConfigurationSelectionType().getValue()) {
                    case KNOWN -> knownConfigurationRadioButton;
                    case FILE -> fileConfigurationRadioButton;
                });
        typeOfConfigurationBinding = forgeConfigurationTypeGetterBinding(
                configurationSelectionGroup, configurationTypeMap);
        viewModel.getConfigurationSelectionType().bind(typeOfConfigurationBinding);
        fileSelectionViewController.setViewModel(viewModel.getFilePicker());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTypeMap();
        fileSelectionViewController.addListener(new WeakInvalidationListener(fileRadioButtonSelector));
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
                    throw new UnsupportedOperationException("Toggle " + toggle.toString() +
                            " is unknown");
                }
            }
        };
    }
}

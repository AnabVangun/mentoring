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
import static mentoring.viewmodel.base.ConfigurationPickerViewModel.ConfigurationType.FILE;
import static mentoring.viewmodel.base.ConfigurationPickerViewModel.ConfigurationType.KNOWN;

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
    
    private final InvalidationListener fileRadioButtonSelector = observable -> 
            configurationSelectionGroup.selectToggle(fileConfigurationRadioButton);
    
    private final Map<Toggle, ConfigurationType> configurationTypeMap = new HashMap<>();
    
    public void setViewModel(ConfigurationPickerViewModel<?> viewModel) {
        //TODO internationalise strings
        bindKnownConfigurationSelectorToViewModel(viewModel);
        initialiseToggleGroup(viewModel.getConfigurationSelectionType().getValue());
        bindConfigurationTypeToGui(viewModel);
        fileSelectionViewController.setViewModel(viewModel.getFilePicker());
    }
    
    private void bindKnownConfigurationSelectorToViewModel(ConfigurationPickerViewModel<?> viewModel){
        configurationSelector.setItems(viewModel.getKnownContent());
        configurationSelector.valueProperty().bindBidirectional(viewModel.getSelectedItem());
    }
    
    private void initialiseToggleGroup(ConfigurationType type){
        configurationSelectionGroup.selectToggle(
                switch(type) {
                    case KNOWN -> knownConfigurationRadioButton;
                    case FILE -> fileConfigurationRadioButton;
                });
    }
    
    private void bindConfigurationTypeToGui(ConfigurationPickerViewModel<?> viewModel){
        configurationSelector.setOnMouseClicked(event -> 
                configurationSelectionGroup.selectToggle(knownConfigurationRadioButton));
        fileSelectionViewController.addListener(new WeakInvalidationListener(fileRadioButtonSelector));
        ObjectBinding<ConfigurationType> typeOfConfigurationBinding = 
                forgeConfigurationTypeGetterBinding(
                        configurationSelectionGroup, configurationTypeMap);
        viewModel.getConfigurationSelectionType().bind(typeOfConfigurationBinding);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTypeMap();
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

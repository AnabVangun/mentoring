package mentoring.view.base;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import mentoring.viewmodel.base.BoundableConfigurationPickerViewModel;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.base.ConfigurationPickerViewModel.ConfigurationType;

/**
 * View responsible for selecting a configuration.
 * Users of this view SHOULD call 
 * {@link #setViewModel(mentoring.viewmodel.base.ConfigurationPickerViewModel) } before adding this
 * view to the stage.
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
    
    private ConfigurationPickerViewModel<?> viewModel;
    
    private InvalidationListener fileRadioButtonSelector;
    private InvalidationListener toggleGroupSelector;
    
    private final Map<Toggle, ConfigurationType> toggleToTypeMap = new HashMap<>();
    private final Map<ConfigurationType, Toggle> typeToToggleMap = new HashMap<>();
    private final BooleanProperty disableProperty = new SimpleBooleanProperty(false);
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeMaps();
        makeBindingsToDisable();
    }
    
    private void initializeMaps(){
        toggleToTypeMap.put(knownConfigurationRadioButton, ConfigurationType.KNOWN);
        toggleToTypeMap.put(fileConfigurationRadioButton, ConfigurationType.FILE);
        
        toggleToTypeMap.forEach((toggle, type) -> typeToToggleMap.put(type, toggle));
    }
    
    private void makeBindingsToDisable(){
        configurationSelector.disableProperty().bind(disableProperty);
        knownConfigurationRadioButton.disableProperty().bind(disableProperty);
        fileConfigurationRadioButton.disableProperty().bind(disableProperty);
        fileSelectionViewController.disableProperty().bind(disableProperty);
    }
    
    /**
     * Set the ViewModel underlying this model.
     * @param viewModel the new ViewModel to use
     */
    public void setViewModel(ConfigurationPickerViewModel<?> viewModel) {
        //TODO internationalise strings
        this.viewModel = viewModel;
        initializeListeners();
        bindKnownConfigurationSelectorToViewModel();
        bindConfigurationTypeSelectorToViewModel();
        bindViewModelToUserActionOnConfigurationTypeSelector();
        fileSelectionViewController.setViewModel(viewModel.getFilePicker());
    }
    
    /**
     * Get the ViewModel underlying this model.
     * @return the ViewModel in use
     */
    public ConfigurationPickerViewModel<?> getViewModel(){
        return viewModel;
    }
    
    private void initializeListeners(){
        ObjectBinding<Toggle> toggleSelectedBinding =
                forgeToggleGetterBinding(viewModel.getConfigurationSelectionType(), typeToToggleMap);
        toggleGroupSelector = observable -> 
                configurationSelectionGroup.selectToggle(toggleSelectedBinding.get());
        fileRadioButtonSelector = observable -> 
                setConfigurationType(fileConfigurationRadioButton);
    }
    
    private void bindKnownConfigurationSelectorToViewModel(){
        configurationSelector.setItems(viewModel.getKnownContent());
        configurationSelector.valueProperty().bindBidirectional(viewModel.getSelectedItem());
    }
    
    private void bindConfigurationTypeSelectorToViewModel(){
        viewModel.getConfigurationSelectionType()
                .addListener(new WeakInvalidationListener(toggleGroupSelector));
        //Set value of toggle group to that of the view model
        toggleGroupSelector.invalidated(viewModel.getConfigurationSelectionType());
    }
    
    private void bindViewModelToUserActionOnConfigurationTypeSelector(){
        for(Toggle toggle : configurationSelectionGroup.getToggles()){
            if(toggle instanceof ToggleButton button){
                button.setOnAction(event -> setConfigurationType(button));
            } else {
                throw new IllegalStateException("Toggle group " + configurationSelectionGroup 
                        + " contains toggles that are not buttons, found " + toggle);
            }
        }
        configurationSelector.setOnMouseClicked(event -> 
                setConfigurationType(knownConfigurationRadioButton));
        fileSelectionViewController.addListener(new WeakInvalidationListener(fileRadioButtonSelector));
    }
    
    private void setConfigurationType(Toggle toggle){
        viewModel.getConfigurationSelectionType().setValue(toggleToTypeMap.get(toggle));
    }
    
    @Deprecated
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
    
    static ObjectBinding<Toggle> forgeToggleGetterBinding(Property<ConfigurationType> typeProperty, 
            Map<ConfigurationType, Toggle> map){
        Objects.requireNonNull(map);
        return new ObjectBinding<>(){
            {
                super.bind(typeProperty);
            }
            
            @Override
            protected Toggle computeValue(){
                ConfigurationType type = typeProperty.getValue();
                if (type == null){
                    throw new IllegalStateException(
                            "Illegal state: no configuration type is selected");
                }
                if(map.containsKey(type)){
                    return map.get(type);
                } else {
                    throw new UnsupportedOperationException("Type " + type.toString() +
                            " is unknown");
                }
            }
        };
    }
    
    /**
     * Bind this view to its twin if it has one. This view has a twin if its underlying ViewModel
     * is a {@link BoundableConfigurationPickerViewModel}. If the operation is successful, this 
     * view becomes disabled.
     * @return true if the underlying ViewModel can be bounded and has been so.
     */
    public boolean bind(){
        if(viewModel instanceof BoundableConfigurationPickerViewModel boundable){
            boundable.bind();
            disableProperty.set(true);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Unbind this view from its twin if it has one. This view has a twin if its underlying 
     * ViewModel is a {@link BoundableConfigurationPickerViewModel}. If the operation is successful, 
     * this view becomes enabled.
     * @return true if the underlying ViewModel can be unbounded and has been so.
     */
    public boolean unbind(){
        if(viewModel instanceof BoundableConfigurationPickerViewModel boundable){
            boundable.unbind();
            disableProperty.set(false);
            return true;
        } else {
            return false;
        }
    }
}

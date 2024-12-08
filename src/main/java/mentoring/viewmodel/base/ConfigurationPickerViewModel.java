package mentoring.viewmodel.base;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mentoring.configuration.Configuration;
import mentoring.viewmodel.base.function.ConfigurationTypeFunction;

/**
 * ViewModel used to pick a new configuration.
 * @param <T> type of the configuration to pick
 */
public class ConfigurationPickerViewModel<T extends Configuration<T>> {
    protected final Map<String, T> knownConfigurations;
    protected final ObservableList<String> items;
    protected final Property<String> selectedItem;
    protected final Property<ConfigurationType> configurationType;
    protected final FilePickerViewModel<T> filePicker;
    private T lastLoadedConfiguration = null;
    private ConfigurationType lastLoadedConfigurationType = null;
    private String lastLoadedConfigurationParameter = null;
    
    /**
     * Type of configuration to pick.
     */
    public static enum ConfigurationType{
        /** The configuration is already a Java object known to the ConfigurationPickerViewModel.*/
        KNOWN(ConfigurationType::getKnownConfiguration, ConfigurationType::getSelectedItemParameter),
        /**The configuration must be parsed from a file.*/
        FILE(ConfigurationType::getConfigurationFromFile, ConfigurationType::getSelectedFileParameter);
        
        private final ConfigurationTypeFunction configurationGetter;
        private final Function<ConfigurationPickerViewModel<?>, String> parameterGetter;
        
        private ConfigurationType(ConfigurationTypeFunction configurationGetter, 
                Function<ConfigurationPickerViewModel<?>, String> parameterGetter){
            this.configurationGetter = configurationGetter;
            this.parameterGetter = parameterGetter;
        }
        
        <T extends Configuration<T>> T getConfiguration(
                ConfigurationPickerViewModel<T> viewModel) throws IOException {
            return configurationGetter.getConfiguration(viewModel);
        }
        
        String getParameter(ConfigurationPickerViewModel<?> viewModel){
            return parameterGetter.apply(viewModel);
        }
        
        private static <T extends Configuration<T>> T getKnownConfiguration(
                ConfigurationPickerViewModel<T> viewModel){
            String key = viewModel.selectedItem.getValue();
            T result = viewModel.knownConfigurations.get(key);
            if (result == null){
                throw new IllegalStateException(
                        "Tried to get a known configuration with value %s, was not found in %s"
                                .formatted(key, viewModel.knownConfigurations));
            }
            return result;
        }

        private static <T extends Configuration<T>> T getConfigurationFromFile(
                ConfigurationPickerViewModel<T> viewModel) throws IOException{
            return viewModel.filePicker.parseCurrentFile();
        }
        
        private static <T extends Configuration<T>> String getSelectedItemParameter(
                ConfigurationPickerViewModel<T> viewModel){
            return viewModel.selectedItem.getValue();
        }
        
        private static <T extends Configuration<T>> String getSelectedFileParameter(
                ConfigurationPickerViewModel<T> viewModel){
            return viewModel.filePicker.getCurrentFilePath().get();
        }
    }
    
    /**
     * Build a new ConfigurationPickerViewModel instance.
     * @param defaultSelectedInstance the first selected item
     * @param knownInstances the values known to the ViewModel
     * @param filePicker ViewModel used to pick a configuration file
     * @param defaultSelection the default type of configuration picked by the picker
     */
    public ConfigurationPickerViewModel(T defaultSelectedInstance, List<T> knownInstances, 
            FilePickerViewModel<T> filePicker,
            ConfigurationType defaultSelection){
        this.filePicker = Objects.requireNonNull(filePicker);
        if(! knownInstances.contains(defaultSelectedInstance)){
            throw new IllegalArgumentException(
                    "Default instance %s is not in the known configurations"
                    .formatted(defaultSelectedInstance, knownInstances));
        }
        knownConfigurations = Collections.unmodifiableMap(knownInstances.stream().collect(
                Collectors.toMap(item -> item.toString(), Function.identity())));
        items = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(
                knownInstances.stream().map(item -> item.toString()).collect(Collectors.toList())));
        selectedItem = new SimpleStringProperty(defaultSelectedInstance.toString());
        configurationType = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(defaultSelection));
    }
    
    /**
     * Deep-copy constructor: build a new independent instance with equal values.
     * @param copy the ViewModel to copy
     */
    protected ConfigurationPickerViewModel(ConfigurationPickerViewModel<T> copy){
        this.filePicker = new FilePickerViewModel<>(copy.filePicker);
        knownConfigurations = copy.knownConfigurations;
        items = copy.items;
        selectedItem = new SimpleStringProperty(copy.selectedItem.getValue());
        configurationType = new ReadOnlyObjectWrapper<>(copy.configurationType.getValue());
        lastLoadedConfiguration = copy.lastLoadedConfiguration;
        lastLoadedConfigurationType = copy.lastLoadedConfigurationType;
        lastLoadedConfigurationParameter = copy.lastLoadedConfigurationParameter;
    }
    
    /**
     * Get the configurations known to the picker.
     * @return an unmodifiable list containing the strings representing the known configuration 
     *      among which to pick
     */
    public ObservableList<String> getKnownContent(){
        return items;
    }
    
    /**
     * Get the currently selected item from the known configurations.
     * @return a property containing the string representing the selected item in the known 
     *      configurations
     */
    public Property<String> getSelectedItem(){
        return selectedItem;
    }
    
    
    /**
     * Get the type of configuration currently selected.
     * @return a property describing which type of Configuration will be returned by a call to 
     *      {@link #getConfiguration()}
     */
    public Property<ConfigurationType> getConfigurationSelectionType(){
        return configurationType;
    }
    
    /**
     * Get the selected configuration. Depending on the value of 
     * {@link #getConfigurationSelectionType()}, the configuration may be already loaded in memory
     * or may be parsed from the selected file.
     * @return the expected configuration instance
     * @throws IOException if the configuration must be loaded from the file but the operation fails
     */
    public T getConfiguration() throws IOException {
        if(lastLoadedConfigurationType != null && 
                lastLoadedConfigurationType.equals(configurationType.getValue()) && 
                lastLoadedConfigurationParameter.equals(configurationType.getValue().getParameter(this))){
            return lastLoadedConfiguration;
        }
        T result = configurationType.getValue().getConfiguration(this);
        lastLoadedConfiguration = result;
        lastLoadedConfigurationType = configurationType.getValue();
        lastLoadedConfigurationParameter = configurationType.getValue().getParameter(this);
        return result;
    }
    
    /**
     * Get the FilePickerViewModel used to pick a configuration file by this ViewModel.
     * @return the internal FilePickerViewModel
     */
    public FilePickerViewModel<T> getFilePicker() {
        return this.filePicker;
    }
}

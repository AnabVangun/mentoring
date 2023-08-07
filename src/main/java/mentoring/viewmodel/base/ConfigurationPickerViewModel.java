package mentoring.viewmodel.base;

import java.io.IOException;
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
import mentoring.viewmodel.base.function.FileParser;

/**
 * ViewModel used to pick a new configuration.
 * @param <T> type of the configuration to pick
 */
public class ConfigurationPickerViewModel<T extends Configuration<T>> extends FilePickerViewModel<T> {
    private final Map<String, T> knownConfigurations;
    private final ObservableList<String> items;
    private final Property<String> selectedItem;
    private final Property<ConfigurationType> configurationType;
    
    /**
     * Type of configuration to pick.
     */
    public static enum ConfigurationType{
        /** The configuration is already a Java object known to its class's 
        {@link Configuration#values()}  method.*/
        KNOWN(ConfigurationType::getKnownConfiguration),
        /**The configuration must be parsed from a file.*/
        FILE(ConfigurationType::getConfigurationFromFile);
        
        private final ConfigurationTypeFunction function;
        
        private ConfigurationType(ConfigurationTypeFunction function){
            this.function = function;
        }
        
        <T extends Configuration<T>> T getConfiguration(
                ConfigurationPickerViewModel<T> viewModel) throws IOException {
            return function.getConfiguration(viewModel);
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
            return viewModel.parseCurrentFile();
        }
    }
    
    /**
     * Build a new ConfigurationPickerViewModel instance.
     * @param defaultSelectedInstance the first selected item
     * @param knownInstances the values known to the ViewModel
     * @param defaultFilePath path to a file that can be parsed as a configuration
     * @param defaultSelection the default type of configuration picked by the picker
     * @param parserSupplier to parse the configuration from a file if necessary
     */
    public ConfigurationPickerViewModel(T defaultSelectedInstance, List<T> knownInstances, 
            String defaultFilePath, 
            ConfigurationType defaultSelection, FileParser<T> parserSupplier){
        super(defaultFilePath, parserSupplier);
        if(! knownInstances.contains(defaultSelectedInstance)){
            throw new IllegalArgumentException(
                    "Default instance %s is not in the known configurations"
                    .formatted(defaultSelectedInstance, knownInstances));
        }
        knownConfigurations = knownInstances.stream().collect(
                Collectors.toMap(item -> item.toString(), Function.identity()));
        items = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(
                knownInstances.stream().map(item -> item.toString()).collect(Collectors.toList())));
        selectedItem = new SimpleStringProperty(defaultSelectedInstance.toString());
        configurationType = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(defaultSelection));
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
        return configurationType.getValue().getConfiguration(this);
    }
}

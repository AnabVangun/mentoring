package mentoring.viewmodel.base;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mentoring.configuration.Configuration;
import mentoring.viewmodel.base.function.ConfigurationTypeFunction;

/**
 * ViewModel used to pick a new configuration.
 * @param <T> type of the configuration to pick
 */
public class ConfigurationPickerViewModel<T extends Configuration<T>> {
    private final Map<String, T> knownConfigurations;
    private final ObservableList<String> items;
    private final Property<String> selectedItem;
    private final Property<ConfigurationType> configurationType;
    private final FilePickerViewModel<T> filePicker;
    private final ChangeListener<File> fileBindingListener = (observable, oldValue, newValue) -> 
                getFilePicker().setCurrentFile(newValue);
    private final ChangeListener<File> fileBindingWeakListener = 
            new WeakChangeListener<>(fileBindingListener);
    
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
            return viewModel.filePicker.parseCurrentFile();
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
        //TODO: if configuration has already been loaded and nothing has changed, return it from cache
        return configurationType.getValue().getConfiguration(this);
    }
    
    /**
     * Get the FilePickerViewModel used to pick a configuration file by this ViewModel.
     * @return the internal FilePickerViewModel
     */
    public FilePickerViewModel<T> getFilePicker() {
        return this.filePicker;
    }
    
    //TODO test and document
    public void bind(ConfigurationPickerViewModel<?> o){
        //TODO check that o is a valid candidate: it has the right type and content
        @SuppressWarnings("unchecked")
        //if o is not of the right type, an exception will be raised as expected
        ConfigurationPickerViewModel<T> other = (ConfigurationPickerViewModel<T>) o;
        configurationType.bind(other.configurationType);
        selectedItem.bind(other.selectedItem);
        other.filePicker.getCurrentFile().addListener(fileBindingWeakListener);
        //Fire event once to make sure that value is up to date
        fileBindingListener.changed(other.filePicker.getCurrentFile(), null, 
                other.filePicker.getCurrentFile().get());
    }
    
    //TODO test and document
    public void unbind(ConfigurationPickerViewModel<?> o){
        configurationType.unbind();
        selectedItem.unbind();
        o.filePicker.getCurrentFile().removeListener(fileBindingWeakListener);
    }
}

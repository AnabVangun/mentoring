package mentoring.viewmodel.base;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mentoring.configuration.Configuration;
import mentoring.viewmodel.base.function.ConfigurationParser;
import mentoring.viewmodel.base.function.ConfigurationTypeFunction;

/**
 * ViewModel used to pick a new configuration.
 * @param <T> type of the configuration to pick
 */
public class ConfigurationPickerViewModel<T extends Configuration<T>> {
    private final Map<String, T> knownConfigurations;
    private final ObservableList<String> items;
    private final Property<String> selectedItem;
    private final ReadOnlyStringWrapper selectedFilePath = new ReadOnlyStringWrapper();
    private final ReadOnlyObjectWrapper<File> selectedFile = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<File> selectedFileDirectory = new ReadOnlyObjectWrapper<>();
    private final Property<ConfigurationType> configurationType;
    private final ConfigurationParser<T> configurationParser;
    
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
            File file = viewModel.selectedFile.getValue();
            return viewModel.configurationParser.apply(file);
        }
    }
    
    /**
     * Build a new ConfigurationPickerViewModel instance.
     * @param defaultSelectedInstance serves two purposes: used to populate the list returned by
     * {@link ConfigurationPickerViewModel#getKnownContent()} and the first selected item
     * @param defaultFilePath path to a file that can be parsed as a configuration
     * @param defaultSelection the default type of configuration picked by the picker
     * @param parserSupplier to parse the configuration from a file if necessary
     */
    public ConfigurationPickerViewModel(T defaultSelectedInstance, String defaultFilePath, 
            ConfigurationType defaultSelection, ConfigurationParser<T> parserSupplier){
        List<T> configurations = defaultSelectedInstance.values();
        knownConfigurations = configurations.stream()
                .collect(Collectors.toMap(item -> item.toString(), Function.identity()));
        items = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(
                configurations.stream().map(item -> item.toString()).collect(Collectors.toList())));
        selectedItem = new SimpleStringProperty(defaultSelectedInstance.toString());
        setCurrentFile(getFileOrDefaultDirectory(defaultFilePath));
        configurationType = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(defaultSelection));
        this.configurationParser = Objects.requireNonNull(parserSupplier);
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
     * Get the currently selected file. This observable may be invalidated by calls to
     * {@link #setCurrentFile(java.io.File)}.
     * @return an observable describing the currently selected file
     */
    public ReadOnlyObjectProperty<File> getCurrentFile(){
        return selectedFile.getReadOnlyProperty();
    }
    
    /**
     * Select the input file. Calls to this method will invalidate the properties 
     * returned by {@link #getCurrentFile()} and {@link #getCurrentFilePath()} if appropriate.
     * @param file the file to select
     */
    public final void setCurrentFile(File file){
        File safeFile = getFileOrDefaultDirectory(file);
        selectedFile.set(safeFile);
        selectedFilePath.set(safeFile.getPath());
        selectedFileDirectory.set(safeFile.isFile() ? safeFile.getParentFile() : safeFile);
    }
    
    private static File getFileOrDefaultDirectory(String filePath){
        return getFileOrDefaultDirectory((File) (filePath == null ? null : new File(filePath)));
    }
    
    private static File getFileOrDefaultDirectory(File file){
        return (file == null || !file.exists()) ? new File(System.getProperty("user.home")) : file;
    }
    
    /**
     * Get an absolute path to the currently selected file. This observable may be invalidated by 
     * calls to {@link #setCurrentFile(java.io.File)}.
     * @return an observable describing an absolute path to the currently selected file
     */
    public ReadOnlyStringProperty getCurrentFilePath(){
        return selectedFilePath.getReadOnlyProperty();
    }
    
    /**
     * Get the directory containing the currently selected file. 
     * This observable may be invalidated by calls to {@link #setCurrentFile(java.io.File)}.
     * @return an observable describing the currently selected directory
     */
    public ReadOnlyObjectProperty<File> getCurrentFileDirectory(){
        return selectedFileDirectory.getReadOnlyProperty();
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

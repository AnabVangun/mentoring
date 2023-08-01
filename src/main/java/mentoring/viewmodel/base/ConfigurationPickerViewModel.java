package mentoring.viewmodel.base;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
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
import mentoring.io.Parser;
import mentoring.viewmodel.tasks.PersonGetter;

/**
 * ViewModel used to pick a new configuration.
 * @param <T> type of the configuration to pick
 * @param <E> type of the parser used to parse the configuration
 */
public class ConfigurationPickerViewModel<T extends Configuration<T>, E extends Parser<T>> {
    private final Map<String, T> knownConfigurations;
    private final ObservableList<String> items;
    private final Property<String> selectedItem;
    private final ReadOnlyStringWrapper selectedFilePath;
    private final ReadOnlyObjectWrapper<File> selectedFile;
    private final Property<ConfigurationType> configurationType;
    private final ConfigurationParserSupplier<T,E> parserSupplier;
    private final PersonGetter.ReaderGenerator readerGenerator;
    
    /**
     * Type of configuration to pick.
     */
    public static enum ConfigurationType{
        /** The configuration is already a Java object known to its class's 
        {@link Configuration#values()}  method.*/
        KNOWN,
        /**The configuration must be parsed from a file.*/
        FILE;
    }
    
    /**
     * Build a new ConfigurationPickerViewModel instance.
     * @param defaultSelectedInstance serves two purposes: used to populate the list returned by
     * {@link ConfigurationPickerViewModel#getKnownContent()} and the first selected item
     * @param defaultFilePath path to a file that can be parsed as a configuration, or an empty 
     * string
     * @param defaultSelection the default type of configuration picked by the picker
     * @param parserSupplier to generate the parser used to parse the configuration from a file
     *      if necessary
     * @param readerGenerator to generate the reader used to read the data
     */
    public ConfigurationPickerViewModel(T defaultSelectedInstance, String defaultFilePath, 
            ConfigurationType defaultSelection, ConfigurationParserSupplier<T,E> parserSupplier, 
            PersonGetter.ReaderGenerator readerGenerator){
        List<T> configurations = defaultSelectedInstance.values();
        knownConfigurations = configurations.stream()
                .collect(Collectors.toMap(item -> item.toString(), Function.identity()));
        items = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(
                configurations.stream().map(item -> item.toString()).collect(Collectors.toList())));
        selectedItem = new SimpleStringProperty(defaultSelectedInstance.toString());
        selectedFilePath = new ReadOnlyStringWrapper(Objects.requireNonNull(defaultFilePath));
        selectedFile = new ReadOnlyObjectWrapper<>(new File(defaultFilePath));
        configurationType = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(defaultSelection));
        this.parserSupplier = Objects.requireNonNull(parserSupplier);
        this.readerGenerator = Objects.requireNonNull(readerGenerator);
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
    public void setCurrentFile(File file){
        selectedFile.set(Objects.requireNonNull(file));
        selectedFilePath.set(file.getAbsolutePath());
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
        ConfigurationType type = configurationType.getValue();
        return switch(type){
            case KNOWN -> getKnownConfiguration();
            case FILE -> getConfigurationFromFile();
            default -> throw new UnsupportedOperationException(
                    "Unsupported ConfigurationType: %s".formatted(type)); 
        };
    }
    
    private T getKnownConfiguration(){
        String key = selectedItem.getValue();
        T result = knownConfigurations.get(key);
        if (result == null){
            throw new IllegalStateException(
                    "Tried to get a known configuration with value %s, was not found in %s"
                            .formatted(key, knownConfigurations));
        }
        return result;
    }
    
    private T getConfigurationFromFile() throws IOException{
        File file = selectedFile.getValue();
        E parser = parserSupplier.get();
        return parser.parse(readerGenerator.generate(file.getAbsolutePath()));
    }
    
    //TODO refactor, move to mentoring.view.base
    /**
     * Represents an operation that accepts a single input argument and returns a {@link Parser} for
     * the appropriate configuration.
     * @param <T> type of the configuration to parse
     * @param <E> type of the returned parser
     */
    @FunctionalInterface
    public static interface ConfigurationParserSupplier<T extends Configuration<T>, 
            E extends Parser<T>> extends Supplier<E>{}
}

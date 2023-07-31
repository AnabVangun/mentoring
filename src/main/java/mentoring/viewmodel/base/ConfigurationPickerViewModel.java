package mentoring.viewmodel.base;

import java.io.File;
import java.util.List;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mentoring.configuration.Configuration;

/**
 * ViewModel used to pick a new configuration.
 * @param <T> type of the configuration to pick
 */
public class ConfigurationPickerViewModel<T extends Configuration<T>> {
    private final List<Configuration<T>> knownConfigurations;
    private final ObservableList<String> items;
    private String filePath;
    //TODO document, test and implement class
    
    public static enum ConfigurationType{
        KNOWN,
        FILE;
    }
    
    public ConfigurationPickerViewModel(T defaultSelectedInstance, String defaultFilePath, 
            ConfigurationType defaultSelection){
        //knownConfigurations = FXCollections.observableArrayList(defaultSelectedInstance.values());
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    public ObservableList<String> getContent(){
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    public Property<String> getSelectedItem(){
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    public Property<File> getCurrentFile(){
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    public void setCurrentFile(File file){
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    public Property<String> getCurrentFilePath(){
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    public Property<ConfigurationType> getConfigurationSelectionType(){
        throw new UnsupportedOperationException("not implemented yet");
    }
}

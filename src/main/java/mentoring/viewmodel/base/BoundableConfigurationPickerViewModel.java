package mentoring.viewmodel.base;

import java.io.File;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import mentoring.configuration.Configuration;

/**
 * ViewModel used to pick a new configuration that can be bounded to another one.
 * This class is not thread-safe.
 * @param <T> type of the configuration to pick
 */
public class BoundableConfigurationPickerViewModel<T extends Configuration<T>> 
        extends ConfigurationPickerViewModel<T> {
    
    private final ConfigurationPickerViewModel<T> twin;
    private final InvalidationListener fileBindingListener;
    private final InvalidationListener fileBindingWeakListener;
    private boolean bounded = false;
    
    @SuppressWarnings("unchecked")
    public BoundableConfigurationPickerViewModel(ConfigurationPickerViewModel<T> twin){
        super(twin);
        this.twin = twin;
        fileBindingListener = observable -> 
                getFilePicker().setCurrentFile(((ReadOnlyObjectProperty<File>) observable).get());
        fileBindingWeakListener = 
            new WeakInvalidationListener(fileBindingListener);
    }
    
    /**
     * Bind this ViewModel to its twin: all its observable values will be bounded. This results in
     * a no-op if it is already bounded.
     */
    public void bind(){
        if(!bounded){
            configurationType.bind(twin.configurationType);
            selectedItem.bind(twin.selectedItem);
            twin.filePicker.getCurrentFile().addListener(fileBindingWeakListener);
            //Fire event once to make sure that value is up to date
            fileBindingListener.invalidated(twin.filePicker.getCurrentFile());
            bounded = true;
        }
    }
    
    /**
     * Unbind this ViewModel from its twin. This results in a no-op if it is not already bounded.
     */
    public void unbind(){
        configurationType.unbind();
        selectedItem.unbind();
        twin.filePicker.getCurrentFile().removeListener(fileBindingWeakListener);
        bounded = false;
    }
}

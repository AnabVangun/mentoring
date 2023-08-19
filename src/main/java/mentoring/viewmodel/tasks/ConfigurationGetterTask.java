package mentoring.viewmodel.tasks;

import java.util.List;
import java.util.Objects;
import javafx.scene.control.Alert;
import mentoring.configuration.Configuration;
import mentoring.viewmodel.base.ConfigurableViewModel;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;

/**
 * Class used to get a {@link Configuration} instance and update ViewModels.
 * @param <T> type of configuration to get
 */
public class ConfigurationGetterTask<T extends Configuration<T>> 
        extends AbstractTask<T, ConfigurationGetterTask<T>> {
    private final ConfigurationPickerViewModel<T> configurationVM;
    private final List<ConfigurableViewModel<T>> resultVMs;
    private T configuration;
    
    public ConfigurationGetterTask(ConfigurationPickerViewModel<T> configurationVM,
            List<? extends ConfigurableViewModel<T>> resultVMs){
        //TODO refactor: move to View layer
        super(task -> {
            State state = task.getState();
            switch(state){
                case READY, SUCCEEDED -> {/*no-op, excluded from default*/}//FIXME READY should be deleted (it erroneously fails a test)
                case FAILED -> new Alert(Alert.AlertType.ERROR, 
                        task.getException().getLocalizedMessage()).show();
                default -> new Alert(Alert.AlertType.WARNING,
                            "Callback was called before task was finished: " + state).show();
            }
        });
        this.configurationVM = Objects.requireNonNull(configurationVM);
        this.resultVMs = List.copyOf(resultVMs);
    }
    
    @Override
    protected T call() throws Exception{
        configuration = configurationVM.getConfiguration();
        return configuration;
    }
    
    @Override
    protected void specificActionOnSuccess(){
        for (ConfigurableViewModel<T> vm : resultVMs){
            vm.setConfiguration(configuration);
        }
    }
    
    @Override
    protected ConfigurationGetterTask<T> self(){
        return this;
    }
}

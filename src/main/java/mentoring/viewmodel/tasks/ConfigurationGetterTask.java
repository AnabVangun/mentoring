package mentoring.viewmodel.tasks;

import java.util.List;
import java.util.Objects;
import mentoring.configuration.Configuration;
import mentoring.viewmodel.base.ConfigurableViewModel;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;

/**
 * Class used to get a {@link Configuration} instance and update ViewModels.
 * @param <T> type of configuration to get
 */
public class ConfigurationGetterTask<T extends Configuration<T>> extends AbstractTask<T> {
    private final ConfigurationPickerViewModel<T> configurationVM;
    private final List<ConfigurableViewModel<T>> resultVMs;
    private T configuration;
    
    public ConfigurationGetterTask(ConfigurationPickerViewModel<T> configurationVM,
            List<? extends ConfigurableViewModel<T>> resultVMs, 
            TaskCompletionCallback<? super T> callback){
        super(callback);
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
}

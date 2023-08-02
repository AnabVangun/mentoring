package mentoring.viewmodel.tasks;

import javafx.concurrent.Task;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

/**
 * Class used to get a {@link ResultConfiguration} instance and update ViewModels.
 */
public class ResultConfigurationGetterTask extends Task<ResultConfiguration<Person, Person>> {
    
    public ResultConfigurationGetterTask(
            ConfigurationPickerViewModel<ResultConfiguration<Person, Person>> configurationVM,
            PersonMatchesViewModel... resultVM){
        throw new UnsupportedOperationException("not implemented yet"); //TODO test and implement
    }
    
    @Override
    protected ResultConfiguration<Person, Person> call() throws Exception{
        throw new UnsupportedOperationException("not implemented yet"); //TODO test and implement
    }
    
    @Override
    protected void succeeded(){
        throw new UnsupportedOperationException("not implemented yet"); //TODO test and implement
    }
    
    @Override
    protected void failed(){
        throw new UnsupportedOperationException("not implemented yet"); //TODO test and implement
    }
}

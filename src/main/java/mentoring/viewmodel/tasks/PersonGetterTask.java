package mentoring.viewmodel.tasks;

import java.util.List;
import java.util.Objects;
import javafx.concurrent.Task;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.base.FilePickerViewModel;
import mentoring.viewmodel.datastructure.PersonListViewModel;

/**
 * Class used to get persons and update an input view model.
 */
public class PersonGetterTask extends Task<List<Person>> {
    private final PersonListViewModel resultVM;
    private PersonConfiguration personConfiguration;
    private List<Person> persons;
    private final FilePickerViewModel<List<Person>> personPicker;
    private final ConfigurationPickerViewModel<PersonConfiguration> configurationPicker;
    
    /**
     * Initialise a PersonGetter object.
     * @param resultVM the ViewModel that will be updated when the task completes
     * @param personPicker the ViewModel that knows how to parse the person file
     * @param configurationPicker the ViewModel that knows how to obtain the person configuration
     */
    public PersonGetterTask(PersonListViewModel resultVM, 
            FilePickerViewModel<List<Person>> personPicker,
            ConfigurationPickerViewModel<PersonConfiguration> configurationPicker) {
        this.resultVM = Objects.requireNonNull(resultVM);
        this.personPicker = Objects.requireNonNull(personPicker);
        this.configurationPicker = Objects.requireNonNull(configurationPicker);
    }

    @Override
    protected List<Person> call() throws Exception {
        personConfiguration = configurationPicker.getConfiguration();
        persons = personPicker.parseCurrentFile();
        return persons;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        resultVM.update(personConfiguration, persons);
    }
    
    @Override
    protected void failed() {
        super.failed();
        throw new RuntimeException("Something went wrong in task", getException());
    }
}
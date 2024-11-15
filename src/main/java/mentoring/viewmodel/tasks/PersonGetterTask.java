package mentoring.viewmodel.tasks;

import java.util.List;
import java.util.Objects;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.base.OpenChoiceFilePickerViewModel;
import mentoring.viewmodel.datastructure.PersonListViewModel;

/**
 * Class used to get persons and update an input view model.
 */
public class PersonGetterTask extends AbstractTask<List<Person>> {
    private final PersonListViewModel resultVM;
    private PersonConfiguration personConfiguration;
    private List<Person> persons;
    private final OpenChoiceFilePickerViewModel<List<Person>> personPicker;
    private final ConfigurationPickerViewModel<PersonConfiguration> configurationPicker;
    
    /**
     * Initialise a PersonGetter object.
     * @param resultVM the ViewModel that will be updated when the task completes
     * @param personPicker the ViewModel that knows how to parse the person file
     * @param configurationPicker the ViewModel that knows how to obtain the person configuration
     * @param callback the method to call when the task has run
     */
    public PersonGetterTask(PersonListViewModel resultVM, 
            OpenChoiceFilePickerViewModel<List<Person>> personPicker,
            ConfigurationPickerViewModel<PersonConfiguration> configurationPicker,
            TaskCompletionCallback<? super List<Person>> callback) {
        super(callback);
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
    protected void specificActionOnSuccess() {
        resultVM.update(personConfiguration, persons);
    }
}
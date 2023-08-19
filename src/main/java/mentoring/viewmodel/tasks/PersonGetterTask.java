package mentoring.viewmodel.tasks;

import java.util.List;
import java.util.Objects;
import static javafx.concurrent.Worker.State.FAILED;
import static javafx.concurrent.Worker.State.READY;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import javafx.scene.control.Alert;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.base.FilePickerViewModel;
import mentoring.viewmodel.datastructure.PersonListViewModel;

/**
 * Class used to get persons and update an input view model.
 */
public class PersonGetterTask extends AbstractTask<List<Person>, PersonGetterTask> {
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
    
    @Override
    protected PersonGetterTask self(){
        return this;
    }
}
package mentoring.viewmodel.tasks;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

/**
 * Export matches to a file in a background task.
 */
public class MatchExportTask extends AbstractTask<Void, MatchExportTask> {
    
    private final PersonMatchesViewModel firstExportedVM;
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private final PersonMatchesViewModel[] exportedVMs;
    private final WriterSupplier writerSupplier;
    private final ConfigurationPickerViewModel<ResultConfiguration<Person,Person>> configurationVM;

    /**
     * Initialise a MatchExportTask object.
     * @param writerSupplier to supply the writer used to export the data
     * @param callback the method to call when the task has run
     * @param configurationVM the ViewModel that will be used to get the export configuration
     * @param firstExportedVM a mandatory ViewModel containing data to export
     * @param exportedVMs optional additional ViewModels containing data to export
     */
    public MatchExportTask(WriterSupplier writerSupplier, 
            TaskCompletionCallback<Void, MatchExportTask> callback,
            ConfigurationPickerViewModel<ResultConfiguration<Person,Person>> configurationVM, 
            PersonMatchesViewModel firstExportedVM, PersonMatchesViewModel... exportedVMs) {
        super(callback);
        this.firstExportedVM = Objects.requireNonNull(firstExportedVM);
        for (int i = 0; i < exportedVMs.length; i++) {
            Objects.requireNonNull(exportedVMs[i], "Null view model at index " + i);
        }
        this.exportedVMs = exportedVMs;
        this.writerSupplier = Objects.requireNonNull(writerSupplier);
        this.configurationVM = Objects.requireNonNull(configurationVM);
    }

    @Override
    protected Void call() throws Exception {
        ResultConfiguration<Person, Person> configuration = configurationVM.getConfiguration();
        try (final Writer writer = writerSupplier.get()) {
            firstExportedVM.writeMatches(writer, configuration, true);
            for(PersonMatchesViewModel vm : exportedVMs){
                vm.writeMatches(writer, configuration, false);
            }
        }
        return null;
    }
    
    @Override
    protected MatchExportTask self(){
        return this;
    }
    
    public static interface WriterSupplier {
        Writer get() throws IOException;
    }
}

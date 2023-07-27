package mentoring.viewmodel.tasks;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

/**
 * Export matches to a file in a background task.
 */
public class MatchExportTask extends Task<Void> {
    
    private final PersonMatchesViewModel firstExportedVM;
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private final PersonMatchesViewModel[] exportedVMs;
    private final WriterSupplier writerSupplier;
    private final RunConfiguration data;

    /**
     * Initialise a MatchExportTask object.
     * @param writerSupplier to supply the writer used to export the data
     * @param data where to get data from
     * @param firstExportedVM a mandatory ViewModel containing data to export
     * @param exportedVMs optional additional ViewModels containing data to export
     */
    public MatchExportTask(WriterSupplier writerSupplier, RunConfiguration data, 
            PersonMatchesViewModel firstExportedVM, PersonMatchesViewModel... exportedVMs) {
        Objects.requireNonNull(writerSupplier);
        Objects.requireNonNull(data);
        Objects.requireNonNull(firstExportedVM);
        Objects.requireNonNull(exportedVMs);
        for (int i = 0; i < exportedVMs.length; i++) {
            Objects.requireNonNull(exportedVMs[i], "Null view model at index " + i);
        }
        this.firstExportedVM = firstExportedVM;
        this.exportedVMs = exportedVMs;
        this.writerSupplier = writerSupplier;
        this.data = data;
    }

    @Override
    protected Void call() throws Exception {
        ResultConfiguration<Person, Person> configuration = data.getResultConfiguration();
        try (final Writer writer = writerSupplier.get()) {
            firstExportedVM.writeMatches(writer, configuration, true);
            for(PersonMatchesViewModel vm : exportedVMs){
                vm.writeMatches(writer, configuration, false);
            }
        }
        return null;
    }

    @Override
    protected void succeeded() {
        //TODO internationalise string
        super.succeeded();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Export completed");
        alert.show();
    }

    @Override
    protected void failed() {
        super.failed();
        Alert alert = new Alert(Alert.AlertType.ERROR, getException().getLocalizedMessage());
        alert.show();
    }
    
    public static interface WriterSupplier {
        Writer get() throws IOException;
    }
}

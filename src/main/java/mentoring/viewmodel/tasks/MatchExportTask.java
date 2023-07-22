package mentoring.viewmodel.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
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
    
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private final PersonMatchesViewModel[] exportedVMs;
    private final File outputFile;
    private final RunConfiguration data;

    /**
     * Initialise a MatchExportTask object.
     * @param outputFile the file where to export the data
     * @param data where to get data from
     * @param exportedVMs the ViewModels that contain the data to export.
     */
    public MatchExportTask(File outputFile, RunConfiguration data, PersonMatchesViewModel... exportedVMs) {
        //TODO test
        this.exportedVMs = exportedVMs;
        this.outputFile = outputFile;
        this.data = data;
    }

    @Override
    protected Void call() throws Exception {
        if(exportedVMs.length == 0){
            return null;
        }
        ResultConfiguration<Person, Person> configuration = data.getResultConfiguration();
        try (final PrintWriter writer = new PrintWriter(outputFile, Charset.forName("utf-8"))) {
            exportedVMs[0].writeMatches(writer, configuration, true);
            for(int i = 1; i < exportedVMs.length; i++){
                exportedVMs[i].writeMatches(writer, configuration, false);
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
    
}

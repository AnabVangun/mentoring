package mentoring.viewmodel.tasks;

import java.io.File;
import java.io.FileOutputStream;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;

class MatchExportTask extends Task<Void> {
    
    private final PersonMatchesViewModel exportedVM;
    private final File outputFile;
    private final RunConfiguration data;

    /**
     * Initialise a MatchExportTask object.
     * @param exportedVM the view model that contains the data to export
     * @param outputFile the file where to export the data
     * @param data where to get data from
     */
    MatchExportTask(PersonMatchesViewModel exportedVM, File outputFile, RunConfiguration data) {
        this.exportedVM = exportedVM;
        this.outputFile = outputFile;
        this.data = data;
    }

    @Override
    protected Void call() throws Exception {
        try (final FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            exportedVM.writeMatches(outputStream, data.getResultConfiguration());
            return null;
        }
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

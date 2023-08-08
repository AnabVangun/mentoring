package mentoring.view.base;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import mentoring.viewmodel.base.FilePickerViewModel;

/**
 * View responsible for selecting a file to parse.
 * <p> This view is not fully operational until setViewModel has been called.
 */
public class FilePickerView implements Initializable {
    @FXML
    private TextField fileSelectorLabel;
    @FXML
    private Button fileSelectorButton;
    
    private FilePickerViewModel<?> viewModel;
    private FileChooser chooser;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}
    
    /**
     * Set the ViewModel used by this view to manage the File selection.
     * @param viewModel underlying ViewModel behind the view
     */
    public void setViewModel(FilePickerViewModel<?> viewModel){
        this.viewModel = Objects.requireNonNull(viewModel);
        List<FileChooser.ExtensionFilter> filters = viewModel.getStandardExtensions().stream()
                .map(entry -> new FileChooser.ExtensionFilter(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        chooser = ViewTools.createFileChooser("Choose configuration file", filters);
        chooser.initialDirectoryProperty().bind(viewModel.getCurrentFileDirectory());
        fileSelectorLabel.textProperty().bind(viewModel.getCurrentFilePath());
        ViewTools.configureButton(fileSelectorButton, "Pick file", event -> {
            File inputFile = chooser.showOpenDialog(
                    ((Node) event.getSource()).getScene().getWindow());
            if(inputFile != null){
                viewModel.setCurrentFile(inputFile);
            }
        });
    }
}

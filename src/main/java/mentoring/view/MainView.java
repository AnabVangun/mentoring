package mentoring.view;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javax.inject.Inject;
import mentoring.view.base.ViewTools;
import mentoring.view.datastructure.MatchesTableView;
import mentoring.viewmodel.MainViewModel;
import mentoring.viewmodel.PojoRunConfiguration;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.datastructure.PersonType;

public class MainView implements Initializable {
    
    private final MainViewModel vm;
    private final FileChooser chooser = ViewTools.createFileChooser("Export to",
            List.of(new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                    new FileChooser.ExtensionFilter("All files", "*.*")));
    
    @FXML
    private MatchesTableView tableViewController;
    @FXML
    private Button runButton;
    @FXML
    private Button addManualMatchButton;
    @FXML
    private Button deleteManualMatchButton;
    @FXML
    private Button exportButton;
    
    @Inject
    MainView(MainViewModel vm){
        this.vm = vm;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        RunConfiguration data = PojoRunConfiguration.TEST;
        fillPersonTables(data);
        configureButtons(data);
    }
        
    private void fillPersonTables(RunConfiguration data){
        for (PersonType type : PersonType.values()){
            vm.getPersons(tableViewController.getPersonViewModel(type), data, type);
        }
    }
    
    private void configureButtons(RunConfiguration data){
        configureButtonToMakeMatches(runButton, "Run", data);
        configureButtonToMakeManualMatch(addManualMatchButton, "Set as match", data);
        configureButtonToDeleteManualMatch(deleteManualMatchButton, "Delete manual match");
        configureButtonToExportMatches(exportButton, "Export matches", data);
    }
    
    private void configureButtonToMakeMatches(Button button, String buttonCaption, 
            RunConfiguration data){
        ViewTools.configureButton(button, buttonCaption, event -> {
            /*When configuration VM has been defined, use this instead the try block
            Put it in a separate function for a configuration button
            vm.getResultConfiguration(configurationVM, tableViewController.getBatchMatchesViewModel(),
                    tableViewController.getOneAtATimeMatchesViewModel());*/
            try {
                //TODO delete when configuration loading is properly implemented
                tableViewController.getBatchMatchesViewModel()
                        .setConfiguration(data.getResultConfiguration());
                tableViewController.getOneAtATimeMatchesViewModel()
                        .setConfiguration(data.getResultConfiguration());
            } catch (IOException e){
                throw new UncheckedIOException(e);
            }
            vm.makeMatches(
                    tableViewController.getPersonViewModel(PersonType.MENTEE),
                    tableViewController.getPersonViewModel(PersonType.MENTOR),
                    tableViewController.getBatchMatchesViewModel(), 
                    tableViewController.getOneAtATimeMatchesViewModel(),
                    data);
                    });
    }
    
    private void configureButtonToMakeManualMatch(Button button, String buttonCaption,
            RunConfiguration data){
        ViewTools.configureButton(button, buttonCaption, event -> vm.makeSingleMatch(
                //FIXME: protect against illegal use: no selection, confirmation for multiple selection...
                tableViewController.getSelectedPerson(PersonType.MENTEE),
                tableViewController.getSelectedPerson(PersonType.MENTOR), 
                tableViewController.getOneAtATimeMatchesViewModel(), data));
        //FIXME: manualMatchButton should only be enabled when a mentee and a mentor have been selected
    }
    
    private void configureButtonToDeleteManualMatch(Button button, String buttonCaption){
        ViewTools.configureButton(button, buttonCaption, event -> vm.removeSingleMatch(
                tableViewController.getSelectedManualMatch(), 
                tableViewController.getOneAtATimeMatchesViewModel()));
    }
    
    private void configureButtonToExportMatches(Button button, String buttonCaption, 
            RunConfiguration data){
        ViewTools.configureButton(button, buttonCaption, event -> {
            File outputFile = chooser.showSaveDialog(
                    ((Node) event.getSource()).getScene().getWindow());
            if(outputFile != null){
                File parent = outputFile.getParentFile();
                if(parent != null){
                    chooser.setInitialDirectory(parent);
                }
                vm.exportMatches(outputFile, data,
                tableViewController.getOneAtATimeMatchesViewModel(),
                tableViewController.getBatchMatchesViewModel());
            }
        });
    }
}

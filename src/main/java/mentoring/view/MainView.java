package mentoring.view;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.inject.Inject;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.view.base.ViewTools;
import mentoring.view.datastructure.MatchesTableView;
import mentoring.viewmodel.MainViewModel;
import mentoring.viewmodel.PojoRunConfiguration;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.base.FilePickerViewModel;
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
    @FXML
    private MenuItem configureMenuItem;
    
    @Inject
    MainView(MainViewModel vm){
        this.vm = vm;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        RunConfiguration data = PojoRunConfiguration.TEST;
        fillPersonTables(data);
        configureButtons(data);
        showConfigurationPicker();
    }
        
    private void fillPersonTables(RunConfiguration data){
        for (PersonType type : PersonType.values()){
            vm.getPersons(tableViewController.getPersonViewModel(type), data, type);
        }
    }
    
    private void configureButtons(RunConfiguration data){
        //TODO internationalise string
        configureButtonToMakeMatches(runButton, "Run", data);
        configureButtonToMakeManualMatch(addManualMatchButton, "Set as match", data);
        configureButtonToDeleteManualMatch(deleteManualMatchButton, "Delete manual match");
        configureButtonToExportMatches(exportButton, "Export matches", data);
        configureMenuItem.setText("Configure");
        configureMenuItem.setOnAction(event -> showConfigurationPicker());
    }
    
    private void configureButtonToMakeMatches(Button button, String buttonCaption, 
            RunConfiguration data){
        ViewTools.configureButton(button, buttonCaption, event -> {
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
    
    @SuppressWarnings("unchecked")
    //FIXME: the explicit cast should not be needed, and the unchecked warning as well
    private void showConfigurationPicker(){
        //TODO: refactor: emphasize structure and operations.
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/mentoring/globalConfigurationSelectionView.fxml"));
        Parent node = null;
        try {
            node = loader.load();
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
        GlobalConfigurationPickerView globalConfigurationView = 
                (GlobalConfigurationPickerView) loader.getController();
        FilePickerViewModel<List<Person>> menteeSourceVM =
                vm.forgePersonListPickerViewModel();
        ConfigurationPickerViewModel<ResultConfiguration<Person, Person>> resultVM = 
                vm.forgeResultConfigurationPickerViewModel();
        globalConfigurationView.getMenteeSourceView().setViewModel(menteeSourceVM);
        globalConfigurationView.getResultConfigurationView().setViewModel(resultVM);
        globalConfigurationView.setValidationAction(() -> {
            //TODO modify person handling to add mentees to VM here
            vm.getResultConfiguration(resultVM,
                    List.of(tableViewController.getBatchMatchesViewModel(),
                            tableViewController.getOneAtATimeMatchesViewModel()));
        });
        Scene scene = new Scene(node);
        scene.getStylesheets().add(getClass().getResource("/mentoring/styles.css").toExternalForm());
        Stage configurationWindow = new Stage();
        configurationWindow.setScene(scene);
        configurationWindow.initModality(Modality.APPLICATION_MODAL);
        if(runButton.getScene() != null){
            configurationWindow.initOwner(runButton.getScene().getWindow());
        }
        configurationWindow.showAndWait();
    }
}

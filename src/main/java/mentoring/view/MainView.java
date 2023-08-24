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
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javax.inject.Inject;
import mentoring.view.base.StageBuilder;
import mentoring.view.base.TaskCompletionAlert;
import mentoring.view.base.ViewTools;
import mentoring.view.datastructure.ForbiddenMatchesView;
import mentoring.view.datastructure.MatchesTableView;
import mentoring.viewmodel.MainViewModel;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.base.FilePickerViewModel;
import mentoring.viewmodel.datastructure.ForbiddenMatchListViewModel;
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
    private Button addForbiddenMatchButton;
    @FXML
    private Button exportButton;
    @FXML
    private MenuItem configureMenuItem;
    @FXML
    private MenuItem forbiddenMatchesMenuItem;
    
    @Inject
    MainView(MainViewModel vm){
        this.vm = vm;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureButtons();
        showConfigurationPicker();
    }
    
    private void configureButtons(){
        //TODO internationalise string
        configureButtonToMakeMatches(runButton, "Run");
        configureButtonToMakeManualMatch(addManualMatchButton, "Set as match");
        configureButtonToDeleteManualMatch(deleteManualMatchButton, "Delete manual match");
        configureButtonToForbidMatch(addForbiddenMatchButton, "Forbid match");
        configureButtonToExportMatches(exportButton, "Export matches");
        configureMenuItem.setText("Configure");
        configureMenuItem.setOnAction(event -> showConfigurationPicker());
        forbiddenMatchesMenuItem.setText("Forbidden matches");
        forbiddenMatchesMenuItem.setOnAction(event -> showForbiddenMatches());
    }
    
    private void configureButtonToMakeMatches(Button button, String buttonCaption){
        ViewTools.configureButton(button, buttonCaption, event -> {
            vm.makeMatches(
                    tableViewController.getPersonViewModel(PersonType.MENTEE),
                    tableViewController.getPersonViewModel(PersonType.MENTOR),
                    tableViewController.getBatchMatchesViewModel(), 
                    tableViewController.getOneAtATimeMatchesViewModel());
                    });
    }
    
    private void configureButtonToMakeManualMatch(Button button, String buttonCaption){
        ViewTools.configureButton(button, buttonCaption, event -> vm.makeSingleMatch(
                //FIXME: protect against illegal use: no selection, confirmation for multiple selection...
                tableViewController.getSelectedPerson(PersonType.MENTEE),
                tableViewController.getSelectedPerson(PersonType.MENTOR), 
                tableViewController.getOneAtATimeMatchesViewModel()));
    }
    
    private void configureButtonToDeleteManualMatch(Button button, String buttonCaption){
        ViewTools.configureButton(button, buttonCaption, event -> vm.removeSingleMatch(
                tableViewController.getSelectedManualMatch(), 
                tableViewController.getOneAtATimeMatchesViewModel()));
    }
    
    private void configureButtonToForbidMatch(Button button, String buttonCaption){
        ViewTools.configureButton(button, buttonCaption, event -> vm.addForbiddenMatch(
                tableViewController.getSelectedPerson(PersonType.MENTEE), 
                tableViewController.getSelectedPerson(PersonType.MENTOR)));
    }
    
    private void configureButtonToExportMatches(Button button, String buttonCaption){
        ViewTools.configureButton(button, buttonCaption, event -> {
            File outputFile = chooser.showSaveDialog(
                    ((Node) event.getSource()).getScene().getWindow());
            if(outputFile != null){
                File parent = outputFile.getParentFile();
                if(parent != null){
                    chooser.setInitialDirectory(parent);
                }
                vm.exportMatches(outputFile, TaskCompletionAlert.MATCH_EXPORT_ALERT,
                tableViewController.getOneAtATimeMatchesViewModel(),
                tableViewController.getBatchMatchesViewModel());
            }
        });
    }
    
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
        FilePickerViewModel<?> menteeSourceVM =
                vm.getPersonPicker(PersonType.MENTEE);
        ConfigurationPickerViewModel<?> menteeConfigurationVM =
                vm.getPersonConfiguration(PersonType.MENTEE);
        FilePickerViewModel<?> mentorSourceVM =
                vm.getPersonPicker(PersonType.MENTOR);
        ConfigurationPickerViewModel<?> mentorConfigurationVM =
                vm.getPersonConfiguration(PersonType.MENTOR);
        ConfigurationPickerViewModel<?> matchVM = vm.getMatchConfiguration();
        ConfigurationPickerViewModel<?> resultVM = vm.getResultConfiguration();
        ConfigurationPickerViewModel<?> exportVM = vm.getExportConfiguration();
        globalConfigurationView.getPersonSourceView(PersonType.MENTEE).setViewModel(menteeSourceVM);
        globalConfigurationView.getPersonConfigurationView(PersonType.MENTEE).setViewModel(menteeConfigurationVM);
        globalConfigurationView.getPersonSourceView(PersonType.MENTOR).setViewModel(mentorSourceVM);
        globalConfigurationView.getPersonConfigurationView(PersonType.MENTOR).setViewModel(mentorConfigurationVM);
        globalConfigurationView.getMatchConfigurationView().setViewModel(matchVM);
        globalConfigurationView.getResultConfigurationView().setViewModel(resultVM);
        globalConfigurationView.getExportConfigurationView().setViewModel(exportVM);
        globalConfigurationView.setValidationAction(() -> {
            vm.getResultConfiguration(List.of(tableViewController.getBatchMatchesViewModel(),
                            tableViewController.getOneAtATimeMatchesViewModel()));
            for (PersonType type : PersonType.values()){
                vm.getPersons(tableViewController.getPersonViewModel(type), type);
            }
        });
        
        StageBuilder builder = new StageBuilder().withTitle("Configure mentoring");
        if(runButton.getScene() != null){
            builder.withModality(Modality.APPLICATION_MODAL, runButton.getScene().getWindow());
        }
        builder.build(node).showAndWait();
    }
    
    private void showForbiddenMatches(){
        //TODO: refactor: emphasize structure and operations.
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/mentoring/forbiddenMatchesView.fxml"));
        Parent node = null;
        try {
            node = loader.load();
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
        ForbiddenMatchesView view = 
                (ForbiddenMatchesView) loader.getController();
        ForbiddenMatchListViewModel forbiddenMatchesViewModel = vm.getForbiddenMatches();
        view.setViewModel(forbiddenMatchesViewModel);
        view.setRemovalButtonAction((event, viewModel) -> vm.removeForbiddenMatch(viewModel));
        StageBuilder builder = new StageBuilder().withTitle("Forbidden matches");
        builder.build(node).show();
    }
}

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
import javafx.stage.Stage;
import javax.inject.Inject;
import mentoring.view.base.StageBuilder;
import mentoring.view.base.TaskCompletionAlertFactory;
import mentoring.view.base.ViewTools;
import mentoring.view.datastructure.ForbiddenMatchesView;
import mentoring.view.datastructure.MatchesTableView;
import mentoring.viewmodel.MainViewModel;
import mentoring.viewmodel.datastructure.PersonType;
import mentoring.viewmodel.tasks.AbstractTask;

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
        vm.BindSelectionProperties(tableViewController.getSelectedPersonProperty(PersonType.MENTEE), 
                tableViewController.getSelectedPersonProperty(PersonType.MENTOR), 
                tableViewController.getSelectedComputedMatchProperty(),
                tableViewController.getSelectedManualMatchProperty());
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
                    tableViewController.getOneAtATimeMatchesViewModel(),
                    TaskCompletionAlertFactory.alertOnFailure(except ->
                            //TODO internationalise string
                            "Failed to make matches: %s".formatted(except.getLocalizedMessage())));
                    });
        button.disableProperty().bind(vm.disableComputedMatch());
    }
    
    private void configureButtonToMakeManualMatch(Button button, String buttonCaption){
        ViewTools.configureButton(button, buttonCaption, event -> vm.makeSingleMatch(
                tableViewController.getOneAtATimeMatchesViewModel(),
                TaskCompletionAlertFactory.alertOnFailure(except -> 
                        "Failed to make manual match: %s".formatted(except))));
        button.disableProperty().bind(vm.disableManualMatch());
    }
    
    private void configureButtonToDeleteManualMatch(Button button, String buttonCaption){
        ViewTools.configureButton(button, buttonCaption, event -> vm.removeSingleMatch(
                tableViewController.getOneAtATimeMatchesViewModel(),
                TaskCompletionAlertFactory.alertOnFailure(except -> 
                        //TODO: internationalise string
                        "Failed to remove manual match : %s".formatted(except.getLocalizedMessage()))));
        button.disableProperty().bind(vm.disableDeleteManualMatch());
    }
    
    private void configureButtonToForbidMatch(Button button, String buttonCaption){
        ViewTools.configureButton(button, buttonCaption, event -> vm.addForbiddenMatch(
                TaskCompletionAlertFactory.alertOnFailure(except -> 
                        //TODO internationalise String
                        "Failed to add match : %s".formatted(except.getLocalizedMessage()))));
        button.disableProperty().bind(vm.disableForbidMatch());
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
                vm.exportMatches(outputFile, 
                        TaskCompletionAlertFactory.alertOnSuccessAndFailure(
                                //TODO internationalise string
                                () -> "Export completed in file %s"
                                        .formatted(outputFile.getAbsolutePath()), 
                                except -> except.getLocalizedMessage()),
                        tableViewController.getOneAtATimeMatchesViewModel(),
                        tableViewController.getBatchMatchesViewModel());
            }
        });
        button.disableProperty().bind(vm.disableExportMatches());
    }
    
    private void showConfigurationPicker(){
        /*
        TODO: when configuration is changed, erase the current matches.
            1. If there is anything to erase, ask for confirmation before showing the window
            2. optionaly, if some parts of configuration can be changed without erasing the system,
                handle it nicely
        */
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/mentoring/globalConfigurationSelectionView.fxml"));
        Parent node = loadFromFxmlLoader(loader);
        configureGlobalConfigurationPickerView(loader);
        StageBuilder builder = new StageBuilder().withTitle("Configure mentoring");
        if(runButton.getScene() != null){
            builder.withModality(Modality.APPLICATION_MODAL, runButton.getScene().getWindow());
        }
        Stage stage = builder.build(node);
        stage.setMaximized(true);
        stage.showAndWait();
    }
    
    private Parent loadFromFxmlLoader(FXMLLoader loader){
        try {
            return loader.load();
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
    
    private void configureGlobalConfigurationPickerView(FXMLLoader loader){
        GlobalConfigurationPickerView globalConfigurationView = 
                (GlobalConfigurationPickerView) loader.getController();
        setGlobalConfigurationPickerViewModels(globalConfigurationView);
        globalConfigurationView.setValidationAction(this::getConfiguration);
    }
    
    private void setGlobalConfigurationPickerViewModels(GlobalConfigurationPickerView
            globalConfigurationView){
        globalConfigurationView.getPersonSourceView(PersonType.MENTEE)
                .setViewModel(vm.getPersonPicker(PersonType.MENTEE));
        globalConfigurationView.getPersonConfigurationView(PersonType.MENTEE)
                .setViewModel(vm.getPersonConfiguration(PersonType.MENTEE));
        globalConfigurationView.getPersonSourceView(PersonType.MENTOR)
                .setViewModel(vm.getPersonPicker(PersonType.MENTOR));
        globalConfigurationView.getPersonConfigurationView(PersonType.MENTOR)
                .setViewModel(vm.getPersonConfiguration(PersonType.MENTOR));
        globalConfigurationView.getMatchConfigurationView()
                .setViewModel(vm.getMatchConfiguration());
        globalConfigurationView.getResultConfigurationView()
                .setViewModel(vm.getResultConfiguration());
        globalConfigurationView.getExportConfigurationView()
                .setViewModel(vm.getExportConfiguration());
    }
    
    private void getConfiguration(){
        //TODO signal to vm that configuration is being loaded.
        getPersons();
        getResultConfiguration();
        clearMatches();
        //TODO coordinate clearing forbiddenMatches between MatchesBuilderHandler and MainViewModel
        vm.getForbiddenMatches().clear();
    }
    
    private void getPersons(){
        for (PersonType type : PersonType.values()){
            vm.getPersons(tableViewController.getPersonViewModel(type), type,
                    TaskCompletionAlertFactory.alertOnFailure(except -> 
                            //TODO internationalise string
                            "Failed to load %s : %s".formatted(type, 
                                    except.getLocalizedMessage())));
        }
    }
    
    private void getResultConfiguration(){
        vm.getResultConfiguration(List.of(tableViewController.getBatchMatchesViewModel(),
                tableViewController.getOneAtATimeMatchesViewModel()),
                TaskCompletionAlertFactory.alertOnFailure(except ->
                        //TODO internationalize string
                        "Failed to get result configuration: %s"
                                .formatted(except.getLocalizedMessage())));
    }
    
    private void clearMatches(){
        tableViewController.getOneAtATimeMatchesViewModel().clear();
        tableViewController.getBatchMatchesViewModel().clear();
    }
    
    private void showForbiddenMatches(){
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/mentoring/forbiddenMatchesView.fxml"));
        Parent node = loadFromFxmlLoader(loader);
        configureForbiddenMatchesView(loader);
        StageBuilder builder = new StageBuilder().withTitle("Forbidden matches");
        builder.build(node).show();
    }
    
    private void configureForbiddenMatchesView(FXMLLoader loader){
        ForbiddenMatchesView view = 
                (ForbiddenMatchesView) loader.getController();
        view.setViewModel(vm.getForbiddenMatches());
        AbstractTask.TaskCompletionCallback<Object> callback = 
                TaskCompletionAlertFactory.alertOnFailure(except -> 
                        //TODO internationalise String
                        "Failed to remove forbidden match: %s"
                                .formatted(except.getLocalizedMessage()));
        view.setRemovalButtonAction((event, viewModel) -> vm.removeForbiddenMatch(viewModel,
                callback));
    }
}

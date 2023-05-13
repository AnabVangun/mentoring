package mentoring.view.datastructure;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javax.inject.Inject;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;

/**
 * View responsible for displaying a table of {@link MatchViewModel} object.
 */
public class MatchesTableView implements Initializable {
    
    @FXML
    private TableView<PersonMatchViewModel> table;
    
    private final PersonMatchesViewModel vm;
    
    @Inject
    MatchesTableView(PersonMatchesViewModel vm){
        this.vm = vm;
    }
    
    /**
     * Returns this view's underlying view model.
     */
    public PersonMatchesViewModel getViewModel(){
        return vm;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        table.getColumns().addAll(vm.headerContentProperty());
        table.setItems(vm.itemsProperty());
        //TODO bind to vm property
        table.setVisible(false);
    }
    
}

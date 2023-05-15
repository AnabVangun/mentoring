package mentoring.view.datastructure;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
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
        update();
        vm.addListener(observable -> update());
    }
    
    private void update(){
        /*
        TODO : Once ResultConfiguration offers a Map<String, String> instead of a String[] for each 
        line, modify MatchViewModel so that observableMatch is a Map<String, Observable<String>> and 
        simplify code behind
        */
        table.getColumns().clear();
        List<String> headers = vm.getHeaderContent();
        for (int i = 0; i < headers.size(); i++){
            TableColumn<PersonMatchViewModel, String> column = new TableColumn<>(headers.get(i));
            int counter = i;
            column.setCellValueFactory(p -> new SimpleStringProperty(
                    p.getValue().observableMatch().get(counter)));
            table.getColumns().add(column);
        }
        table.itemsProperty().get().setAll(vm.getItems());
        table.visibleProperty().set(vm.isValid());
    }
    
}

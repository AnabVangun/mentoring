package mentoring.view.datastructure;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
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
        table.getColumns().clear();
        List<String> headers = vm.getHeaderContent();
        for (String header : headers){
            addColumn(table, header, p -> p.getValue().observableMatch());
        }
        table.itemsProperty().get().setAll(vm.getItems());
        table.visibleProperty().set(vm.isValid());
    }
    
    //TODO extract into utility class for TableView
    private static <E> void addColumn(TableView<E> table, String header, 
            Callback<CellDataFeatures<E, String>, Map<String, String>> propertiesGetter){
        TableColumn<E, String> column = new TableColumn<>(header);
        column.setCellValueFactory(p -> new SimpleStringProperty(
                propertiesGetter.call(p).get(header)));
        table.getColumns().add(column);
    }
    
}

package mentoring.view.base;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import mentoring.viewmodel.base.TabularDataViewModel;

/**
 * Set of static functions used to manage Table views.
 */
public class TabularDataViewTools {
    
    private TabularDataViewTools(){/*no-op*/}
    
    /**
     * Set a TableView to display the content of a TabularDataViewModel.
     * @param <E> the type of data to display in the table
     * @param table the table to update
     * @param viewModel the ViewModel containing the data to display
     * @param propertyGetter how to get the content of each column in the table. The map MUST have
     * each header defined in the ViewModel as keys.
     */
    public static <E> void updateTable(TableView<E> table, TabularDataViewModel<E> viewModel,
            Function<E, Map<String, Object>> propertyGetter){
        //No need to requireNonNull on table: the call to getColumns() will fail.
        Objects.requireNonNull(viewModel);
        Objects.requireNonNull(propertyGetter);
        table.getColumns().clear();
        List<String> headers = viewModel.getHeaders();
        for (String header : headers){
            addColumn(table, header, p -> propertyGetter.apply(p.getValue()));
        }
        table.itemsProperty().get().setAll(viewModel.getContent());
    }
    
    private static <E> void addColumn(TableView<E> table, String header, 
            Callback<TableColumn.CellDataFeatures<E, Object>, Map<String, Object>> propertiesGetter){
        TableColumn<E, Object> column = new WrappableTableColumn<>(header);
        column.setCellValueFactory(p -> new SimpleObjectProperty<Object>(
                propertiesGetter.call(p).get(header)));
        table.getColumns().add(column);
    }
    
    /**
     * Select an item in a TableView and scroll to it.
     * @param <E> the type of data to display in the table
     * @param table the table to update
     * @param index to select in the table. Optional: it can be -1
     */
    public static <E> void selectAndScrollTo(TableView<E> table, int index){
        if (index == -1){
            table.getSelectionModel().clearSelection();
        } else {
            table.getSelectionModel().selectIndices(index);
            table.scrollTo(index);
        }
    }
}

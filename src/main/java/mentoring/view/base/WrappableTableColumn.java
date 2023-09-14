package mentoring.view.base;

import java.util.Objects;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;

/**
 * Table column with a contextual menu letting the user wrap the column content.
 */
class WrappableTableColumn<E> extends TableColumn<E, String> {
    //TODO internationalise String
    private final MenuItem wrapColumnButton = new MenuItem("Wrap column");
    private final MenuItem unwrapColumnButton = new MenuItem("Unwrap column");
    private final ContextMenu wrapColumnMenu = new ContextMenu(wrapColumnButton);
    private final ContextMenu unwrapColumnMenu = new ContextMenu(unwrapColumnButton);

    WrappableTableColumn(String header) {
        super(Objects.requireNonNull(header));
        wrapColumnButton.setOnAction(event -> {
            setCellFactory(tc -> forgeWrappedCell(tc));
            setContextMenu(unwrapColumnMenu);
        });
        unwrapColumnButton.setOnAction(event -> {
            setCellFactory(tc -> forgeNonWrappedCell());
            setContextMenu(wrapColumnMenu);
        });
        setContextMenu(wrapColumnMenu);
    }
    
    private TableCell<E, String> forgeWrappedCell(TableColumn<E, String> column){
        TableCell<E, String> cell = new TableCell<>();
        Text text = new Text();
        cell.setGraphic(text);
        cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
        text.wrappingWidthProperty().bind(column.widthProperty());
        text.textProperty().bind(cell.itemProperty());
        return cell;
    }
    
    private TableCell<E, String> forgeNonWrappedCell(){
        return new TextFieldTableCell<>();
    }
    
}

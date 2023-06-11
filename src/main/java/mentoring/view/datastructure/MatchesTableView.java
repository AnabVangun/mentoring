package mentoring.view.datastructure;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import javax.inject.Inject;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;

/**
 * View responsible for displaying a table of {@link MatchViewModel} object.
 */
public class MatchesTableView implements Initializable {
    
    @FXML
    private TableView<String> menteeTable;//TODO create a PersonViewModel to handle menteeTable data
    @FXML
    private TableView<String> mentorTable;//TODO use PersonViewModel to handle mentorTable data
    @FXML
    private TableView<PersonMatchViewModel> computedTable;
    @FXML
    private TableView<PersonMatchViewModel> manualTable;
    
    private final PersonMatchesViewModel vm;
    private static final DataFormat ROW_DATA_FORMAT = new DataFormat("number/row");
    private static final DataFormat COLUMN_DATA_FORMAT = new DataFormat("number/column");
    private static final DataFormat MATCH_VIEW_MODEL_DATA_FORMAT = 
            new DataFormat("application/MatchViewModel");
    //TODO put magic string in global configuration
    private static final PseudoClass DRAG_HOVER_CLASS = PseudoClass.getPseudoClass("drag-hover");
    private final InvalidationListener listener;
    
    @Inject
    MatchesTableView(PersonMatchesViewModel computedVM){
        this.vm = computedVM;
        listener = observable -> {
            update(computedTable, vm);
            updateManualTable(manualTable, vm);
        };
    }
    
    /**
     * Returns this view's underlying view model.
     */
    public PersonMatchesViewModel getViewModel(){
        return vm;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vm.addListener(new WeakInvalidationListener(listener));
        /*
        TODO: make it so only the mentee and mentor columns can be dragged
        Behaviour in viewmodel will be different depending on whether the dragged element is from 
        mentee, mentor, or neither: viewmodel should participate on deciding whether the move is 
        valid and what to register.
        3. protect list from outside modifications in view model
        */
        //setUpDragAndDropPersonsFromResultsToLocked();
    }
    
    private synchronized void update(TableView<PersonMatchViewModel> table, 
            PersonMatchesViewModel associatedVM){
        table.getColumns().clear();
        List<String> headers = associatedVM.getHeaderContent();
        for (String header : headers){
            addColumn(table, header, p -> p.getValue().observableMatch());
        }
        table.itemsProperty().get().setAll(associatedVM.getBatchItems());
    }
    
    private synchronized void updateManualTable(TableView<PersonMatchViewModel> table, 
            PersonMatchesViewModel associatedVM) {
        table.getColumns().clear();
        List<String> headers = associatedVM.getHeaderContent();
        for (String header : headers){
            addColumn(table, header, p -> p.getValue().observableMatch());
        }
        table.itemsProperty().get().setAll(associatedVM.getTransferredItems());
    }
    
    //TODO extract into tested utility class for TableView
    private static <E> void addColumn(TableView<E> table, String header, 
            Callback<CellDataFeatures<E, String>, Map<String, String>> propertiesGetter){
        TableColumn<E, String> column = new TableColumn<>(header);
        column.setCellValueFactory(p -> new SimpleStringProperty(
                propertiesGetter.call(p).get(header)));
        table.getColumns().add(column);
    }
    
    private void setUpDragAndDropPersonsFromResultsToLocked(){
        handleDragBeginning(computedTable);
        registerDropTarget(computedTable, manualTable);
        configureHoverDisplayForDropTarget(computedTable, manualTable);
        handleDrop();
    }
    
    private static void handleDragBeginning(TableView<PersonMatchViewModel> source){
        source.setOnDragDetected(event -> {
            Clipboard board = source.startDragAndDrop(TransferMode.MOVE);
            TablePosition draggedPosition = source.getSelectionModel().getSelectedCells().get(0);
            putTablePositionInClipboard(board, draggedPosition);
            event.consume();
        });
    }
    
    //TODO put in helper class and test
    private static void putTablePositionInClipboard(Clipboard board, TablePosition position){
        ClipboardContent content = new ClipboardContent();
        content.put(ROW_DATA_FORMAT, position.getRow());
        content.put(COLUMN_DATA_FORMAT, position.getColumn());
        content.put(MATCH_VIEW_MODEL_DATA_FORMAT, 
                position.getTableColumn().getCellObservableValue(position.getRow()).getValue());
        board.setContent(content);
    }
    
    private static void registerDropTarget(TableView<PersonMatchViewModel> source, 
            TableView<PersonMatchViewModel> target){
        target.setOnDragOver(event -> {
            if(isValidDragAndDrop(source, event)){
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
    }
    
    private static void configureHoverDisplayForDropTarget(
            TableView<PersonMatchViewModel> acceptedSource, 
            TableView<PersonMatchViewModel> target){
        target.setOnDragEntered(event -> {
            if(isValidDragAndDrop(acceptedSource, event)){
                target.pseudoClassStateChanged(DRAG_HOVER_CLASS, true);
            }
        });
        target.setOnDragExited(event -> {
            if(isValidDragAndDrop(acceptedSource, event)){
                target.pseudoClassStateChanged(DRAG_HOVER_CLASS, false);
            }
        });
    }
    
    private static boolean isValidDragAndDrop(TableView<PersonMatchViewModel> source, 
            DragEvent event) {
        return event.getGestureSource() == source;
    }
    
    private void handleDrop() {
        manualTable.setOnDragDropped(event -> {
            Clipboard board = event.getDragboard();
            boolean success = addDroppedPersonToTarget(board, vm);
            event.setDropCompleted(success);
            event.consume();
        });
    }
    
    private static boolean addDroppedPersonToTarget(Clipboard board, 
            PersonMatchesViewModel vm){
        //TODO test in conjunction with putTablePositionInClipboard
        boolean success = false;
        if(board.hasContent(MATCH_VIEW_MODEL_DATA_FORMAT)){
            //FIXME: the index used is the index in the sorted table, not the underlying list.
            vm.transferItem((PersonMatchViewModel) board.getContent(MATCH_VIEW_MODEL_DATA_FORMAT));
                    //vm.getBatchItems()
                    //.get(Integer.parseInt(board.getContent(ROW_DATA_FORMAT).toString())));
            success = true;
        }
        return success;
    }
}

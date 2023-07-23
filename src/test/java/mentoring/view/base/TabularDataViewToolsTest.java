package mentoring.view.base;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import mentoring.view.base.TabularDataViewToolsTest.TabularDataViewToolsArgs;
import mentoring.viewmodel.base.TabularDataViewModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import test.tools.TestFramework;

@ExtendWith(ApplicationExtension.class)
class TabularDataViewToolsTest implements TestFramework<TabularDataViewToolsArgs> {
    
    @Start
    private void start(Stage stage) {
        //no-op
    }

    @Override
    public Stream<TabularDataViewToolsArgs> argumentsSupplier() {
        return Stream.of(new TabularDataViewToolsArgs("unique test case"));
    }
    /*
    TODO: reflect on how and what to test.
    What:
        1. Check that the headers are as expected --> the text of each header
        2. Check that the rows are as expected --> the ObservableList contains the input objects
        3. Check that the content of each cell is as expected --> the propertyGetter functions as expected
    */
    @TestFactory
    Stream<DynamicNode> updateTable_setHeaders(){
        return test("updateTable() properly sets the headers of the table", args -> {
            TableView<String> view = new TableView<>();
            List<String> expectedHeaders = List.of("first", "second");
            TabularDataViewModel<String> viewModel = forgeViewModel(expectedHeaders, List.of("foo"));
            TabularDataViewTools.updateTable(view, viewModel, forgeGetterProperty(expectedHeaders));
            Assertions.assertEquals(expectedHeaders, extractHeaderText(view));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> updateTable_setItems(){
        return test("updateTable() properly sets the items of the table", args -> {
            TableView<String> view = new TableView<>();
            List<String> headers = List.of("header");
            List<String> expectedItems = List.of("foo", "bar");
            TabularDataViewModel<String> viewModel = forgeViewModel(headers, expectedItems);
            TabularDataViewTools.updateTable(view, viewModel, forgeGetterProperty(headers));
            Assertions.assertEquals(expectedItems, view.getItems());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> updateTable_setCells(){
        return test("updateTable() properly sets the cells of the table", args -> {
            TableView<String> view = new TableView<>();
            List<String> headers = List.of("first", "second");
            List<String> items = List.of("foo", "bar");
            Map<String, List<String>> expectedCells = Map.of(
                    "first", List.of("firstfoo", "firstbar"), 
                    "second", List.of("secondfoo", "secondbar"));
            TabularDataViewModel<String> viewModel = forgeViewModel(headers, items);
            TabularDataViewTools.updateTable(view, viewModel, forgeGetterProperty(headers));
            Map<String, List<String>> actualCells = view.getColumns().stream()
                    .collect(Collectors.toMap(column -> column.getText(), 
                            column -> List.of(column.getCellObservableValue(0).getValue().toString(), 
                                    column.getCellObservableValue(1).getValue().toString())));
            Assertions.assertEquals(expectedCells, actualCells);
        });
    }
    
    private static TabularDataViewModel<String> forgeViewModel(
            List<String> expectedHeaders, List<String> expectedContent){
        @SuppressWarnings("unchecked")
        TabularDataViewModel<String> viewModel = 
                Mockito.mock(TabularDataViewModel.class);
        Mockito.when(viewModel.getHeaders()).thenReturn(expectedHeaders);
        Mockito.when(viewModel.getContent()).thenReturn(expectedContent);
        return viewModel;
    }
    
    private static Function<String, Map<String, String>> forgeGetterProperty(List<String> headers){
        return input -> headers.stream().collect(Collectors.toMap(Function.identity(), 
                header -> header + input));
    }
    
    private static <E> List<String> extractHeaderText(TableView<E> table){
        return table.getColumns().stream()
                .map(column -> column.getText())
                .collect(Collectors.toList());
    }
    
    static record TabularDataViewToolsArgs(String testCase){
        @Override
        public String toString(){
            return this.testCase;
        }
    }
}

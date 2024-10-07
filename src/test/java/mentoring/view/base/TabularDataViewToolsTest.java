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
import org.junit.jupiter.api.function.Executable;
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
    
    private static Function<String, Map<String, Object>> forgeGetterProperty(List<String> headers){
        return input -> headers.stream().collect(Collectors.toMap(Function.identity(), 
                header -> header + input));
    }
    
    private static <E> List<String> extractHeaderText(TableView<E> table){
        return table.getColumns().stream()
                .map(column -> column.getText())
                .collect(Collectors.toList());
    }
    
    @TestFactory
    Stream<DynamicNode> selectAndScroll_selectExpectedItem(){
        return test("selectAndScroll() properly selects the expected item", args -> {
            TableView<String> view = new TableView<>();
            List<String> headers = List.of("first", "second");
            List<String> items = List.of("foo", "bar");
            TabularDataViewTools.updateTable(view, forgeViewModel(headers, items), 
                    forgeGetterProperty(headers));
            TabularDataViewTools.selectAndScrollTo(view, 1);
            Assertions.assertEquals("bar", view.getSelectionModel().getSelectedItem());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> selectAndScroll_clearPreviousSelection(){
        return test("selectAndScroll() properly clears the previous selection", args -> {
            TableView<String> view = new TableView<>();
            List<String> headers = List.of("first", "second");
            List<String> items = List.of("foo", "bar");
            TabularDataViewTools.updateTable(view, forgeViewModel(headers, items), 
                    forgeGetterProperty(headers));
            view.getSelectionModel().select("foo");
            TabularDataViewTools.selectAndScrollTo(view, 1);
            Assertions.assertEquals(List.of("bar"), view.getSelectionModel().getSelectedItems());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> selectAndScroll_scroll(){
        return test("selectAndScroll() scrolls to show the new selection", args -> {
            @SuppressWarnings("unchecked")
            TableView<String> view = Mockito.spy(TableView.class);
            TabularDataViewTools.selectAndScrollTo(view, 1);
            Mockito.verify(view).scrollTo(1);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> updateTable_NPE(){
        return test("updateTable() throws NPEs on null input", args -> {
            TableView<String> view = new TableView<>();
            List<String> headers = List.of("first", "second");
            TabularDataViewModel<String> viewModel = forgeViewModel(headers, List.of("foo"));
            Function<String, Map<String, Object>> propertyGetter = forgeGetterProperty(headers);
            Assertions.assertAll(
                    assertUpdateTableThrowsNPE(null, viewModel, propertyGetter),
                    assertUpdateTableThrowsNPE(view, null, propertyGetter),
                    assertUpdateTableThrowsNPE(view, viewModel, null));
        });
    }
    
    private static <E> Executable assertUpdateTableThrowsNPE(TableView<E> table, 
            TabularDataViewModel<E> viewModel, Function<E, Map<String, Object>> propertyGetter) {
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> TabularDataViewTools.updateTable(table, viewModel, propertyGetter));
    }
    
    @TestFactory
    Stream<DynamicNode> selectAndScroll_NPE(){
        return test("selectAndScroll() throws an NPE on null view", args ->
                Assertions.assertThrows(NullPointerException.class,
                        () -> TabularDataViewTools.selectAndScrollTo(null, 2)));
    }
    
    static record TabularDataViewToolsArgs(String testCase){
        @Override
        public String toString(){
            return this.testCase;
        }
    }
}

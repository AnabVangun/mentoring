package mentoring.viewmodel.datastructure;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.IndexedPropertyName;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyName;
import mentoring.viewmodel.datastructure.PersonListViewModelTest.PersonListViewModelArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

class PersonListViewModelTest implements TestFramework<PersonListViewModelArgs>{
    //TODO refactor to extract ObservableTest interface with default tests for listener handling

    @Override
    public Stream<PersonListViewModelArgs> argumentsSupplier() {
        return Stream.of(new PersonListViewModelArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> update_setHeader(){
        return test("update() sets header", args -> {
            //TODO when header is properly implemented, update test
            PersonListViewModel viewModel = new PersonListViewModel();
            viewModel.update(args.getSimpleConfiguration(), List.of());
            Assertions.assertEquals(args.expectedHeader(), viewModel.getHeaderContent());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_setHeader_repeated(){
        return test("repeated calls to update() properly sets header", args -> {
            //TODO when header is properly implemented, update test
            PersonListViewModel viewModel = new PersonListViewModel();
            viewModel.update(new PersonConfiguration("dummy configuration", 
                    Set.of(),
                    Set.of(), "|", "%s", List.of("nothing")), 
                    List.of());
            viewModel.update(args.getSimpleConfiguration(), List.of());
            Assertions.assertEquals(args.expectedHeader(), viewModel.getHeaderContent());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_setItems(){
        return test("update() sets items", args -> {
            PersonListViewModel viewModel = new PersonListViewModel();
            PersonBuilder builder = new PersonBuilder();
            List<Person> data = List.of(
                    builder.withFullName("first").withProperty("simple", 3)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build(),
                    builder.withFullName("second").withProperty("simple", 4)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build());
            viewModel.update(args.getSimpleConfiguration(), data);
            args.assertItemsAsExpected(viewModel, data, args.getSimpleConfiguration());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_setItems_repeated(){
        return test("repeated calls to update() set items", args -> {
            PersonListViewModel viewModel = new PersonListViewModel();
            PersonBuilder builder = new PersonBuilder();
            viewModel.update(args.getSimpleConfiguration(), List.of(
                    builder.withFullName("wrong person").withProperty("simple", 8)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build()));
            List<Person> data = List.of(
                    builder.withFullName("first").withProperty("simple", 3)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build(),
                    builder.withFullName("second").withProperty("simple", 4)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build());
            viewModel.update(args.getSimpleConfiguration(), data);
            args.assertItemsAsExpected(viewModel, data, args.getSimpleConfiguration());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_setUnderlyingData(){
        return test("update() sets underlying data", args -> {
            PersonListViewModel viewModel = new PersonListViewModel();
            PersonBuilder builder = new PersonBuilder();
            List<Person> data = List.of(
                    builder.withFullName("first").withProperty("simple", 3)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build(),
                    builder.withFullName("second").withProperty("simple", 4)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build());
            viewModel.update(args.getSimpleConfiguration(), data);
            Assertions.assertEquals(data, viewModel.getUnderlyingData());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_setUnderlyingData_repeated(){
        return test("repeated calls to update() set underlying data", args -> {
            PersonListViewModel viewModel = new PersonListViewModel();
            PersonBuilder builder = new PersonBuilder();
            viewModel.update(args.getSimpleConfiguration(), List.of(
                    builder.withFullName("wrong person").withProperty("simple", 8)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build()));
            List<Person> data = List.of(
                    builder.withFullName("first").withProperty("simple", 3)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build(),
                    builder.withFullName("second").withProperty("simple", 4)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build());
            viewModel.update(args.getSimpleConfiguration(), data);
            Assertions.assertEquals(data, viewModel.getUnderlyingData());
        });
    }
        
    @TestFactory
    Stream<DynamicNode> update_notifyListeners(){
        return test("update() notifies the registered listeners", args -> {
            Observable[] notified = new Observable[2];
            InvalidationListener listener1 = observable -> notified[0] = observable;
            InvalidationListener listener2 = observable -> notified[1] = observable;
            PersonListViewModel viewModel = new PersonListViewModel();
            viewModel.addListener(listener1);
            viewModel.addListener(listener2);
            viewModel.update(args.getSimpleConfiguration(), List.of());
            Assertions.assertAll(
                    () -> Assertions.assertSame(viewModel, notified[0]),
                    () -> Assertions.assertSame(viewModel, notified[1]));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> addListener_NPE(){
        return test("addListener() throws an NPE", args -> {
            PersonListViewModel viewModel = new PersonListViewModel();
            Assertions.assertThrows(NullPointerException.class, () -> viewModel.addListener(null));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeListener_nominal(){
        return test("removeListener() removes exactly the input listener", args -> {
            PersonListViewModel viewModel = new PersonListViewModel();
            Observable[] notified = new Observable[2]; 
            InvalidationListener listener1 = observable -> notified[0] = observable;
            InvalidationListener listener2 = observable -> notified[1] = observable;
            viewModel.addListener(listener1);
            viewModel.addListener(listener2);
            viewModel.removeListener(listener1);
            viewModel.update(args.getSimpleConfiguration(), List.of());
            Assertions.assertAll(
                    () -> Assertions.assertNull(notified[0]),
                    () -> Assertions.assertSame(viewModel, notified[1]));
        });
    }
    
    //TODO implement other tests (including repeated calls to update)
    
    static record PersonListViewModelArgs(String testCase){
        @Override
        public String toString(){
            return testCase;
        }
        
        PersonConfiguration getSimpleConfiguration(){
            return new PersonConfiguration("configuration", 
                    Set.of(new PropertyName<>("simple", "simple header", PropertyType.INTEGER)), 
                    Set.of(new IndexedPropertyName<>("indexed", "indexed header", PropertyType.BOOLEAN),
                            new SetPropertyName<>("set", "set header", PropertyType.STRING)),
                    "|", "%s", List.of("simple header"));
        }
        
        List<String> expectedHeader(){
            return List.of("Name");
        }
        
        void assertItemsAsExpected(PersonListViewModel viewModel, List<Person> data, 
                PersonConfiguration configuration){
            List<PersonViewModel> toCheck = viewModel.getItems();
            List<Map<String, String>> actual = toCheck.stream()
                    .map(element -> element.getPersonData()).toList();
            List<Map<String, String>> expected = data.stream()
                    .map(element -> new PersonViewModel(configuration, element).getPersonData())
                    .toList();
            Assertions.assertEquals(expected, actual);
        }
    }
}

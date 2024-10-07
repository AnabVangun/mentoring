package mentoring.viewmodel.datastructure;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.IndexedPropertyName;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyName;
import mentoring.datastructure.SimplePropertyName;
import mentoring.match.Match;
import mentoring.match.MatchTest;
import mentoring.viewmodel.base.ObservableTest;
import mentoring.viewmodel.base.ObservableArgs;
import mentoring.viewmodel.datastructure.PersonListViewModelTest.PersonListViewModelArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;

class PersonListViewModelTest extends ObservableTest<PersonListViewModel,
        PersonListViewModelArgs>{

    @Override
    public Stream<PersonListViewModelArgs> argumentsSupplier() {
        return Stream.of(new PersonListViewModelArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> update_setHeader(){
        return test("update() sets header", args -> {
            PersonListViewModel viewModel = new PersonListViewModel();
            viewModel.update(args.getSimpleConfiguration(), List.of());
            Assertions.assertEquals(args.expectedHeader(), viewModel.getHeaders());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> update_setHeader_repeated(){
        return test("repeated calls to update() properly sets header", args -> {
            PersonListViewModel viewModel = new PersonListViewModel();
            viewModel.update(new PersonConfiguration("dummy configuration", 
                    Set.of(),
                    Set.of(), "|", "%s", List.of("nothing")), 
                    List.of());
            viewModel.update(args.getSimpleConfiguration(), List.of());
            Assertions.assertEquals(args.expectedHeader(), viewModel.getHeaders());
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
        return test("update() notifies the registered listeners", 
                args -> assertInvalidatedEventFired(args, vm -> args.invalidate(vm)));
    }
    
    @TestFactory
    Stream<DynamicNode> getPersonViewModel_expectedResult(){
        return test("getPersonViewModel() returns the expected result",
                args -> {
                    PersonListViewModel viewModel = new PersonListViewModel();
                    PersonMatchViewModel matchVM = Mockito.mock(PersonMatchViewModel.class);
                    PersonBuilder builder = new PersonBuilder();
                    Person mentee = builder.withFullName("mentee").withProperty("simple", 8)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build();
                    Person mentor = builder.withFullName("mentor").withProperty("simple", 3)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build();
                    Match<Person, Person> match = new MatchTest.MatchArgs("", mentee, mentor, 12)
                            .convertAs(Person.class, Person.class);
                    Mockito.when(matchVM.getData()).thenReturn(match);
                    viewModel.update(args.getSimpleConfiguration(), List.of(mentee, mentor));
                    Assertions.assertAll(
                            () -> Assertions.assertEquals(mentee,
                                    viewModel.getPersonViewModel(matchVM, PersonType.MENTEE)
                                            .getData()),
                            () -> Assertions.assertEquals(mentor,
                                    viewModel.getPersonViewModel(matchVM, PersonType.MENTOR)
                                            .getData()));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> getPersonViewModel_nullResult(){
        return test("getPersonViewModel() returns null when it cannot find the view model",
                args -> {
                    PersonListViewModel viewModel = new PersonListViewModel();
                    PersonMatchViewModel matchVM = Mockito.mock(PersonMatchViewModel.class);
                    PersonBuilder builder = new PersonBuilder();
                    Person mentee = builder.withFullName("mentee").withProperty("simple", 8)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build();
                    Person mentor = builder.withFullName("mentor").withProperty("simple", 3)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build();
                    Match<Person, Person> match = new MatchTest.MatchArgs("", mentee, mentor, 12)
                            .convertAs(Person.class, Person.class);
                    Mockito.when(matchVM.getData()).thenReturn(match);
                    Assertions.assertAll(
                            () -> Assertions.assertNull(
                                    viewModel.getPersonViewModel(matchVM, PersonType.MENTEE),
                                    "mentee should be null"),
                            () -> Assertions.assertNull(
                                    viewModel.getPersonViewModel(matchVM, PersonType.MENTOR),
                                    "mentor should be null"));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> getPersonViewModel_NPE(){
        return test(Stream.of("specific test case"), "getPersonViewModel() throws an NPE on null input", 
                args -> {
                    PersonListViewModel viewModel = new PersonListViewModel();
                    PersonMatchViewModel matchVM = Mockito.mock(PersonMatchViewModel.class);
                    Class<NullPointerException> exception = NullPointerException.class;
                    Assertions.assertAll(
                            () -> Assertions.assertThrows(exception, () -> 
                                    viewModel.getPersonViewModel(null, PersonType.MENTEE)),
                            () -> Assertions.assertThrows(exception, () ->
                                    viewModel.getPersonViewModel(matchVM, null)));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getPersonViewModelIndex_expectedResult(){
        return test("getPersonViewModelIndex() returns the expected result",
                args -> {
                    PersonListViewModel viewModel = new PersonListViewModel();
                    PersonMatchViewModel matchVM = Mockito.mock(PersonMatchViewModel.class);
                    PersonBuilder builder = new PersonBuilder();
                    Person mentee = builder.withFullName("mentee").withProperty("simple", 8)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build();
                    Person mentor = builder.withFullName("mentor").withProperty("simple", 3)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build();
                    Match<Person, Person> match = new MatchTest.MatchArgs("", mentee, mentor, 12)
                            .convertAs(Person.class, Person.class);
                    Mockito.when(matchVM.getData()).thenReturn(match);
                    viewModel.update(args.getSimpleConfiguration(), List.of(mentee, mentor));
                    Assertions.assertAll(
                            () -> Assertions.assertEquals(0,
                                    viewModel.getPersonViewModelIndex(matchVM, PersonType.MENTEE)),
                            () -> Assertions.assertEquals(1,
                                    viewModel.getPersonViewModelIndex(matchVM, PersonType.MENTOR)));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> getPersonViewModelIndex_MinusOne(){
        return test("getPersonViewModelIndex() returns -1 when it cannot find the view model",
                args -> {
                    PersonListViewModel viewModel = new PersonListViewModel();
                    PersonMatchViewModel matchVM = Mockito.mock(PersonMatchViewModel.class);
                    PersonBuilder builder = new PersonBuilder();
                    Person mentee = builder.withFullName("mentee").withProperty("simple", 8)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build();
                    Person mentor = builder.withFullName("mentor").withProperty("simple", 3)
                            .withPropertyMap("indexed", Map.of())
                            .withPropertyMap("set", Map.of()).build();
                    Match<Person, Person> match = new MatchTest.MatchArgs("", mentee, mentor, 12)
                            .convertAs(Person.class, Person.class);
                    Mockito.when(matchVM.getData()).thenReturn(match);
                    Assertions.assertAll(
                            () -> Assertions.assertEquals(-1,
                                    viewModel.getPersonViewModelIndex(matchVM, PersonType.MENTEE),
                                    "mentee should be null"),
                            () -> Assertions.assertEquals(-1,
                                    viewModel.getPersonViewModelIndex(matchVM, PersonType.MENTOR),
                                    "mentor should be null"));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> getPersonViewModelIndex_NPE(){
        return test(Stream.of("specific test case"), 
                "getPersonViewModelIndex() throws an NPE on null input", 
                args -> {
                    PersonListViewModel viewModel = new PersonListViewModel();
                    PersonMatchViewModel matchVM = Mockito.mock(PersonMatchViewModel.class);
                    Class<NullPointerException> exception = NullPointerException.class;
                    Assertions.assertAll(
                            () -> Assertions.assertThrows(exception, () -> 
                                    viewModel.getPersonViewModelIndex(null, PersonType.MENTEE)),
                            () -> Assertions.assertThrows(exception, () ->
                                    viewModel.getPersonViewModelIndex(matchVM, null)));
        });
    }
    
    static class PersonListViewModelArgs extends ObservableArgs<PersonListViewModel>{
        
        PersonListViewModelArgs(String testCase){
            super(testCase);
        }
        
        @Override
        protected PersonListViewModel convert(){
            return new PersonListViewModel();
        }
        
        @Override
        protected void invalidate(PersonListViewModel viewModel){
            viewModel.update(getSimpleConfiguration(), List.of());
        }
        
        PersonConfiguration getSimpleConfiguration(){
            Set<MultiplePropertyName<?,?>> multipleProperties = new LinkedHashSet<>();
            multipleProperties.add(
                    new IndexedPropertyName<>("indexed", "indexed header", PropertyType.BOOLEAN));
            multipleProperties.add(
                    new SetPropertyName<>("set", "set header", PropertyType.STRING));
            return new PersonConfiguration("configuration", 
                    Set.of(new SimplePropertyName<>("simple", "simple header", PropertyType.INTEGER)), 
                    multipleProperties,
                    "|", "%s", List.of("simple header"));
        }
        
        List<String> expectedHeader(){
            return List.of("Name","simple","indexed","set");
        }
        
        void assertItemsAsExpected(PersonListViewModel viewModel, List<Person> data, 
                PersonConfiguration configuration){
            List<PersonViewModel> toCheck = viewModel.getContent();
            Collection<Map<String, Object>> actual = toCheck.stream()
                    .map(element -> element.getFormattedData()).toList();
            PersonViewModelFactory factory = new PersonViewModelFactory(configuration);
            Collection<Map<String, Object>> expected = factory.create(data).stream()
                    .map(element -> element.getFormattedData()).toList();
            Assertions.assertEquals(expected, actual);
        }
    }
}

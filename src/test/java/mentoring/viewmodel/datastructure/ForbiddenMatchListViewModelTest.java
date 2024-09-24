package mentoring.viewmodel.datastructure;

import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.ForbiddenMatches;
import mentoring.viewmodel.datastructure.ForbiddenMatchListViewModelTest.ForbiddenMatchListViewModelArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class ForbiddenMatchListViewModelTest implements TestFramework<ForbiddenMatchListViewModelArgs>{
    @Override
    public Stream<ForbiddenMatchListViewModelArgs> argumentsSupplier(){
        return Stream.of(new ForbiddenMatchListViewModelArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> getContent_emptyList(){
        return test("getContent() returns an empty list when nothing has been added", args -> {
            ForbiddenMatchListViewModel vm = args.convert();
            Assertions.assertTrue(vm.getContent().isEmpty());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getContent_modifiedByAddForbiddenMatch(){
        return test("getContent() returns a list that is modified by addForbiddenMatch()", args -> {
            ForbiddenMatchListViewModel vm = args.convert();
            ObservableList<ForbiddenMatchViewModel> observable = vm.getContent();
            InvalidationListener listener = Mockito.mock(InvalidationListener.class);
            observable.addListener(listener);
            PersonBuilder builder = new PersonBuilder();
            Person mentee = builder.withFullName("mentee").build();
            Person mentor = builder.withFullName("mentor").build();
            vm.addForbiddenMatch(mentee, mentor);
            ForbiddenMatchViewModel added = vm.getContent().get(0);
            Assertions.assertAll(
                    () -> Mockito.verify(listener).invalidated(observable),
                    () -> Assertions.assertEquals(1, vm.getContent().size()),
                    () -> Assertions.assertEquals(mentee, added.getMentee()),
                    () -> Assertions.assertEquals(mentor, added.getMentor()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getContent_modifiedByRemoveForbiddenMatch(){
        return test("getContent() returns a list that is modified by removeForbiddenMatch()", args -> {
            ForbiddenMatchListViewModel vm = args.convert();
            ObservableList<ForbiddenMatchViewModel> observable = vm.getContent();
            InvalidationListener listener = Mockito.mock(InvalidationListener.class);
            PersonBuilder builder = new PersonBuilder();
            Person mentee = builder.withFullName("mentee").build();
            Person mentor = builder.withFullName("mentor").build();
            vm.addForbiddenMatch(mentee, mentor);
            args.handler.forbidMatch(mentee, mentor);
            ForbiddenMatchViewModel added = vm.getContent().get(0);
            observable.addListener(listener);
            vm.removeForbiddenMatch(added, args.handler);
            Assertions.assertAll(
                    () -> Mockito.verify(listener).invalidated(observable),
                    () -> Assertions.assertEquals(0, vm.getContent().size()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getContent_sameReturnValue(){
        return test("getContent() always returns the same value", args -> {
            ForbiddenMatchListViewModel vm = args.convert();
            ObservableList<ForbiddenMatchViewModel> expected = vm.getContent();
            Assertions.assertEquals(expected, vm.getContent());
        });
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> getContent_UnmodifiableList(){
        return test("getContent() returns an unmodifiable list", args -> {
            ForbiddenMatchListViewModel vm = args.convert();
            ObservableList<ForbiddenMatchViewModel> list = vm.getContent();
            PersonBuilder builder = new PersonBuilder();
            ForbiddenMatchViewModel element = new ForbiddenMatchViewModel(
                    builder.withFullName("mentee").build(), 
                    builder.withFullName("mentor").build());
            Assertions.assertThrows(UnsupportedOperationException.class, () -> {
                list.add(element);
            });
        });
    }
    
    @TestFactory
    Stream<DynamicNode> addForbiddenMatch_canAddAgainARemovedMatch(){
        return test("addForbiddenMatch() can add again a match that has been removed", args -> {
            ForbiddenMatchListViewModel vm = args.convert();
            PersonBuilder builder = new PersonBuilder();
            Person mentee = builder.withFullName("mentee").build();
            Person mentor = builder.withFullName("mentor").build();
            vm.addForbiddenMatch(mentee, mentor);
            args.handler.forbidMatch(mentee, mentor);
            ForbiddenMatchViewModel removed = vm.getContent().get(0);
            vm.removeForbiddenMatch(removed, args.handler);
            vm.addForbiddenMatch(mentee, mentor);
            Assertions.assertAll(
                    () -> Assertions.assertEquals(1, vm.getContent().size()),
                    () -> Assertions.assertEquals(mentee, vm.getContent().get(0).getMentee()),
                    () -> Assertions.assertEquals(mentor, vm.getContent().get(0).getMentor()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeForbiddenMatch_notifyHandler(){
        return test("removeForbiddenMatch() notifies the handler", args -> {
            ForbiddenMatchListViewModel vm = args.convert();
            PersonBuilder builder = new PersonBuilder();
            Person mentee = builder.withFullName("mentee").build();
            Person mentor = builder.withFullName("mentor").build();
            vm.addForbiddenMatch(mentee, mentor);
            args.handler.forbidMatch(mentee, mentor);
            vm.removeForbiddenMatch(vm.getContent().get(0), args.handler);
            Assertions.assertTrue(args.handler.forbidMatch(mentee, mentor));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> clear_removesAllContent(){
        return test("clear() clears the content of the ViewModel", args -> {
            ForbiddenMatchListViewModel vm = args.convert();
            ObservableList<ForbiddenMatchViewModel> observable = vm.getContent();
            InvalidationListener listener = Mockito.mock(InvalidationListener.class);
            observable.addListener(listener);
            PersonBuilder builder = new PersonBuilder();
            Person mentee = builder.withFullName("mentee").build();
            Person mentor = builder.withFullName("mentor").build();
            vm.addForbiddenMatch(mentee, mentor);
            vm.clear();
            Assertions.assertAll(
                    () -> Mockito.verify(listener, Mockito.times(2)).invalidated(observable),
                    () -> Assertions.assertTrue(vm.getContent().isEmpty()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> clear_vmStillOperational(){
        return test("clear() does not prevent the ViewModel from working again", args -> {
            ForbiddenMatchListViewModel vm = args.convert();
            ObservableList<ForbiddenMatchViewModel> observable = vm.getContent();
            InvalidationListener listener = Mockito.mock(InvalidationListener.class);
            observable.addListener(listener);
            PersonBuilder builder = new PersonBuilder();
            Person mentee = builder.withFullName("mentee").build();
            Person mentor = builder.withFullName("mentor").build();
            vm.addForbiddenMatch(mentee, mentor);
            vm.clear();
            vm.addForbiddenMatch(mentee, mentor);
            ForbiddenMatchViewModel added = vm.getContent().get(0);
            Assertions.assertAll(
                    () -> Mockito.verify(listener, Mockito.times(3)).invalidated(observable),
                    () -> Assertions.assertEquals(1, vm.getContent().size()),
                    () -> Assertions.assertEquals(mentee, added.getMentee()),
                    () -> Assertions.assertEquals(mentor, added.getMentor()));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> addForbidenMatch_NPE(){
        return test(Stream.of(new ForbiddenMatchListViewModelArgs("unique test case")), 
                "addForbiddenMatch() throws NPE on null input", args -> {
                    ForbiddenMatchListViewModel vm = new ForbiddenMatchListViewModel();
                    Person validPerson = new PersonBuilder().build();
                    Assertions.assertAll(
                            () -> Assertions.assertThrows(NullPointerException.class, 
                                    () -> vm.addForbiddenMatch(null, validPerson), 
                                    "null mentee"),
                            () -> Assertions.assertThrows(NullPointerException.class,
                                    () -> vm.addForbiddenMatch(validPerson, null), 
                                    "null mentor"));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> removeForbiddenMatch_NPE(){
        return test(Stream.of(new ForbiddenMatchListViewModelArgs("unique test case")), 
                "removeForbiddenMatch() throws NPE on null input", args -> {
                    ForbiddenMatchListViewModel vm = new ForbiddenMatchListViewModel();
                    PersonBuilder builder = new PersonBuilder();
                    Person mentee = builder.withFullName("mentee").build();
                    Person mentor = builder.withFullName("mentor").build();
                    vm.addForbiddenMatch(mentee, mentor);
                    Assertions.assertAll(
                        () -> Assertions.assertThrows(NullPointerException.class,
                                () -> vm.removeForbiddenMatch(null, args.handler)),
                        () -> Assertions.assertThrows(NullPointerException.class,
                                () -> vm.removeForbiddenMatch(vm.getContent().get(0), null)));
                            });
    }
    
    static class ForbiddenMatchListViewModelArgs extends TestArgs{
        ForbiddenMatches<Person, Person> handler = new ForbiddenMatches<>();
        ForbiddenMatchListViewModelArgs(String testCase){
            super(testCase);
        }
        
        ForbiddenMatchListViewModel convert(){
            return new ForbiddenMatchListViewModel();
        }
    }
}

package mentoring.viewmodel.tasks;

import java.util.stream.Stream;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.match.ForbiddenMatches;
import mentoring.viewmodel.datastructure.ForbiddenMatchListViewModel;
import mentoring.viewmodel.tasks.ForbiddenMatchTaskTest.ForbiddenMatchTaskArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class ForbiddenMatchTaskTest implements TestFramework<ForbiddenMatchTaskArgs>{
    @Override
    public Stream<ForbiddenMatchTaskArgs> argumentsSupplier(){
        return Stream.of(new ForbiddenMatchTaskArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> addForbiddenTask_updateViewModel(){
        return test("succeeded() updates the forbidden match view model", args -> {
            ForbiddenMatchTask task = args.convert();
            try {
                task.call();
            } catch (Exception e){
                Assertions.fail("normally unreachable code", e);
            }
            task.succeeded();
            Mockito.verify(args.vm).addForbiddenMatch(args.mentee, args.mentor);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of(new ForbiddenMatchTaskArgs("unique test case")), 
                "constructor throws NPE on null input", args -> Assertions.assertAll(
                        () -> assertConstructorThrowsNPE(null, args.mentee, args.mentor, args.handler,
                            args.callback, "null VM"),
                    () -> assertConstructorThrowsNPE(args.vm, null, args.mentor, args.handler,
                            args.callback, "null mentee"),
                    () -> assertConstructorThrowsNPE(args.vm, args.mentee, null, args.handler,
                            args.callback, "null mentor"),
                    () -> assertConstructorThrowsNPE(args.vm, args.mentee, args.mentor, null,
                            args.callback, "null handler"),
                    () -> assertConstructorThrowsNPE(args.vm, args.mentee, args.mentor, args.handler,
                            null, "null callback")));
    }
    
    static Executable assertConstructorThrowsNPE(ForbiddenMatchListViewModel viewModel, 
            Person mentee, Person mentor, ForbiddenMatches<Person, Person> handler, 
            AbstractTask.TaskCompletionCallback<? super Void> callback,
            String message){
        return () -> Assertions.assertThrows(NullPointerException.class, () -> 
                new ForbiddenMatchTask(viewModel, mentee, mentor, handler, callback), message);
    }
    
    static class ForbiddenMatchTaskArgs extends TestArgs {
        final ForbiddenMatchListViewModel vm = Mockito.mock(ForbiddenMatchListViewModel.class);
        final Person mentee;
        final Person mentor;
        @SuppressWarnings("unchecked")
        final ForbiddenMatches<Person, Person> handler = new ForbiddenMatches<>();
        final AbstractTask.TaskCompletionCallback<Object> callback = task -> {};
        
        ForbiddenMatchTaskArgs(String testCase){
            super(testCase);
            PersonBuilder builder = new PersonBuilder();
            mentee = builder.withFullName("mentee").build();
            mentor = builder.withFullName("mentor").build();
        }
        
        ForbiddenMatchTask convert(){
            return new ForbiddenMatchTask(vm, mentee, mentor, handler, callback);
        }
    }
}

package mentoring.viewmodel.datastructure;

import java.util.List;
import java.util.stream.Stream;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import mentoring.viewmodel.datastructure.MatchStatusTest.MatchStatusArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class MatchStatusTest implements TestFramework<MatchStatusArgs>{
    @Override
    public Stream<MatchStatusArgs> argumentsSupplier(){
        return Stream.of(new MatchStatusArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> add_trueFirstTime(){
        return test("add() returns true if the flag had never been added", args ->
                Assertions.assertTrue(args.convert().add(MatchStatus.MatchFlag.MANUAL_MATCH)));
    }
    
    @TestFactory
    Stream<DynamicNode> add_FalseAlreadyPresent(){
        return test("add() returns false if the flag is already present", args ->  {
            MatchStatus status = args.convert();
            MatchStatus.MatchFlag flag = MatchStatus.MatchFlag.COMPUTED_MATCH;
            status.add(flag);
            Assertions.assertFalse(status.add(flag));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> add_trueLaterTime(){
        return test("add() returns true if the flag had been added", args -> {
            MatchStatus status = args.convert();
            MatchStatus.MatchFlag flag = MatchStatus.MatchFlag.COMPUTED_MATCH;
            status.add(flag);
            status.remove(flag);
            Assertions.assertTrue(status.add(flag));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> remove_falseAbsent(){
        return test("remove() returns false if the flag had never been added", args ->
                Assertions.assertFalse(args.convert().remove(MatchStatus.MatchFlag.MANUAL_MATCH)));
    }
    
    @TestFactory
    Stream<DynamicNode> remove_truePresent(){
        return test("remove() returns true if the flag is already present", args ->  {
            MatchStatus status = args.convert();
            MatchStatus.MatchFlag flag = MatchStatus.MatchFlag.COMPUTED_MATCH;
            status.add(flag);
            Assertions.assertTrue(status.remove(flag));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> remove_falseLaterTime(){
        return test("remove() returns false if the flag has been removed", args -> {
            MatchStatus status = args.convert();
            MatchStatus.MatchFlag flag = MatchStatus.MatchFlag.COMPUTED_MATCH;
            status.add(flag);
            status.remove(flag);
            Assertions.assertFalse(status.remove(flag));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getStyleClass_reflectState(){
        return test("getStyleClass() returns the expected status", args -> {
            MatchStatus status = args.convert();
            MatchStatus.MatchFlag flag = MatchStatus.MatchFlag.COMPUTED_MATCH;
            status.add(flag);
            Assertions.assertEquals(List.of(flag.styleClass), status.getStyleClass());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getStyleClass_reflectMultipleState(){
        return test("getStyleClass() returns the expected status for multiple flags", args -> {
            MatchStatus status = args.convert();
            MatchStatus.MatchFlag firstFlag = MatchStatus.MatchFlag.COMPUTED_MATCH;
            MatchStatus.MatchFlag secondFlag = MatchStatus.MatchFlag.MANUAL_MATCH;
            status.add(firstFlag);
            status.add(secondFlag);
            Assertions.assertEquals(List.of(firstFlag.styleClass, secondFlag.styleClass),
                    status.getStyleClass());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getStyleClass_reflectStateAfterRemoval(){
        return test("getStyleClass() returns the expected status after flag removal", args -> {
            MatchStatus status = args.convert();
            MatchStatus.MatchFlag firstFlag = MatchStatus.MatchFlag.COMPUTED_MATCH;
            MatchStatus.MatchFlag secondFlag = MatchStatus.MatchFlag.MANUAL_MATCH;
            status.add(firstFlag);
            status.add(secondFlag);
            status.remove(firstFlag);
            Assertions.assertEquals(List.of(secondFlag.styleClass), status.getStyleClass());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> getStyleClass_ObservableProperlyUpdated(){
        return test("getStyleClass() fires invalidation listeners as expected", args -> {
            MatchStatus status = args.convert();
            MatchStatus.MatchFlag firstFlag = MatchStatus.MatchFlag.COMPUTED_MATCH;
            MatchStatus.MatchFlag secondFlag = MatchStatus.MatchFlag.MANUAL_MATCH;
            ObservableList<String> observable = status.getStyleClass();
            int[] counter = new int[]{0};
            InvalidationListener listener = event -> counter[0] += 1;
            observable.addListener(listener);
            status.add(firstFlag);
            status.add(secondFlag);
            status.remove(firstFlag);
            status.remove(firstFlag);//no-op, counter should not be updated
            Assertions.assertEquals(3, counter[0]);
        });
    }
    
    static class MatchStatusArgs extends TestArgs{
        MatchStatusArgs(String testCase){
            super(testCase);
        }
        
        MatchStatus convert(){
            return new MatchStatus();
        }
    }
}

package mentoring.match;

import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import mentoring.match.ForbiddenMatchesTest.ForbiddenMatchesTestArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class ForbiddenMatchesTest implements TestFramework<ForbiddenMatchesTestArgs>{

    @Override
    public Stream<ForbiddenMatchesTestArgs> argumentsSupplier() {
        return Stream.of(new ForbiddenMatchesTestArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> forbid_returnsTrue(){
        return test("forbid returns true on allowed match", args -> 
                Assertions.assertTrue(args.convert()
                        .forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE, 
                                ForbiddenMatchesTestArgs.FIRST_MENTOR))
        );
    }
    
    @TestFactory
    Stream<DynamicNode> forbid_returnsFalse(){
        return test("forbid returns false on forbidden match", args -> {
            ForbiddenMatches<Integer, Integer> forbiddenMatches = args.convert();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            Assertions.assertFalse(forbiddenMatches.forbidMatch(
                    ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> allow_returnFalse(){
        return test("allow returns false on allowed match", args -> 
                Assertions.assertFalse(args.convert()
                        .allowMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE, 
                                ForbiddenMatchesTestArgs.FIRST_MENTOR))
        );
    }
    
    @TestFactory
    Stream<DynamicNode> allow_returnsTrue(){
        return test("allow returns true on forbidden match", args -> {
            ForbiddenMatches<Integer, Integer> forbiddenMatches = args.convert();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            Assertions.assertTrue(forbiddenMatches.allowMatch(
                    ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> forbid_allow_forbid_returnsTrue(){
        return test("forbid returns true on allowed match", args -> {
            ForbiddenMatches<Integer, Integer> forbiddenMatches = args.convert();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.allowMatch(
                    ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            Assertions.assertTrue(forbiddenMatches.forbidMatch(
                    ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> forbid_actuallyForbid(){
        return test("forbid is applied", args ->{
            ForbiddenMatches<Integer, Integer> forbiddenMatches = args.convert();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.apply(args.matrixHandler, ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            Mockito.verify(args.matrixHandler)
                    .forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX, 
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX);
            Mockito.verify(args.matrixHandler, Mockito.atMostOnce())
                    .forbidMatch(Mockito.anyInt(), Mockito.anyInt());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> forbid_actuallyForbid_multiple(){
        return test("multiple calls to forbid are all applied", args ->{
            ForbiddenMatches<Integer, Integer> forbiddenMatches = args.convert();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.SECOND_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.apply(args.matrixHandler, ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            Mockito.verify(args.matrixHandler)
                    .forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX, 
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX);
            Mockito.verify(args.matrixHandler)
                    .forbidMatch(ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX, 
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX);
            Mockito.verify(args.matrixHandler, Mockito.atMost(2))
                    .forbidMatch(Mockito.anyInt(), Mockito.anyInt());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> forbid_actuallyForbid_withAllow(){
        return test("multiple calls to forbid and allow are all applied", args ->{
            ForbiddenMatches<Integer, Integer> forbiddenMatches = args.convert();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.SECOND_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.allowMatch(ForbiddenMatchesTestArgs.SECOND_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.apply(args.matrixHandler, ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            Mockito.verify(args.matrixHandler)
                    .forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX, 
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX);
            Mockito.verify(args.matrixHandler, Mockito.atMostOnce())
                    .forbidMatch(Mockito.anyInt(), Mockito.anyInt());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> forbid_actuallyForbid_allowedMatch(){
        return test("forbid is applied even if match had been allowed", args ->{
            ForbiddenMatches<Integer, Integer> forbiddenMatches = args.convert();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.allowMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.apply(args.matrixHandler, ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            Mockito.verify(args.matrixHandler)
                    .forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX, 
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX);
            Mockito.verify(args.matrixHandler, Mockito.atMostOnce())
                    .forbidMatch(Mockito.anyInt(), Mockito.anyInt());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> apply_repeatedCalls(){
        return test("apply applies the current state even if called repeatedly", args ->{
            ForbiddenMatches<Integer, Integer> forbiddenMatches = args.convert();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.apply(args.matrixHandler, ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            forbiddenMatches.apply(args.matrixHandler, ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            Mockito.verify(args.matrixHandler, Mockito.times(2))
                    .forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX, 
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX);
            Mockito.verify(args.matrixHandler, Mockito.atMost(2))
                    .forbidMatch(Mockito.anyInt(), Mockito.anyInt());
        });
    }
    
    static class ForbiddenMatchesTestArgs extends TestArgs{
        final CostMatrixHandler matrixHandler = Mockito.mock(CostMatrixHandler.class);
        static final ToIntFunction<Integer> menteeIndexGetter = mentee -> mentee - 1;
        static final ToIntFunction<Integer> mentorIndexGetter = mentor -> mentor * 2;
        static final Integer FIRST_MENTEE = 3;
        static final int FIRST_MENTEE_INDEX = 2;
        static final Integer FIRST_MENTOR = 5;
        static final int FIRST_MENTOR_INDEX = 10;
        static final Integer SECOND_MENTEE = 12;
        static final int SECOND_MENTEE_INDEX = 11;
        static final Integer SECOND_MENTOR = 8;
        static final int SECOND_MENTOR_INDEX = 16;
        
        ForbiddenMatchesTestArgs(String testCase){
            super(testCase);
        }
        
        ForbiddenMatches<Integer, Integer> convert(){
            return new ForbiddenMatches<>();
        }
    }
}

package mentoring.match;

import java.util.List;
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
        return test("forbid() returns true on allowed match", args -> 
                Assertions.assertTrue(args.convert()
                        .forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE, 
                                ForbiddenMatchesTestArgs.FIRST_MENTOR))
        );
    }
    
    @TestFactory
    Stream<DynamicNode> forbid_returnsFalse(){
        return test("forbid() returns false on forbidden match", args -> {
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            Assertions.assertFalse(forbiddenMatches.forbidMatch(
                    ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> allow_returnFalse(){
        return test("allow() returns false on allowed match", args -> 
                Assertions.assertFalse(args.convert()
                        .allowMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE, 
                                ForbiddenMatchesTestArgs.FIRST_MENTOR))
        );
    }
    
    @TestFactory
    Stream<DynamicNode> allow_returnsTrue(){
        return test("allow() returns true on forbidden match", args -> {
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            Assertions.assertTrue(forbiddenMatches.allowMatch(
                    ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> forbid_allow_forbid_returnsTrue(){
        return test("forbid() returns true on allowed match", args -> {
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
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
        return test("forbid() is applied", args ->{
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
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
        return test("multiple calls to forbid() are all applied", args ->{
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
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
        return test("multiple calls to forbid() and allow() are all applied", args ->{
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
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
        return test("forbid() is applied even if match had been allowed", args ->{
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
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
    Stream<DynamicNode> apply_AllowForbiddenMatches(){
        return test("apply() allows the previously forbidden matches if appropriate", args -> {
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
            CostMatrixHandler<String, String> costMatrix = args.createCostMatrix();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE, 
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.apply(costMatrix, ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            forbiddenMatches.allowMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.apply(costMatrix, ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            Assertions.assertTrue(costMatrix.isMatchAllowed(
                    ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX, 
                    ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> apply_repeatedCalls(){
        return test("apply() applies the current state even if called repeatedly", args ->{
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
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
    
    @TestFactory
    Stream<DynamicNode> applyFromLastState_noLastState(){
        return test("applyFromLastState() applies the entire state when first call", args -> {
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
            CostMatrixHandler<String, String> matrixHandler = args.createCostMatrix();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.SECOND_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.allowMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.applyFromLastState(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            Assertions.assertAll(
                    () -> Assertions.assertFalse(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "first mentee first mentor"),
                    () -> Assertions.assertFalse(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "second mentee second mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "first mentee second mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "second mentee first mentor")
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> applyFromLastState_noOpAfterApply(){
        return test("applyFromLastState() does not apply anything if called after apply", args -> {
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
            CostMatrixHandler<String, String> matrixHandler = args.createCostMatrix();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.apply(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            forbiddenMatches.allowMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.apply(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            matrixHandler.clearSpecificallyForbiddenMatches();
            matrixHandler.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX, 
                    ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX);
            forbiddenMatches.applyFromLastState(args.matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            Assertions.assertAll(
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "first mentee first mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "second mentee second mentor"),
                    () -> Assertions.assertFalse(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "first mentee second mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "second mentee first mentor")
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> applyFromLastState_noOpAfterApplyFromLastState(){
        return test("applyFromLastState() does not apply anything if called twice", args -> {
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
            CostMatrixHandler<String, String> matrixHandler = args.createCostMatrix();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.apply(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            forbiddenMatches.allowMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.applyFromLastState(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            matrixHandler.clearSpecificallyForbiddenMatches();
            matrixHandler.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX, 
                    ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX);
            forbiddenMatches.applyFromLastState(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            Assertions.assertAll(
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "first mentee first mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "second mentee second mentor"),
                    () -> Assertions.assertFalse(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "first mentee second mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "second mentee first mentor")
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> applyFromLastState_appliesOnlyNewCallsAfterApply(){
        return test("applyFromLastState() only applies new modifications from last apply", args -> {
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
            CostMatrixHandler<String, String> matrixHandler = args.createCostMatrix();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.allowMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.apply(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            matrixHandler.clearSpecificallyForbiddenMatches();
            matrixHandler.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX, 
                    ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX);
            forbiddenMatches.allowMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.SECOND_MENTEE, 
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.applyFromLastState(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            Assertions.assertAll(
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "first mentee first mentor"),
                    () -> Assertions.assertFalse(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "second mentee second mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "first mentee second mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "second mentee first mentor")
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> applyFromLastState_appliesOnlyNewCalls(){
        return test("applyFromLastState() only applies new modifications from last call", args -> {
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
            CostMatrixHandler<String, String> matrixHandler = args.createCostMatrix();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.apply(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            forbiddenMatches.allowMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.applyFromLastState(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            matrixHandler.clearSpecificallyForbiddenMatches();
            matrixHandler.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX, 
                    ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX);
            forbiddenMatches.allowMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.SECOND_MENTEE, 
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.applyFromLastState(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            Assertions.assertAll(
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "first mentee first mentor"),
                    () -> Assertions.assertFalse(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "second mentee second mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "first mentee second mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "second mentee first mentor")
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> clear_allMatchesAllowed(){
        return test("after clear(), all matches are allowed", args -> {
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
            CostMatrixHandler<String, String> matrixHandler = args.createCostMatrix();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.allowMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.SECOND_MENTEE, 
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.clear();
            forbiddenMatches.apply(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            Assertions.assertAll(
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "first mentee first mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "second mentee second mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "first mentee second mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "second mentee first mentor")
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> clear_applyFromLastStateAllowsAllMatches(){
        return test("after clear(), applyFromLastState() allows all matches", args -> {
            ForbiddenMatches<String, String> forbiddenMatches = args.convert();
            CostMatrixHandler<String, String> matrixHandler = args.createCostMatrix();
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.apply(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            forbiddenMatches.allowMatch(ForbiddenMatchesTestArgs.FIRST_MENTEE,
                    ForbiddenMatchesTestArgs.FIRST_MENTOR);
            forbiddenMatches.forbidMatch(ForbiddenMatchesTestArgs.SECOND_MENTEE, 
                    ForbiddenMatchesTestArgs.SECOND_MENTOR);
            forbiddenMatches.clear();
            forbiddenMatches.applyFromLastState(matrixHandler,
                    ForbiddenMatchesTestArgs.menteeIndexGetter, 
                    ForbiddenMatchesTestArgs.mentorIndexGetter);
            Assertions.assertAll(
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "first mentee first mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "second mentee second mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.FIRST_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.SECOND_MENTOR_INDEX),
                            "first mentee second mentor"),
                    () -> Assertions.assertTrue(matrixHandler.isMatchAllowed(
                            ForbiddenMatchesTestArgs.SECOND_MENTEE_INDEX,
                            ForbiddenMatchesTestArgs.FIRST_MENTOR_INDEX),
                            "second mentee first mentor")
            );
        });
    }
    
    static class ForbiddenMatchesTestArgs extends TestArgs{
        @SuppressWarnings("unchecked")
        final CostMatrixHandler<String, String> matrixHandler = Mockito.mock(CostMatrixHandler.class);
        static final String FIRST_MENTEE = "first mentee";
        static final int FIRST_MENTEE_INDEX = 0;
        static final String FIRST_MENTOR = "first mentor";
        static final int FIRST_MENTOR_INDEX = 0;
        static final String SECOND_MENTEE = "second mentee";
        static final int SECOND_MENTEE_INDEX = 1;
        static final String SECOND_MENTOR = "second mentor";
        static final int SECOND_MENTOR_INDEX = 1;
        static final ToIntFunction<String> menteeIndexGetter = mentee -> 
                mentee.equals(FIRST_MENTEE) ? FIRST_MENTEE_INDEX : SECOND_MENTEE_INDEX;
        static final ToIntFunction<String> mentorIndexGetter = mentor -> 
                mentor.equals(FIRST_MENTOR) ? FIRST_MENTOR_INDEX : SECOND_MENTOR_INDEX;
        
        ForbiddenMatchesTestArgs(String testCase){
            super(testCase);
        }
        
        ForbiddenMatches<String, String> convert(){
            return new ForbiddenMatches<>();
        }
        
        CostMatrixHandler<String, String> createCostMatrix(){
            return new CostMatrixHandler<>(
                    List.of(FIRST_MENTEE,SECOND_MENTEE),
                    List.of(FIRST_MENTOR,SECOND_MENTOR),
                    List.of((mentee, mentor) -> 1));
        }
    }
}

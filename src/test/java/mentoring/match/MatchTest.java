package mentoring.match;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

public final class MatchTest implements TestFramework<MatchTest.MatchArgs>{

    @Override
    public Stream<MatchArgs> argumentsSupplier() {
        return Stream.of(new MatchArgs("all standard values", "mentee", 2, 153),
                new MatchArgs("zero cost", '\t', false, 0),
                new MatchArgs("negative cost", Byte.MAX_VALUE, Long.MIN_VALUE, -27));
    }
    
    Stream<MatchPairOfArgs> argumentPairsSupplier(){
        return Stream.of(
                new MatchPairOfArgs("same types of mentees and mentors", 
                        new MatchArgs("first element", "mentee", "mentor", 12), 
                        new MatchArgs("second element", "secondMentee", "secondMentor", 8765)),
                new MatchPairOfArgs("same types and identical mentees",
                        new MatchArgs("first element", "mentee", "mentor", 63),
                        new MatchArgs("second element", "mentee", "secondMentor", 63)),
                new MatchPairOfArgs("same types and identical mentors", 
                        new MatchArgs("first element", "mentee", "mentor", -3), 
                        new MatchArgs("second element", "secondMentee", "mentor", -3)),
                new MatchPairOfArgs("different types of mentees and mentors",
                        new MatchArgs("first element", "mentee", "mentor", -17194),
                        new MatchArgs("second element", 6234, false, -17194))
        );
    }
    
    @TestFactory
    Stream<DynamicNode> gettersReturnExpectedValue(){
        return test("getXXX()", args -> {
            Match<?, ?> match = args.convert();
            Assertions.assertAll(
                    () -> Assertions.assertEquals(args.mentee, match.getMentee()),
                    () -> Assertions.assertEquals(args.mentor, match.getMentor()),
                    () -> Assertions.assertEquals(args.cost, match.getCost())
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> equalsReturnsTrueWhenAppropriate(){
        return test("equals() on equal values", args -> 
                Assertions.assertEquals(args.convert(), args.convert()));
    }
    
    @TestFactory
    Stream<DynamicNode> equalsReturnsFalseWhenAppropriate(){
        return test(argumentPairsSupplier(), "equals() on different values", args -> 
                Assertions.assertNotEquals(args.first.convert(), args.second.convert()));
    }
    
    @TestFactory
    Stream<DynamicNode> hashCodeReturnsSameValueOnEqualInput(){
        return test("hashCode() on equal values", args ->
                Assertions.assertEquals(args.convert().hashCode(), args.convert().hashCode()));
    }
    
    @TestFactory
    Stream<DynamicNode> hashCodeReturnsConstantValue(){
        return test("hashCode() repeatedly", args -> {
           Match<Object, Object> match = args.convert();
           Assertions.assertEquals(match.hashCode(), match.hashCode());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> hashCodeReturnsDifferentValuesWhenAppropriate(){
        return test(argumentPairsSupplier(), "hashCode() on different values", args ->
                Assertions.assertNotEquals(args.first.convert().hashCode(), 
                        args.second.convert().hashCode()));
    }
    
    public static record MatchArgs(String testCase, Object mentee, Object mentor, int cost) {
        public Match<Object, Object> convert(){
            return new Match<>(mentee, mentor, cost);
        }
        
        @SuppressWarnings("unchecked")
        public <T,U> Match<T, U> convertAs(Class<T> first, Class<U> second){
            return new Match<>((T) mentee, (U) mentor, cost);
        }
        
        @Override
        public String toString(){
            return testCase;
        }
    }
    
    static record MatchPairOfArgs(String testCase, MatchArgs first, MatchArgs second) {
        @Override
        public String toString(){
            return testCase;
        }
    }
}
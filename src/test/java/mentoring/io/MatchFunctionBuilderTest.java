package mentoring.io;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.io.MatchFunctionBuilderTest.MatchFunctionBuilderArgs;
import mentoring.match.Match;
import mentoring.match.MatchTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class MatchFunctionBuilderTest implements TestFramework<MatchFunctionBuilderArgs>{
    //TODO: use tokens from MatchFunctionBuilder to generate test strings.

    @Override
    public Stream<MatchFunctionBuilderArgs> argumentsSupplier() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @TestFactory
    Stream<DynamicNode> buildMatchFunction_validInput() {
        PersonBuilder builder = new PersonBuilder();
        return test(Stream.of(new MatchFunctionBuilderArgs("Simple string property", 
                "§§Mentor§_§Ville",
                Map.of(
                        new MatchTest.MatchArgs("", builder.withProperty("Ville","Londres").build(),
                                builder.withProperty("Ville", "Paris").build(),
                                5).convertAs(Person.class, Person.class),
                        "Paris",
                        new MatchTest.MatchArgs("", builder.build(),
                                builder.withFullName("foo").withProperty("Ville", "Reims").build(),
                                12).convertAs(Person.class, Person.class),
                        "Reims"))),
                "buildMatchFunction() returns the correct function on valid input",
                MatchFunctionBuilderArgs::assertFunctionIsCorrect);
    }
 
    @TestFactory
    Stream<DynamicNode> buildMatchFunction_invalidInput() {
        return test(Stream.of(new MatchFunctionBuilderArgs("invalid object accessor", "§§foo§_§bar")),
                "buildMatchFunction() throws the expected exception on invalid input",
                args -> Assertions.assertThrows(IllegalArgumentException.class,
                        () -> args.convert()));
    }
    
    @TestFactory
    Stream<DynamicNode> buildPersonPropertyGetter_validInput() {
        PersonBuilder builder = new PersonBuilder();
        return test(Stream.of(
                new PropertyGetterBuilderArgs("simple property", "§_§Ville",
                        Map.of(builder.withProperty("Ville","Londres").build(), "Londres",
                                builder.withFullName("foo").withProperty("Ville", 3).build(), 3)),
                new PropertyGetterBuilderArgs("name property", 
                        MatchFunctionBuilder.NAME_PROPERTY_TOKEN,
                        Map.of(builder.withFullName("foo").build(), "foo"))),
                "buildMatchFunction() returns the correct function on valid input",
                PropertyGetterBuilderArgs::assertFunctionIsCorrect);
    }
    
    static abstract class AbstractFunctionBuilderArgs<K, V> extends TestArgs {
        final String input;
        final Map<K, V> verificationData;
        
        AbstractFunctionBuilderArgs(String testCase, String input, Map<K, V> verificationData){
            super(testCase);
            this.input = input;
            this.verificationData = verificationData;
        }
        
        abstract Function<K, V> convert();
        
        void assertFunctionIsCorrect(){
            Function<K, V> function = convert();
            Assertions.assertAll(verificationData.keySet().stream().map(key -> 
                    () -> Assertions.assertEquals(verificationData.get(key), function.apply(key))));
        }
        
    }
    
    static class MatchFunctionBuilderArgs extends 
            AbstractFunctionBuilderArgs<Match<Person, Person>, String>{
        
        MatchFunctionBuilderArgs(String testCase, String input, 
                Map<Match<Person, Person>, String> verificationData){
            super(testCase, input, verificationData);
        }
        
        MatchFunctionBuilderArgs(String testCase, String input){
            this(testCase, input, null);
        }
        
        @Override
        Function<Match<Person, Person>, String> convert(){
            return MatchFunctionBuilder.buildMatchFunction(input);
        }
    }
    
    static class PropertyGetterBuilderArgs extends AbstractFunctionBuilderArgs<Person, Object>{
        
        PropertyGetterBuilderArgs(String testCase, String input, 
                Map<Person, Object> verificationData){
            super(testCase, input, verificationData);
        }
        
        PropertyGetterBuilderArgs(String testCase, String input){
            this(testCase, input, null);
        }
        
        @Override
        Function<Person, Object> convert(){
            return MatchFunctionBuilder.buildPersonPropertyGetter(input);
        }
    }
}
package mentoring.io;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.io.MatchFunctionDecoderTest.MatchFunctionDecoderArgs;
import mentoring.match.Match;
import mentoring.match.MatchTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class MatchFunctionDecoderTest implements TestFramework<MatchFunctionDecoderArgs>{

    @Override
    public Stream<MatchFunctionDecoderArgs> argumentsSupplier() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @TestFactory
    Stream<DynamicNode> decodeMatchFunction_validInput() {
        PersonBuilder builder = new PersonBuilder();
        return test(Stream.of(
                new MatchFunctionDecoderArgs("simple string property", 
                        MatchFunctionDecoder.MENTOR_TOKEN 
                                + MatchFunctionDecoder.CUSTOM_SIMPLE_PROPERTY_TOKEN + "Ville",
                        Map.of(
                                new MatchTest.MatchArgs("", 
                                        builder.withProperty("Ville","Londres").build(),
                                        builder.withProperty("Ville", "Paris").build(),
                                        5).convertAs(Person.class, Person.class),
                                "Paris",
                                new MatchTest.MatchArgs("", builder.build(),
                                        builder.withFullName("foo").withProperty("Ville", "Reims").build(),
                                        12).convertAs(Person.class, Person.class),
                                "Reims")),
                new MatchFunctionDecoderArgs("cost property", 
                        MatchFunctionDecoder.COST_TOKEN,
                        Map.of(
                                new MatchTest.MatchArgs("", builder.build(), builder.build(), 
                                        12).convertAs(Person.class, Person.class),
                                12)),
                new MatchFunctionDecoderArgs("multiple property",
                        MatchFunctionDecoder.MENTEE_TOKEN 
                                + MatchFunctionDecoder.CUSTOM_MULTIPLE_PROPERTY_TOKEN + "Sports",
                        Map.of(
                                new MatchTest.MatchArgs("", 
                                        builder.withPropertyMap("Sports", Map.of(true, false))
                                                .build(),
                                        builder.build(), 761).convertAs(Person.class, Person.class),
                                Map.of(true, false)))),
                "decodeMatchFunction() returns the correct function on valid input",
                MatchFunctionDecoderArgs::assertFunctionIsCorrect);
    }
 
    @TestFactory
    Stream<DynamicNode> decodeMatchFunction_invalidInput() {
        return test(Stream.of(new MatchFunctionDecoderArgs("invalid object accessor", 
                MatchFunctionDecoder.STANDARD_TOKEN + "foo" 
                        + MatchFunctionDecoder.CUSTOM_SIMPLE_PROPERTY_TOKEN + "bar")),
                "decodeMatchFunction() throws the expected exception on invalid input",
                args -> Assertions.assertThrows(IllegalArgumentException.class,
                        () -> args.convert()));
    }
    
    @TestFactory
    Stream<DynamicNode> decodePersonPropertyGetter_validInput() {
        PersonBuilder builder = new PersonBuilder();
        return test(Stream.of(
                new PropertyGetterBuilderArgs("simple property", 
                        MatchFunctionDecoder.CUSTOM_SIMPLE_PROPERTY_TOKEN + "Ville",
                        Map.of(builder.withProperty("Ville","Londres").build(), "Londres",
                                builder.withFullName("foo").withProperty("Ville", 3).build(), 3)),
                new PropertyGetterBuilderArgs("name property", 
                        MatchFunctionDecoder.NAME_PROPERTY_TOKEN,
                        Map.of(builder.withFullName("foo").build(), "foo")),
                new PropertyGetterBuilderArgs("multiple property", 
                        MatchFunctionDecoder.CUSTOM_MULTIPLE_PROPERTY_TOKEN + "Sports",
                        Map.of(builder
                                .withPropertyMap("Sports", Map.of("Escrime", true, "Volley", 7))
                                .build(), Map.of("Escrime", true, "Volley", 7)))),
                "decodeMatchFunction() returns the correct function on valid input",
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
    
    static class MatchFunctionDecoderArgs extends 
            AbstractFunctionBuilderArgs<Match<Person, Person>, Object>{
        
        MatchFunctionDecoderArgs(String testCase, String input, 
                Map<Match<Person, Person>, Object> verificationData){
            super(testCase, input, verificationData);
        }
        
        MatchFunctionDecoderArgs(String testCase, String input){
            this(testCase, input, null);
        }
        
        @Override
        Function<Match<Person, Person>, Object> convert(){
            return MatchFunctionDecoder.decodeMatchFunction(input);
        }
    }
    
    static class PropertyGetterBuilderArgs extends AbstractFunctionBuilderArgs<Person, Object>{
        
        PropertyGetterBuilderArgs(String testCase, String input, 
                Map<Person, Object> verificationData){
            super(testCase, input, verificationData);
        }
        
        @Override
        Function<Person, Object> convert(){
            return MatchFunctionDecoder.decodePersonPropertyGetter(input);
        }
    }
}
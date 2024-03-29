package mentoring.io;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.io.PersonFileParserTest.PersonFileParserArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;


class PersonFileParserTest implements TestFramework<PersonFileParserArgs>{
    static final String NEWLINE = System.getProperty("line.separator");
    
    @Override
    public Stream<PersonFileParserArgs> argumentsSupplier(){
        return Stream.of(
                new PersonFileParserArgs("no person", 
                        DummyPersonConfiguration.NAME_PROPERTIES.configuration, 
                        "fifth", List.of()),
                new PersonFileParserArgs("one person", 
                        DummyPersonConfiguration.MULTIPLE_PROPERTIES.configuration, 
                        "third,fourth" + NEWLINE + "vrai|faux,0", List.of(
                                new PersonBuilder().withPropertyMap("third", Map.of(true, 0, false, 1))
                                        .withPropertyMap("fourth", Map.of(0,0))
                                        .withFullName("").build())),
                new PersonFileParserArgs("three persons", 
                        DummyPersonConfiguration.SIMPLE_PROPERTIES.configuration,
                        "first,second" + NEWLINE + "string,1" + NEWLINE + "foo,2" + NEWLINE + "bar,3", List.of(
                                new PersonBuilder().withProperty("first", "string")
                                        .withProperty("second", 1).withFullName("").build(),
                                new PersonBuilder().withProperty("first","foo")
                                        .withProperty("second", 2).withFullName("").build(),
                                new PersonBuilder().withProperty("first","bar")
                                        .withProperty("second",3).withFullName("").build()
                        )));
    }
    
    @TestFactory
    Stream<DynamicNode> personFileParser_npe(){
        return test(Stream.of((PersonConfiguration) null), "personFileParser() fails on null input",
                args -> Assertions.assertThrows(NullPointerException.class, () -> 
                        new PersonFileParser(args)));
    }
    
    @TestFactory
    Stream<DynamicNode> personFileParser_validInput(){
        return test(Stream.of(DummyPersonConfiguration.SIMPLE_PROPERTIES), 
                "personFileParser() succeeds on valid input", args -> 
                        new PersonFileParser(args.configuration));
    }
    
    @TestFactory
    Stream<DynamicNode> parse_CorrectResult(){
        return test("parse() returns the expected person", args ->{
            PersonFileParser parser = args.convert();
            try{
                args.assertCorrectParsing(parser.parse(args.getReader()));
            } catch (IOException e){
                Assertions.fail(e);
            }
        });
    }
    
    static record PersonFileParserArgs(String testCase, PersonConfiguration configuration, 
        String input, List<Person> output) {
        
        @Override
        public String toString(){
            return this.testCase;
        }
        
        PersonFileParser convert(){
            return new PersonFileParser(configuration);
        }
        
        Reader getReader(){
            return new StringReader(input);
        }
        
        void assertCorrectParsing(List<Person> actual){
            Map<Person, Integer> actualCount = new HashMap<>();
            actual.forEach(person ->
                actualCount.put(person, 1 + actualCount.getOrDefault(person, 0)));
            output.forEach(person -> {
                if (actualCount.containsKey(person)){
                    if (actualCount.get(person) == 1){
                        actualCount.remove(person);
                    } else {
                        actualCount.put(person, actualCount.get(person)-1);
                    }
                } else {
                    actualCount.put(person, -1);
                }
            });
            Assertions.assertEquals(0, actualCount.size(), 
                    () -> String.format("Expected %s to be empty", actualCount));
        }
    }
}

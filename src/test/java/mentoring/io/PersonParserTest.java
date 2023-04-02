package mentoring.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.io.PersonParserTest.PersonParserArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

class PersonParserTest implements TestFramework<PersonParserArgs>{
    
    @Override
    public Stream<PersonParserArgs> argumentsSupplier(){
        throw new UnsupportedOperationException("not implemented yet");
    }
    
    @TestFactory
    Stream<DynamicNode> PersonParser_Npe(){
        return test(Stream.of(
                new PersonParserArgs("null configuration", null, new String[]{""}, Map.of()),
                new PersonParserArgs("null header", 
                        DummyPersonConfiguration.ALL_PROPERTIES.configuration, null, Map.of()),
                new PersonParserArgs("all args null", null, null, Map.of())),
                "PersonParser() throws NPEs", args -> 
                        Assertions.assertThrows(NullPointerException.class, 
                                () -> args.convertWithException()));
    }
    
    @TestFactory
    Stream<DynamicNode> PersonParser_MissingAttributes(){
        return test(DummyPersonConfiguration.getConfigurations(), 
                "PersonParser() throws IOException on missing attributes in header", 
                configuration -> {
                    PersonParserArgs args = new PersonParserArgs(configuration.toString(), 
                            configuration, new String[]{"third, second"}, Map.of());
                    Assertions.assertThrows(IOException.class, () -> args.convertWithException());
                });
    }
    
    @TestFactory
    Stream<DynamicNode> PersonParser_DuplicateAttributes(){
        return test(Stream.of(
                new PersonParserArgs(DummyPersonConfiguration.SIMPLE_PROPERTIES, 
                        new String[]{"second","second","first"}, Map.of()),
                new PersonParserArgs(DummyPersonConfiguration.MULTIPLE_PROPERTIES, 
                        new String[]{"first","third","fourth","fourth"}, Map.of()),
                new PersonParserArgs(DummyPersonConfiguration.NAME_PROPERTIES, 
                        new String[]{"fifth","fifth","fifth"},Map.of()),
                new PersonParserArgs(DummyPersonConfiguration.ALL_PROPERTIES,
                        new String[]{"first", "second", "third", "fourth", "fifth", "sixth", 
                            "seventh", "second"}, Map.of())),
                "PersonParser() throws IOException on duplicate attributes in header",
                args -> Assertions.assertThrows(IOException.class, 
                        () -> args.convertWithException()));
    }
    
    @TestFactory
    Stream<DynamicNode> PersonParser_validInput(){
        String[] header = new String[]{"eigth","seventh","sixth","fifth","fourth","third","second",
            "first"};
        return test(Arrays.stream(DummyPersonConfiguration.values()),
                "PersonParser() does not throw exceptions on valid inputs", configuration ->{
                    new PersonParserArgs(configuration, header, Map.of()).convert();
                });
    }
    
    @TestFactory
    Stream<DynamicNode> parseLine(){
        return test(Stream.of(
                new PersonParserArgs(DummyPersonConfiguration.SIMPLE_PROPERTIES, 
                        new String[]{"second","third","first"}, 
                        Map.of(new String[]{"12","foo","bar"}, 
                                new PersonBuilder().withFullName("")
                                        .withProperty("first", "bar")
                                        .withProperty("second", 12).build())),
                new PersonParserArgs(DummyPersonConfiguration.MULTIPLE_PROPERTIES, 
                        new String[]{"first","third","fourth"},
                        Map.of(new String[]{"foo","vrai","-43|12"},
                                new PersonBuilder().withFullName("")
                                        .withPropertyMap("fourth", Map.of(12,0,-43,0))
                                        .withPropertyMap("third", Map.of(true, 0)).build())),
                new PersonParserArgs(DummyPersonConfiguration.NAME_PROPERTIES, 
                        new String[]{"fifth"}, 
                        Map.of(new String[]{"12"}, new PersonBuilder().withFullName("12").build(),
                                new String[]{"foo"}, new PersonBuilder().withFullName("foo").build())),
                new PersonParserArgs(DummyPersonConfiguration.ALL_PROPERTIES,
                        new String[]{"first", "second", "third", "fourth", "fifth", "sixth", "seventh"},
                        Map.of(new String[]{"foo","-98765432","oui|faux|vrai","2147483647|0","bar","foo","barfoo"},
                                new PersonBuilder().withProperty("pFirst", "foo")
                                        .withProperty("pSecond", -98765432)
                                        .withPropertyMap("pThird", Map.of(true, 0, false, 1))
                                        .withPropertyMap("pFourth", Map.of(0, 1, 2147483647, 0))
                                        .withFullName("bar foo barfoo").build()))),
                "parseLine() returns the expected Person", args -> args.assertCorrectParsing());
    }
    
    static record PersonParserArgs(String testCase, PersonConfiguration configuration, 
        String[] header, Map<String[], Person> persons) {
        
        PersonParserArgs(DummyPersonConfiguration dummyConfiguration, String[] header, 
                Map<String[], Person> persons){
            this(dummyConfiguration.toString(), dummyConfiguration.configuration, header, persons);
        }
        
        @Override
        public String toString(){
            return testCase;
        }
        
        PersonParser convertWithException() throws IOException{
            return new PersonParser(configuration, header);
        }
        
        PersonParser convert(){
            try {
                return new PersonParser(configuration, header);
            } catch (IOException e){
                throw new UncheckedIOException(e);
            }
        }
        
        void assertCorrectParsing(){
            PersonParser parser = convert();
            Assertions.assertAll(persons.entrySet().stream().map(entry -> () ->
                    Assertions.assertEquals(entry.getValue(), parser.parseLine(entry.getKey()))));
        }
    }
}
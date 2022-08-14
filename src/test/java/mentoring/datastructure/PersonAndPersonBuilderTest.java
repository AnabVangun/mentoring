package mentoring.datastructure;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

final class PersonAndPersonBuilderTest implements 
        TestFramework<PersonAndPersonBuilderTest.PersonArgs> {

    @Override
    public Stream<PersonArgs> argumentsSupplier() {
        return Stream.of(
                new PersonArgs("Person with simple integer properties", 
                        Map.of("First",1,"Second",-1000),
                        Map.of(), Map.of(), "person with integer properties"),
                new PersonArgs("Person with simple string properties", Map.of(),
                        Map.of("première","string","seconde",""), Map.of(), 
                        "person with string properties"),
                new PersonArgs("Person with simple multiple string properties", Map.of(),
                        Map.of(), Map.of("premier",Set.of("1","&\t"),"deuxième",Set.of()),
                        "person with multiple string properties"),
                new PersonArgs("Person with all properties", Map.of("Un entier", Integer.MAX_VALUE),
                        Map.of("s1","1","s2","02","s3","aa3","s4","_'&4"),
                        Map.of("un set", Set.of("1"), "deux sets", Set.of("a","b","c","d")),
                        "person with c0mpl3x n@me")
        );
    }
    
    @TestFactory
    Stream<DynamicNode> failFastOnNullInput(){
        Class<NullPointerException> exception = NullPointerException.class;
        return test(Stream.of(new PersonArgs("specific input", null, null, null, null)), 
                "fail-fast on null input", args -> {
                    PersonBuilder builder = new PersonBuilder();
                    Assertions.assertAll(
                            () -> assertThrowsExceptionWhenSettingNullProperty(exception,
                                    (s, i) -> builder.withProperty(s, i),
                                    0),
                            () -> assertThrowsExceptionWhenSettingNullProperty(exception,
                                    (s, set) -> builder.withPropertySet(s, set), 
                                    Set.of("")),
                            () -> Assertions.assertThrows(exception, 
                                    () -> builder.withFullName(null))
                    );
                });
    }
    
    private static <T> void assertThrowsExceptionWhenSettingNullProperty(
            Class<? extends Exception> exception,
            BiConsumer<String, T> consumer, 
            T defaultValue){
        Assertions.assertAll(
                () -> Assertions.assertThrows(exception, 
                        () -> consumer.accept(null, defaultValue)),
                () -> Assertions.assertThrows(exception,
                        () -> consumer.accept("non-null String", null)),
                () -> Assertions.assertThrows(exception, 
                        () -> consumer.accept(null, null))
        );
    }
    
    @TestFactory
    Stream<DynamicNode> personHasExpectedProperties(){
        return test("PersonBuilder.build() correctly initialise Person", args -> {
            PersonBuilder builder = args.initialisePersonBuilder();
            Person person = builder.build();
            args.assertPersonHasExpectedProperties(person);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> personBuilderCannotMutatePersonAfterCreation(){
        String undefinedProperty = "____UNDEFINED_____";
        return test("PersonBuilder cannot alter Person after creation", args -> {
            PersonBuilder builder = args.initialisePersonBuilder();
            Person person = builder.build();
            builder.withProperty(undefinedProperty, 0);
            builder.withPropertySet(undefinedProperty, Set.of());
            
            Class expectedException = IllegalArgumentException.class;
            
            Assertions.assertAll(
                    () -> Assertions.assertThrows(expectedException, 
                            () -> person.getPropertyAs(undefinedProperty, Integer.class)),
                    () -> Assertions.assertThrows(expectedException,
                            () -> person.getPropertyAsSetOf(undefinedProperty, Boolean.class)),
                    () -> args.assertPersonHasExpectedProperties(person)
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> multipleStringPropertiesAreImmutable(){
        String empty_set_name = "EMPTY";
        String other_set_name = "OTHER";
        Set<String> mutableSet = new HashSet<>();
        mutableSet.addAll(Set.of("first", "second", "third"));
        Stream<PersonArgs> stream = Stream.of(new PersonArgs("empty multiple string property",
                Map.of(), Map.of(), 
                Map.of(empty_set_name, new HashSet<>(),other_set_name, mutableSet), "name"));
        Class exception = UnsupportedOperationException.class;
        return test(stream, "getMultipleStringProperty() returns immutable set", args -> {
           Person person = args.initialisePersonBuilder().build();
           Assertions.assertAll(
                   () -> Assertions.assertThrows(exception, 
                           () -> person.getPropertyAsSetOf(empty_set_name, String.class)
                                   .add("new value")),
                   () -> Assertions.assertThrows(exception, 
                           () -> person.getPropertyAsSetOf(other_set_name, String.class)
                                   .add("new value")),
                   () -> args.assertPersonHasExpectedProperties(person)
           );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> personBuilderChainedCallReturnSelf(){
        Stream<PersonArgs> stream = Stream.of(new PersonArgs("specific case", Map.of(), Map.of(),
                Map.of(), "John Doe"));
        return test(stream, "withProperty() returns self", args -> {
           PersonBuilder builder = new PersonBuilder();
           Assertions.assertAll(
                   () -> Assertions.assertSame(builder, builder.withProperty("1", true)),
                   () -> Assertions.assertSame(builder, 
                           builder.withPropertySet("4", Set.of())),
                   () -> Assertions.assertSame(builder, builder.withFullName("John Doe"))
           );
        });
    }
    
    static class PersonArgs extends TestArgs{
        final Map<String, Integer> integerProperties;
        final Map<String, String> stringProperties;
        final Map<String, Set<String>> multipleStringProperties;
        final String name;
        
        PersonArgs(String testCase, Map<String, Integer> integerProperties,
                Map<String, String> stringProperties,
                Map<String, Set<String>> multipleStringProperties, String name){
            super(testCase);
            this.integerProperties = integerProperties;
            this.stringProperties = stringProperties;
            this.multipleStringProperties = multipleStringProperties;
            this.name = name;
        }
        
        PersonBuilder initialisePersonBuilder(){
            //This proves that withXXXProperty methods modify the builder on which they are called.
            PersonBuilder result = new PersonBuilder();
            integerProperties.entrySet().forEach(entry -> 
                    result.withProperty(entry.getKey(), entry.getValue()));
            stringProperties.entrySet().forEach(entry ->
                    result.withProperty(entry.getKey(), entry.getValue()));
            multipleStringProperties.entrySet().forEach(entry ->
                    result.withPropertySet(entry.getKey(), entry.getValue()));
            result.withFullName(name);
            return result;
        }
        
        void assertPersonHasExpectedProperties(Person person){
            Assertions.assertAll(
                    () -> Assertions.assertAll(
                            integerProperties.entrySet().stream().map(entry -> 
                                    () -> Assertions.assertEquals(entry.getValue(), 
                                            person.getPropertyAs(entry.getKey(), Integer.class)))),
                    () -> Assertions.assertAll(
                            stringProperties.entrySet().stream().map(entry ->
                                    () -> Assertions.assertEquals(entry.getValue(),
                                            person.getPropertyAs(entry.getKey(), String.class)))),
                    () -> Assertions.assertAll(
                            multipleStringProperties.entrySet().stream().map(entry ->
                                    () -> Assertions.assertEquals(entry.getValue(), 
                                            person.getPropertyAsSetOf(entry.getKey(), String.class)))),
                    () -> Assertions.assertEquals(name, person.getFullName())
            );
        }
    }
}

package mentoring.datastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
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
                new PersonArgs("Person with map properties", Map.of(), Map.of(),
                        Map.of("foo", Map.of(1,false), "bar", Map.of(2,true)), 
                        "person with map properties"),
                new PersonArgs("Person with all properties", Map.of("Un entier", Integer.MAX_VALUE),
                        Map.of("s1","1","s2","02","s3","aa3","s4","_'&4"),
                        Map.of("une map", Map.of(6, false), "deux map", Map.of(-72, false)),
                        "person with c0mpl3x n@me")
        );
    }
    
    public Stream<Pair<PersonArgs, PersonArgs>> argumentPairsSupplier(){
        return argumentsSupplier().flatMap(args -> 
                argumentsSupplier().filter(innerArgs -> !innerArgs.name.equals(args.name))
                        .map(innerArgs -> Pair.of(args, innerArgs)));
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
                                    (s, map) -> builder.withPropertyMap(s, map),
                                    Map.of(1,false)),
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
        String undefinedMapProperty = "____UNDEFINED____";
        return test("PersonBuilder cannot alter Person after creation", args -> {
            PersonBuilder builder = args.initialisePersonBuilder();
            Person person = builder.build();
            builder.withProperty(undefinedProperty, 0);
            builder.withPropertyMap(undefinedMapProperty, Map.of());
            
            Class<IllegalArgumentException> expectedException = IllegalArgumentException.class;
            
            Assertions.assertAll(
                    () -> Assertions.assertThrows(expectedException, 
                            () -> person.getPropertyAs(undefinedProperty, Integer.class)),
                    () -> Assertions.assertThrows(expectedException,
                            () -> person.getPropertyAsSetOf(undefinedProperty, Boolean.class)),
                    () -> Assertions.assertThrows(expectedException,
                            () -> person.getPropertyAsMapOf(undefinedMapProperty, Integer.class, 
                                    Boolean.class)),
                    () -> args.assertPersonHasExpectedProperties(person)
            );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> multiplePropertiesAreImmutable(){
        String empty_map_name = "EMPTY";
        String other_map_name = "OTHER";
        Map<Integer, Boolean> mutableMap = new HashMap<>();
        mutableMap.putAll(Map.of(1, true, 2, false));
        Stream<PersonArgs> stream = Stream.of(new PersonArgs("specific test case",
                Map.of(), Map.of(),
                Map.of(empty_map_name, new HashMap<>(),other_map_name, mutableMap), 
                "name"));
        Class<UnsupportedOperationException> exception = UnsupportedOperationException.class;
        return test(stream, "getPropertyAsSetOf() returns immutable set", args -> {
           Person person = args.initialisePersonBuilder().build();
           Assertions.assertAll(
                   () -> Assertions.assertThrows(exception, 
                           () -> person.getPropertyAsSetOf(empty_map_name, String.class)
                                   .add("new value")),
                   () -> Assertions.assertThrows(exception, 
                           () -> person.getPropertyAsSetOf(other_map_name, String.class)
                                   .add("new value")),
                   () -> args.assertPersonHasExpectedProperties(person)
           );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> MapPropertiesAreImmutable(){
        String empty_map_name = "EMPTY";
        String other_map_name = "OTHER";
        Map<Integer, Boolean> mutableMap = new HashMap<>();
        mutableMap.putAll(Map.of(1, true, 2, false));
        Stream<PersonArgs> stream = Stream.of(new PersonArgs("specific test case",
                Map.of(), Map.of(),
                Map.of(empty_map_name, new HashMap<>(),other_map_name, mutableMap), 
                "name"));
        Class<UnsupportedOperationException> exception = UnsupportedOperationException.class;
        return test(stream, "getPropertyAsMapOf() returns immutable map", args -> {
           Person person = args.initialisePersonBuilder().build();
           Assertions.assertAll(
                   () -> Assertions.assertThrows(exception, 
                           () -> person.getPropertyAsMapOf(empty_map_name, Integer.class, 
                                   Boolean.class).put(3, false)),
                   () -> Assertions.assertThrows(exception, 
                           () -> person.getPropertyAsMapOf(other_map_name, Integer.class,
                                   Boolean.class).put(1, false)),
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
                           builder.withPropertyMap("4", Map.of())),
                   () -> Assertions.assertSame(builder, builder.withFullName("John Doe"))
           );
        });
    }
    
    @TestFactory
    Stream<DynamicNode> equalsReturnsTrueWhenAppropriate(){
        return test("equals() on equal values", args -> 
                Assertions.assertEquals(args.initialisePersonBuilder().build(), 
                        args.initialisePersonBuilder().build()));
    }
    
    @TestFactory
    Stream<DynamicNode> equalsIsNotSensitiveToSetOrder(){
        return test(Stream.of(new PersonArgs("specific test case", null,
                null, null, null)), "equals() on equal values with unsorted maps", args -> {
                    Person first = new PersonBuilder().withPropertyMap("property", 
                            Map.of(true, 1, 1, true)).build();
                    Person second = new PersonBuilder().withPropertyMap("property", 
                            Map.of(1, true, true, 1)).build();
                    Assertions.assertEquals(first, second);
                });
    }
    
    @TestFactory
    Stream<DynamicNode> equalsReturnsFalseWhenAppropriate(){
        return test(argumentPairsSupplier(), "equals() on different values", args -> 
                Assertions.assertNotEquals(args.getLeft().initialisePersonBuilder().build(), 
                        args.getRight().initialisePersonBuilder().build()));
    }
    
    
    @TestFactory
    Stream<DynamicNode> hashCodeReturnsSameValueOnEqualInput(){
        return test("hashCode() on equal values", args ->
                Assertions.assertEquals(args.initialisePersonBuilder().build().hashCode(), 
                        args.initialisePersonBuilder().build().hashCode()));
    }
    
    @TestFactory
    Stream<DynamicNode> hashCodeReturnsConstantValue(){
        return test("hashCode() repeatedly", args -> {
            Person person = args.initialisePersonBuilder().build();
            Assertions.assertEquals(person.hashCode(), person.hashCode());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> hashCodeReturnsDifferentValuesWhenAppropriate(){
        return test(argumentPairsSupplier(), "hashCode() on different values", args ->
                Assertions.assertNotEquals(
                        args.getLeft().initialisePersonBuilder().build().hashCode(), 
                        args.getRight().initialisePersonBuilder().build().hashCode()));
    }
    
    static record PersonArgs(String testCase, Map<String, Integer> integerProperties, 
        Map<String, String> stringProperties, Map<String, Map<Integer, Boolean>> mapProperties,
        String name) {
        
        @Override
        public String toString(){
            return testCase;
        }
        
        PersonBuilder initialisePersonBuilder(){
            //This proves that withXXXProperty methods modify the builder on which they are called.
            PersonBuilder result = new PersonBuilder();
            integerProperties.entrySet().forEach(entry -> 
                    result.withProperty(entry.getKey(), entry.getValue()));
            stringProperties.entrySet().forEach(entry ->
                    result.withProperty(entry.getKey(), entry.getValue()));
            mapProperties.entrySet().forEach(entry ->
                    result.withPropertyMap(entry.getKey(), entry.getValue()));
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
                            mapProperties.entrySet().stream().map(entry -> 
                                    () -> Assertions.assertEquals(entry.getValue(), 
                                            person.getPropertyAsMapOf(entry.getKey(), Integer.class, 
                                                    Boolean.class)))),
                    () -> Assertions.assertEquals(name, person.getFullName())
            );
        }
    }
}

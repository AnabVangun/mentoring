package mentoring.datastructure;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class SetPropertyNameTest extends MultiplePropertyNameTest{
    @Override
    public Stream<PropertyArgs<?,?>> argumentsSupplier() {
        return Stream.of(
                new SetPropertyArgs<>("Simple property", "propriété", "headerName", 
                        PropertyType.STRING,
                        new String[]{"first", "second", "third"}, 
                        Set.of("first", "second", "third")),
                new SetPropertyArgs<>("Property with equal names", "name", "name", 
                        PropertyType.INTEGER, 
                        new String[]{"12","-3"}, Set.of(12, -3)),
                new SetPropertyArgs<>("Property with empty name", "", "header_name", 
                        PropertyType.BOOLEAN, new String[]{"vrai","faux"}, Set.of(true, false)),
                new SetPropertyArgs<>("Property with empty headerName", "name", "", 
                        PropertyType.STRING, new String[]{"first"}, Set.of("first")),
                new SetPropertyArgs<>("Property with empty names", "", "", 
                        PropertyType.INTEGER, new String[]{}, Set.of())
            );
    }
    
    @Override
    @TestFactory
    Stream<DynamicNode> getStringRepresentation_expectedValue(){
        return test(Stream.of(
                new SetPropertyArgs<>("specific test case", "propriété", "headerName", 
                        PropertyType.STRING, new String[]{"value"}, Set.of("value"))), 
                "getStringRepresentation() returns the expected value",
                args -> {
                    PropertyName<?> property = args.convert();
                    Person person = new PersonBuilder()
                            .withPropertyMap(property.getName(), args.expectedResult).build();
                    Assertions.assertEquals(Set.of("value").toString(), 
                            property.getStringRepresentation(person));
                });
    }
    
    @Override
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> getStringRepresentation_NPE(){
        Set<String> expected = new LinkedHashSet<>();
        expected.add("first");
        expected.add("second");
        return test(Stream.of(
                new SetPropertyArgs<>("Simple property", "propriété", "headerName", 
                        PropertyType.STRING, new String[]{"first", "second"}, expected)), 
                "getStringRepresentation() returns the expected value",
                args -> {
                    PropertyName<?> property = args.convert();
                    Assertions.assertThrows(NullPointerException.class, 
                            () -> property.getStringRepresentation(null));
                });
    }
    
    static class SetPropertyArgs<K> extends MapPropertyArgs<K,Integer>{

        SetPropertyArgs(String testCase, String name, String headerName, PropertyType<K> keyType,
                String[] mapInput, Set<K> expectedResult){
            super(testCase, name, headerName, keyType, PropertyType.INTEGER, null, mapInput, 
                    expectedResult.stream()
                            .collect(Collectors.toMap(Function.identity(), args -> 0)));
        }

        @Override
        SetPropertyName<K> convert() {
            return new SetPropertyName<>(getExpectedName(), getExpectedHeaderName(), 
                    expectedType);
        }
    }
}
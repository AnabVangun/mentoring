package mentoring.viewmodel.datastructure;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.IndexedPropertyDescription;
import mentoring.datastructure.MultiplePropertyDescription;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyDescription;
import mentoring.datastructure.SimplePropertyDescription;
import mentoring.viewmodel.datastructure.PersonViewModelFactoryTest.PersonViewModelFactoryTestArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class PersonViewModelFactoryTest implements TestFramework<PersonViewModelFactoryTestArgs>{
    @Override
    public Stream<PersonViewModelFactoryTestArgs> argumentsSupplier(){
            Set<MultiplePropertyDescription<?,?>> multipleProperties = new LinkedHashSet<>();
            multipleProperties.add(new IndexedPropertyDescription<>("indexed", "indexed header", PropertyType.BOOLEAN));
            multipleProperties.add(new SetPropertyDescription<>("set", "set header", PropertyType.STRING));
            PersonConfiguration configuration = new PersonConfiguration("configuration", 
                    Set.of(new SimplePropertyDescription<>("simple", "simple header", PropertyType.INTEGER)), 
                    multipleProperties,
                    "|", "%s", List.of("simple header"));
            PersonBuilder builder = new PersonBuilder();
        return Stream.of(
                new PersonViewModelFactoryTestArgs("standard configuration", configuration, 
                        List.of(
                                builder.withProperty("simple", 12)
                                        .withPropertyMap("indexed", Map.of(true,0))
                                        .withPropertyMap("set", Map.of("zero",0))
                                        .withFullName("foo").build()),
                        //FIXME: 12 should be an integer rather than a String, idem for other properties
                        Set.of(Map.of("simple","12","indexed","[true]",
                                "set","[zero]","Name","foo"))));
    }
    
    @TestFactory
    Stream<DynamicNode> create_expectedResult(){
        return test("create() returns the expected result", args ->
                args.assertResultAsExpected(args.convert().create(args.persons)));
    }
    
    /*TODO define test for {@link PersonViewModelFactory#getFullNamePropertyName}*/
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of("specific test case"), "constructor throws NPE", args ->
                Assertions.assertThrows(NullPointerException.class, 
                        () -> new PersonViewModelFactory(null)));
    }
    
    static class PersonViewModelFactoryTestArgs extends TestArgs{
        final PersonConfiguration configuration;
        final Iterable<Person> persons;
        final Set<Map<String, Object>> expectedResult;
        
        PersonViewModelFactoryTestArgs(String testCase, PersonConfiguration configuration,
                Iterable<Person> persons, Set<Map<String, Object>> expectedResult){
            super(testCase);
            this.configuration = configuration;
            this.persons = persons;
            this.expectedResult = expectedResult;
        }
        
        PersonViewModelFactory convert(){
            return new PersonViewModelFactory(configuration);
        }
        
        void assertResultAsExpected(Collection<PersonViewModel> actual){
            Set<Map<String, Object>> actualSet = actual.stream()
                    .map(PersonViewModel::getFormattedData).collect(Collectors.toSet());
            Assertions.assertEquals(expectedResult, actualSet);
        }
    }
}

package mentoring.io;

import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import mentoring.configuration.ExtendedPersonConfigurationArgs;
import mentoring.configuration.ExtendedPersonConfigurationTest;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.IndexedPropertyName;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyName;
import mentoring.io.PersonConfigurationParserTest.PersonConfigurationParserArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class PersonConfigurationParserTest implements 
        ExtendedPersonConfigurationTest<PersonConfigurationParserArgs>{

    @Override
    public Stream<PersonConfigurationParserArgs> argumentsSupplier() {
        return Stream.of(new PersonConfigurationParserArgs("validTestConfiguration.yaml", 
                "standard valid test",
                Set.of(new PropertyName<>("Anglais", "Anglais", PropertyType.BOOLEAN), 
                        new PropertyName<>("Promotion", "Promotion", PropertyType.INTEGER)),
                Set.of(new SetPropertyName<>("Métiers", "Activités et métiers", PropertyType.STRING),
                        new IndexedPropertyName<>("Motivation", "Motivation", PropertyType.STRING)),
                ",", "%s %s (X%s)", List.of("Prénom", "Nom", "Promotion")));
    }
    
    @Override
    public String builderName(){
        return "parse()";
    }
    
    public Stream<PersonConfigurationParserArgs> invalidArgumentsSupplier() {
        return Stream.of(
                new PersonConfigurationParserArgs(
                        "invalidMultiplePropertyAggregationTypeTestConfiguration.yaml",
                        "invalid multiple property aggregation type"),
                new PersonConfigurationParserArgs(
                        "invalidNameDefinitionTestConfiguration.yaml",
                        "inconsistent name definition"),
                new PersonConfigurationParserArgs(
                        "invalidPropertyTypeTestConfiguration.yaml", "invalid property type"),
                new PersonConfigurationParserArgs(
                        "missingMultiplePropertyAttributeTestConfiguration.yaml",
                        "missing multiple property attribute"),
                new PersonConfigurationParserArgs(
                        "missingNameFormatTestConfiguration.yaml", "missing name format"),
                new PersonConfigurationParserArgs(
                        "missingNamePropertiesTestConfiguration.yaml", "missing name properties"),
                new PersonConfigurationParserArgs(
                        "missingNameTestConfiguration.yaml", "missing configuration name"),
                new PersonConfigurationParserArgs(
                        "missingPersonTestConfiguration.yaml", "missing person configuration"),
                new PersonConfigurationParserArgs(
                        "missingPropertyTypeTestConfiguration.yaml", "missing property type"),
                new PersonConfigurationParserArgs(
                        "missingSeparatorTestConfiguration.yaml", "missing separator"));
    }
    
    @TestFactory
    Stream<DynamicNode> parse_invalidInput(){
        return test(invalidArgumentsSupplier(), "parse() on invalid input", args -> 
                Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> args.convertWithException()));
    }
    
    static record PersonConfigurationParserArgs(String filePath, 
        String configurationName,
        Set<PropertyName<?>> propertiesNames, 
        Set<MultiplePropertyName<?,?>> multiplePropertiesNames, 
        String separator, String nameFormat, List<String> namePropertiesHeader)
    implements ExtendedPersonConfigurationArgs{
        
        PersonConfigurationParserArgs(String filePath, String configurationName){
            this(filePath, configurationName, null, null, null, null, null);
        }

        @Override
        public PersonConfiguration convert() {
            try {
                return convertWithException();
            } catch (IOException e){
                throw new UncheckedIOException(e);
            }
        }
        
        public PersonConfiguration convertWithException() throws IOException {
            return new PersonConfigurationParser()
                    .parse(new FileReader(getClass().getResource(filePath).getFile(),
                            Charset.forName("utf-8")));
        }
        
        @Override
        public String toString(){
            return filePath;
        }

        @Override
        public String getExpectedName() {
            return configurationName;
        }

        @Override
        public Set<PropertyName<?>> getExpectedProperties() {
            return propertiesNames;
        }

        @Override
        public Set<MultiplePropertyName<?, ?>> getExpectedMultipleProperties() {
            return multiplePropertiesNames;
        }

        @Override
        public String getExpectedSeparator() {
            return separator;
        }

        @Override
        public String getExpectedNameFormat() {
            return nameFormat;
        }

        @Override
        public List<String> getExpectedNameProperties() {
            return namePropertiesHeader;
        }
    }
}

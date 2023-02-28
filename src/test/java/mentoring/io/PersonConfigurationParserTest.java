package mentoring.io;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
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
import mentoring.io.ParserTest.ParserArgs;
import mentoring.io.PersonConfigurationParserTest.PersonConfigurationParserArgs;
import mentoring.io.datareader.DataReader;
import mentoring.io.datareader.YamlReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class PersonConfigurationParserTest implements 
        ExtendedPersonConfigurationTest<PersonConfigurationParserArgs>, 
        ParserTest<PersonConfiguration, PersonConfigurationParser, PersonConfigurationParserArgs> {

    @Override
    public Stream<PersonConfigurationParserArgs> argumentsSupplier() {
        return Stream.of(
                new PersonConfigurationParserArgs("validTestConfiguration.yaml", 
                        "standard valid test",
                        Set.of(new PropertyName<>("Anglais", "Anglais", PropertyType.BOOLEAN), 
                                new PropertyName<>("Promotion", "Promotion", PropertyType.INTEGER)),
                        Set.of(
                                new SetPropertyName<>("Métiers", "Activités et métiers", 
                                        PropertyType.STRING),
                                new IndexedPropertyName<>("Motivation", "Motivation", 
                                        PropertyType.STRING)),
                        ",", "%s %s (X%s)", List.of("Prénom", "Nom", "Promotion"), new YamlReader()),
                new PersonConfigurationParserArgs("validSecondTestConfiguration.yaml", 
                        "standard second valid test",
                        Set.of(new PropertyName<>("Anglais", "Anglais", PropertyType.YEAR)),
                        Set.of(new SetPropertyName<>("Métiers", "Métiers", 
                                PropertyType.SIMPLIFIED_LOWER_STRING)),
                        ",", "%s", List.of("Prénom"), new YamlReader()));
    }
    
    @Override
    public String builderName(){
        return "parse()";
    }
    
    @Override
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
    
    @Override
    public PersonConfigurationParser prepareParser(){
        return new PersonConfigurationParser(new YamlReader());
    }
    
    static record PersonConfigurationParserArgs(String filePath, 
        String configurationName,
        Set<PropertyName<?>> propertiesNames, 
        Set<MultiplePropertyName<?,?>> multiplePropertiesNames, 
        String separator, String nameFormat, List<String> namePropertiesHeader, DataReader reader)
            implements ExtendedPersonConfigurationArgs, 
                    ParserArgs<PersonConfiguration, PersonConfigurationParser>{
        
        PersonConfigurationParserArgs(String filePath, String configurationName){
            this(filePath, configurationName, null, null, null, null, null, new YamlReader());
        }

        @Override
        public PersonConfiguration convert() {
            try {
                return convertWithException();
            } catch (IOException e){
                throw new UncheckedIOException(e);
            }
        }
        
        @Override
        public PersonConfiguration convertWithException() throws IOException {
            return getParserUnderTest().parse(getDataSource());
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

        @Override
        public PersonConfigurationParser getParserUnderTest() {
            return new PersonConfigurationParser(reader);
        }

        @Override
        public void assertResultAsExpected(PersonConfiguration actual) {
            ExtendedPersonConfigurationArgs.assertResultAsExpected(this, actual);
        }

        @Override
        public Reader getDataSource() throws IOException {
            return new FileReader(getClass().getResource(filePath).getFile(),
                    Charset.forName("utf-8"));
        }
    }
}

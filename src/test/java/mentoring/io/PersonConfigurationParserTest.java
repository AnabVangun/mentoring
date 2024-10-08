package mentoring.io;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import mentoring.configuration.ExtendedPersonConfigurationArgs;
import mentoring.configuration.ExtendedPersonConfigurationTest;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.MultiplePropertyDescription;
import mentoring.datastructure.IndexedPropertyDescription;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyDescription;
import mentoring.datastructure.SimplePropertyDescription;
import mentoring.io.ParserTest.ParserArgs;
import mentoring.io.PersonConfigurationParserTest.PersonConfigurationParserArgs;
import mentoring.io.datareader.DataReader;
import mentoring.io.datareader.YamlReader;

class PersonConfigurationParserTest implements 
        ExtendedPersonConfigurationTest<PersonConfigurationParserArgs>, 
        ParserTest<PersonConfiguration, PersonConfigurationParser, PersonConfigurationParserArgs> {

    @Override
    public Stream<PersonConfigurationParserArgs> argumentsSupplier() {
        return Stream.of(new PersonConfigurationParserArgs("validTestConfiguration.yaml", 
                        "standard valid test",
                        Set.of(new SimplePropertyDescription<>("Anglais", "Anglais", PropertyType.BOOLEAN), 
                                new SimplePropertyDescription<>("Promotion", "Promotion", PropertyType.INTEGER)),
                        Set.of(new SetPropertyDescription<>("Métiers", "Activités et métiers", 
                                        PropertyType.STRING),
                                new IndexedPropertyDescription<>("Motivation", "Motivation", 
                                        PropertyType.STRING)),
                        ",", "%s %s (X%s)", List.of("Prénom", "Nom", "Promotion"), new YamlReader()),
                new PersonConfigurationParserArgs("validSecondTestConfiguration.yaml", 
                        "standard second valid test",
                        Set.of(new SimplePropertyDescription<>("Anglais", "Anglais", PropertyType.YEAR)),
                        Set.of(new SetPropertyDescription<>("Métiers", "Métiers", 
                                PropertyType.SIMPLIFIED_LOWER_STRING)),
                        ",", "%s", List.of("Prénom"), new YamlReader()));
    }
    
    @Override
    public String builderName(){
        return "parse()";
    }
    
    @Override
    public Stream<PersonConfigurationParserArgs> specificallyInvalidArgumentsSupplier() {
        return Stream.of(
                new PersonConfigurationParserArgs(
                        "invalidNameDefinitionTestConfiguration.yaml",
                        "inconsistent name definition", 1));
    }
    
    @Override
    public Stream<PersonConfigurationParserArgs> genericallyInvalidArgumentsSupplier(){
        return Stream.of(
                new PersonConfigurationParserArgs(
                        "invalidMultiplePropertyAggregationTypeTestConfiguration.yaml",
                        "invalid multiple property aggregation type", 0),
                new PersonConfigurationParserArgs(
                        "invalidPropertyTypeTestConfiguration.yaml", "invalid property type", 0),
                new PersonConfigurationParserArgs(
                        "missingMultiplePropertyAttributeTestConfiguration.yaml",
                        "missing multiple property attribute", 0),
                new PersonConfigurationParserArgs(
                        "missingNameFormatTestConfiguration.yaml", "missing name format", 0),
                new PersonConfigurationParserArgs(
                        "missingNamePropertiesTestConfiguration.yaml", "missing name properties", 0),
                new PersonConfigurationParserArgs(
                        "missingNameTestConfiguration.yaml", "missing configuration name", 0),
                new PersonConfigurationParserArgs(
                        "missingPersonTestConfiguration.yaml", "missing person configuration", 0),
                new PersonConfigurationParserArgs(
                        "missingPropertyTypeTestConfiguration.yaml", "missing property type", 0),
                new PersonConfigurationParserArgs(
                        "missingSeparatorTestConfiguration.yaml", "missing separator", 0));
    }
    
    @Override
    public PersonConfigurationParser prepareParser(){
        return new PersonConfigurationParser(new YamlReader());
    }
    
    static record PersonConfigurationParserArgs(String filePath, 
        String configurationName,
        Set<SimplePropertyDescription<?>> propertiesNames, 
        Set<MultiplePropertyDescription<?,?>> multiplePropertiesNames, 
        String separator, String nameFormat, List<String> namePropertiesHeader, DataReader reader,
        int specificErrorsCount)
            implements ExtendedPersonConfigurationArgs, 
                    ParserArgs<PersonConfiguration, PersonConfigurationParser>{
        
        PersonConfigurationParserArgs(String filePath, String configurationName, 
                Set<SimplePropertyDescription<?>> propertiesNames, 
                Set<MultiplePropertyDescription<?,?>> multiplePropertiesNames, 
                String separator, String nameFormat, List<String> namePropertiesHeader, 
                DataReader reader){
            this(filePath, configurationName, propertiesNames, multiplePropertiesNames, separator,
                    nameFormat, namePropertiesHeader, reader, 0);
        }
                
        PersonConfigurationParserArgs(String filePath, String configurationName, 
                int specificErrorsCount){
            this(filePath, configurationName, null, null, null, null, null, new YamlReader(),
                    specificErrorsCount);
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
        public Set<SimplePropertyDescription<?>> getExpectedProperties() {
            return propertiesNames;
        }

        @Override
        public Set<MultiplePropertyDescription<?, ?>> getExpectedMultipleProperties() {
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
        
        @Override
        public Map<String, Object> getData(){
            try {
                return reader.read(getDataSource());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        
        @Override
        public int getExpectedSpecificErrorsCount(){
            return specificErrorsCount;
        }
    }
}

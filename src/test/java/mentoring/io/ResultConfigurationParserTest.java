package mentoring.io;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.io.ParserTest.ParserArgs;
import mentoring.io.ResultConfigurationParserTest.ResultConfigurationParserArgs;
import mentoring.io.datareader.DataReader;
import mentoring.io.datareader.YamlReader;
import mentoring.match.Match;
import mentoring.match.MatchTest.MatchArgs;
import org.junit.jupiter.api.Assertions;
import test.tools.TestToolbox;

class ResultConfigurationParserTest implements ParserTest<ResultConfiguration<Person, Person>, 
        ResultConfigurationParser, ResultConfigurationParserArgs>{

    @Override
    public Stream<ResultConfigurationParserArgs> argumentsSupplier() {
        PersonBuilder builder = new PersonBuilder();
        return Stream.of(
                new ResultConfigurationParserArgs("standard configuration", 
                        "validResultConfigurationTest.yaml", 
                        new String[]{"Mentoré", "Mentor", "Coût", "Propriété"},
                        new String[][]{{"foo", "bar", "5", "Paris"},{"bar", "foo", "12", "Reims"}},
                        List.of(
                                new MatchArgs("", builder.withFullName("foo").build(),
                                        builder.withFullName("bar")
                                                .withProperty("Ville", "Paris").build(),
                                        5).convertAs(Person.class, Person.class),
                                new MatchArgs("", builder.withFullName("bar").build(),
                                        builder.withFullName("foo")
                                                .withProperty("Ville", "Reims").build(),
                                        12).convertAs(Person.class, Person.class)),
                        new YamlReader(), Map.of()),
                new ResultConfigurationParserArgs("configuration with complex persons",
                        "validComplexResultConfigurationTest.yaml",
                        new String[]{"Mentoré", "Première propriété", "Mentor", "Coût", 
                            "Deuxième propriété"},
                        new Object[][]{{"foo1", Map.of("3","6","5","false"), "bar1", "962", "180"}, 
                            {"bar2", Map.of("true","blood","taken","2"), "foo2", "0", "-51"}},
                        List.of(
                                new MatchArgs("", 
                                        builder.withFullName("foo1").withProperty("Anglais", true)
                                                .withPropertyMap("Goûts", Map.of(3,6,5,false)).build(),
                                        builder.withFullName("bar1").withProperty("Taille", 180)
                                                .withProperty("taille", 3).build(),
                                        962).convertAs(Person.class, Person.class),
                                new MatchArgs("",
                                        builder.withFullName("bar2").withProperty("Anglais", false)
                                                .withPropertyMap("Goûts", 
                                                        Map.of(true,"blood","taken",2)).build(),
                                        builder.withFullName("foo2").withProperty("Taille", -51).build(),
                                        0).convertAs(Person.class, Person.class)), 
                        new YamlReader(), 
                        Map.of(1, s -> TestToolbox.recreateMap(s, String.class, String.class))));
    }
    
    @Override
    public Stream<ResultConfigurationParserArgs> invalidArgumentsSupplier(){
        return Stream.of(
                new ResultConfigurationParserArgs("more columns than column descriptions in header", 
                        "missingHeaderColumnsResultConfigurationTest.yaml"),
                new ResultConfigurationParserArgs("less columns than column descriptions in header",
                        "tooManyHeaderColumnsResultConfigurationTest.yaml"),
                new ResultConfigurationParserArgs("unknown magic word in column description",
                        "unknownMagicWordResultConfigurationTest.yaml"));
    }
    
    @Override
    public ResultConfigurationParser prepareParser(){
        return new ResultConfigurationParser(new YamlReader());
    }
    
    static record ResultConfigurationParserArgs(String testCase, String filePath, 
            String[] expectedResultHeader, Object[][] expectedResultLines, 
            List<Match<Person, Person>> matchToPrint, DataReader reader, 
            Map<Integer, Function<String, Object>> mapperFunctions) 
            implements ParserArgs<ResultConfiguration<Person, Person>, ResultConfigurationParser>{
        
        ResultConfigurationParserArgs(String testCase, String filePath){
            this(testCase, filePath, null, null, null, new YamlReader(), null);
        }
        
        @Override
        public String toString(){
            return testCase;
        }
        
        @Override
        public ResultConfiguration<Person, Person> convert(){
            try {
                return convertWithException();
            } catch (IOException e){
                throw new UncheckedIOException(e);
            }
        }
        
        @Override
        public ResultConfiguration<Person, Person> convertWithException() throws IOException{
            return getParserUnderTest().parse(getDataSource());
        }
        @Override
        public ResultConfigurationParser getParserUnderTest() {
            return new ResultConfigurationParser(reader);
        }

        @Override
        public void assertResultAsExpected(ResultConfiguration<Person, Person> actual) {
            Object[][] actualResults = new Object[expectedResultLines.length][];
            for (int i = 0; i < expectedResultLines.length; i++){
                String[] line = actual.getResultLine(matchToPrint.get(i));
                actualResults[i] = new Object[line.length];
                for (int j = 0; j < line.length; j++){
                    actualResults[i][j] = mapperFunctions.containsKey(j) 
                            ? mapperFunctions.get(j).apply(line[j])
                            : line[j];
                }
            }
            Assertions.assertAll("Result configuration is not as expected", 
                    () -> Assertions.assertEquals(testCase, actual.toString()),
                    () -> Assertions.assertArrayEquals(expectedResultHeader, actual.getResultHeader()),
                    () -> Assertions.assertArrayEquals(expectedResultLines, actualResults));
        }

        @Override
        public Reader getDataSource() throws IOException {
            return new FileReader(getClass().getResource(filePath).getFile(),
                            Charset.forName("utf-8"));
        }
    }
}

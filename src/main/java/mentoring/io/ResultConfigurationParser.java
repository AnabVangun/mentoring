package mentoring.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.io.datareader.DataReader;
import mentoring.match.Match;

/**
 * Parser used to build {@link ResultConfiguration} objects from configuration files.
 * <p>Instances of this class can be reused and are thread-safe.
 */
public final class ResultConfigurationParser extends Parser<ResultConfiguration<Person, Person>>{

    public ResultConfigurationParser(DataReader dataReader) {
        super(dataReader);
    }

    @Override
    protected ResultConfiguration<Person, Person> buildObject(Map<String, Object> data) 
            throws IllegalArgumentException{
        String name = extractAttribute(data, "configurationName", String.class);
        List<String> resultHeader = extractAttributeList(data, "header", String.class);
        List<String> lineDescription = extractAttributeList(data, "lineDescription", String.class);
        assertValidResultHeader(resultHeader, lineDescription);
        return new ResultConfiguration<>(name, resultHeader, buildLineFormatter(lineDescription));
    }
    
    private static void assertValidResultHeader(List<String> resultHeader, 
            List<String> lineDescription){
        if (resultHeader.size() != lineDescription.size()){
            throw new IllegalArgumentException("%s and %s do not have the same length"
                    .formatted(resultHeader, lineDescription));
        }
    }
    
    private static Function<Match<Person, Person>, String[]> buildLineFormatter(
            List<String> lineDescription){
        List<Function<Match<Person, Person>, String>> functions = 
                new ArrayList<>(lineDescription.size());
        lineDescription.forEach(column -> 
                functions.add(MatchFunctionDecoder.decodeMatchFunction(column)));
        return args -> {
            String[] result = new String[lineDescription.size()];
            for (int index = 0; index < result.length; index++){
                result[index] = functions.get(index).apply(args);
            }
            return result;
        };
    }
}

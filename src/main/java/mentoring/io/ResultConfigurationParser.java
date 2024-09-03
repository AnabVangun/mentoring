package mentoring.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    //TODO modify ResultConfigurationParser to make the parsed file a map between a header and its column

    public ResultConfigurationParser(DataReader dataReader) {
        super(dataReader);
    }
    
    private static final String CONFIGURATION_NAME_KEY = "configurationName";
    private static final String HEADER_KEY = "header";
    private static final String LINE_DESCRIPTION_KEY = "lineDescription";
    private static final Set<String> EXPECTED_KEYS = 
            Set.of(CONFIGURATION_NAME_KEY, HEADER_KEY, LINE_DESCRIPTION_KEY);

    @Override
    protected ResultConfiguration<Person, Person> buildObject(Map<String, Object> data) 
            throws IllegalArgumentException{
        String name = extractAttribute(data, CONFIGURATION_NAME_KEY, String.class);
        List<String> resultHeader = extractAttributeList(data, HEADER_KEY, String.class);
        List<String> lineDescription = extractAttributeList(data, LINE_DESCRIPTION_KEY, String.class);
        return ResultConfiguration.createForArrayLine(name, resultHeader, 
                buildLineFormatter(lineDescription));
    }
    
    private static Function<Match<Person, Person>, Object[]> buildLineFormatter(
            List<String> lineDescription){
        List<Function<Match<Person, Person>, Object>> functions = 
                new ArrayList<>(lineDescription.size());
        lineDescription.forEach(column -> 
                functions.add(MatchFunctionDecoder.decodeMatchFunction(column)));
        return args -> {
            Object[] result = new Object[lineDescription.size()];
            for (int index = 0; index < result.length; index++){
                result[index] = functions.get(index).apply(args);
            }
            return result;
        };
    }
    
    @Override
    protected List<String> registerSpecificErrors(Map<String, Object> data){
        List<String> result = new ArrayList<>();
        registerInconsistentHeaderLengthErrorIfAppropriate(data, result);
        return result;
    }
    
    private static void registerInconsistentHeaderLengthErrorIfAppropriate(Map<String, Object> data,
            List<String> errors){
        if (data.containsKey(HEADER_KEY) && data.containsKey(LINE_DESCRIPTION_KEY)){
            List<String> resultHeader = extractAttributeList(data, HEADER_KEY, String.class);
            List<String> lineDescription = extractAttributeList(data, 
                    LINE_DESCRIPTION_KEY, String.class);
            if (! isValidResultHeader(resultHeader, lineDescription)){
                errors.add("%s and %s do not have the same length"
                        .formatted(resultHeader, lineDescription));
            }
        }
    }
    
    private static boolean isValidResultHeader(List<String> resultHeader, 
            List<String> lineDescription){
        return (resultHeader.size() == lineDescription.size());
    }
    
    @Override
    protected Set<String> getExpectedKeys(){
        return EXPECTED_KEYS;
    }
}

package mentoring.io.datareader;

import java.io.Reader;
import java.util.Map;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

/**
 * Parser used to extract data from a YAML file.
 * <p>Instances of this class can be reused and are thread-safe.
 */
public class YamlReader extends DataReader {
    private final LoadSettings yamlSettings = LoadSettings.builder().build();
    
    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Object> readWithException(Reader reader) throws YamlEngineException{
        return (Map<String, Object>) (new Load(yamlSettings).loadFromReader(reader));
    }
}

package mentoring.io.yaml;

import java.io.Reader;
import java.util.Map;
import java.util.Objects;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

/**
 * Parser used to extract data from a YAML file.
 * <p>Instances of this class can be reused but are not thread-safe.
 */
public class YamlParser {
    private final Load yamlReader = new Load(LoadSettings.builder().build());
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> parse(Reader reader){
        Objects.requireNonNull(reader);
        try {
            return (Map<String, Object>) yamlReader.loadFromReader(reader);
        } catch (YamlEngineException e){
            throw new IllegalArgumentException("Could not parse YAML content from reader", e);
        }
    }
}

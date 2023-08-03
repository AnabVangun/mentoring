package mentoring.configuration;

import java.util.List;

/**
 * Base abstract class for all types of configurations determining how to run the application.
 * @param <T> self-type of the subclass
 */
public abstract class Configuration<T extends Configuration<T>> {
    final private String configurationName;
    
    protected Configuration(String configurationName){
        this.configurationName = configurationName;
    }
    
    @Override
    public String toString(){
        return configurationName;
    }
}

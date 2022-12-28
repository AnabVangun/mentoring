package mentoring.configuration;

import java.util.List;
import java.util.Set;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.PropertyName;

public interface ExtendedPersonConfigurationArgs extends SimplePersonConfigurationArgs {
    
    String getExpectedName();
    
    Set<PropertyName<?>> getExpectedProperties();
    
    Set<MultiplePropertyName<?,?>> getExpectedMultipleProperties();
    
    String getExpectedSeparator();
    
    String getExpectedNameFormat();
    
    List<String> getExpectedNameProperties();
}

package mentoring.configuration;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.PropertyName;

public interface PersonConfiguration {
    
    Set<PropertyName> getBooleanPropertiesNames();
    
    Set<PropertyName> getIntegerPropertiesNames();
    
    Set<PropertyName> getStringPropertiesNames();
    
    Set<PropertyName> getMultipleStringPropertiesNames();
    
    String getSeparator();
    
    String getNameFormat();
    
    List<String> getNamePropertiesHeaderNames();
    
    Collection<String> getAllPropertiesHeaderNames();
}

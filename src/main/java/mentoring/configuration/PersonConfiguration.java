package mentoring.configuration;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.Property;

public interface PersonConfiguration {
    
    Set<Property> getBooleanProperties();
    
    Set<Property> getIntegerProperties();
    
    Set<Property> getStringProperties();
    
    Set<Property> getMultipleStringProperties();
    
    String getSeparator();
    
    String getNameFormat();
    
    List<String> getNamePropertiesHeaderNames();
    
    Collection<String> getAllPropertiesHeaderNames();
}

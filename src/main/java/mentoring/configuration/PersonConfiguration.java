package mentoring.configuration;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.PropertyName;

public interface PersonConfiguration {
    //TODO document the difference between SimpleProperties and SetProperties
    
    Set<PropertyName<? extends Object>> getPropertiesNames();
    
    Set<PropertyName<? extends Object>> getMultiplePropertiesNames();
    
    String getSeparator();
    
    String getNameFormat();
    
    List<String> getNamePropertiesHeaderNames();
    
    Collection<String> getAllPropertiesHeaderNames();
}

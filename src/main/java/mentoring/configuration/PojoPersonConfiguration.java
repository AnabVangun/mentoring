package mentoring.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.PropertyName;

public enum PojoPersonConfiguration implements PersonConfiguration{
    TEST_CONFIGURATION(Set.of(new PropertyName<>("Anglais", Boolean.class), 
            new PropertyName<>("Promotion", Integer.class)), 
            Set.of(new PropertyName<>("Métiers","Activités et métiers", String.class), 
                    new PropertyName<>("Motivation", String.class)), 
            ";", "%s %s (X%s)", List.of("Prénom","Nom","Promotion")),
    MENTEE_CONFIGURATION_REAL_DATA(Set.of(
            new PropertyName<>("Promotion","Promotion, cycle", String.class),
            new PropertyName<>("Maturité","maturité", Integer.class),
            new PropertyName<>("Email","E-mail", String.class)),
            Set.of(new PropertyName<>("Métiers","résumé métier secteur", String.class), 
                    new PropertyName<>("Langue","Option : langue préférentielle", String.class)),
            ",", "%s %s (X%s)", List.of("Prénom", "NOM", "Promotion, cycle")),
    MENTOR_CONFIGURATION_REAL_DATA(Set.of(
            new PropertyName<>("Anglophone","anglophone", Boolean.class),
            new PropertyName<>("Promotion","Promotion (X09, ...)", String.class),
            new PropertyName<>("Email","Adresse email", String.class)),
            Set.of(new PropertyName<>("Métiers","Résumé métier secteur", String.class)),
            ",", "%s %s (%s)", List.of("Prénom", "Nom", "Promotion (X09, ...)"));
    
    private final Set<PropertyName<? extends Object>> properties;
    private final Set<PropertyName<? extends Object>> multipleProperties;
    private final String separator;
    private final String nameFormat;
    private final List<String> nameProperties;
    private final Collection<String> allPropertiesHeaderNames;
    
    private PojoPersonConfiguration(Set<PropertyName<? extends Object>> properties, 
            Set<PropertyName<? extends Object>> multipleProperties, 
            String separator, String nameFormat,
            List<String> nameProperties){
        Set<String> tmpAllProperties = new HashSet<>();
        this.properties = properties;
        this.properties.forEach(p -> tmpAllProperties.add(p.getHeaderName()));
        this.multipleProperties = multipleProperties;
        this.multipleProperties.forEach(p -> tmpAllProperties.add(p.getHeaderName()));
        this.separator = separator;
        this.nameFormat = nameFormat;
        this.nameProperties = nameProperties;
        tmpAllProperties.addAll(nameProperties);
        allPropertiesHeaderNames = Collections.unmodifiableCollection(tmpAllProperties);
    }

    @Override
    public Set<PropertyName<? extends Object>> getPropertiesNames() {
        return properties;
    }

    @Override
    public Set<PropertyName<? extends Object>> getMultiplePropertiesNames() {
        return multipleProperties;
    }

    @Override
    public String getSeparator() {
        return separator;
    }

    @Override
    public String getNameFormat() {
        return nameFormat;
    }

    @Override
    public List<String> getNamePropertiesHeaderNames() {
        return nameProperties;
    }

    @Override
    public Collection<String> getAllPropertiesHeaderNames() {
        return allPropertiesHeaderNames;
    }
}
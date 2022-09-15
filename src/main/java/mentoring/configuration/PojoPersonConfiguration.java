package mentoring.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.MultiplePropertyName;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyName;

/**
 * Example person configurations for test cases.
 */
public enum PojoPersonConfiguration implements PersonConfiguration{
    /** Configuration used in simple test cases. */
    TEST_CONFIGURATION(Set.of(new PropertyName<>("Anglais", PropertyType.BOOLEAN), 
            new PropertyName<>("Promotion", PropertyType.INTEGER)), 
            Set.of(new SetPropertyName<>("Métiers","Activités et métiers", PropertyType.STRING), 
                    new SetPropertyName<>("Motivation", PropertyType.STRING)), 
            ";", "%s %s (X%s)", List.of("Prénom","Nom","Promotion")),
    /** Configuration used for real mentee data in the 2021 data set. */
    MENTEE_CONFIGURATION_REAL_DATA(Set.of(
            new PropertyName<>("Promotion","Promotion, cycle", PropertyType.INTEGER),
            new PropertyName<>("Maturité","maturité", PropertyType.INTEGER),
            new PropertyName<>("Email","E-mail", PropertyType.STRING)),
            Set.of(new SetPropertyName<>("Métiers","résumé métier secteur", PropertyType.STRING), 
                    new SetPropertyName<>("Langue","Option : langue préférentielle", PropertyType.STRING)),
            ",", "%s %s (X%s)", List.of("Prénom", "NOM", "Promotion, cycle")),
    /** Configuration used for real mentor data in the 2021 data set. */
    MENTOR_CONFIGURATION_REAL_DATA(Set.of(
            new PropertyName<>("Anglophone","anglophone", PropertyType.BOOLEAN),
            new PropertyName<>("Promotion","Promotion (X09, ...)", PropertyType.STRING),
            new PropertyName<>("Email","Adresse email", PropertyType.STRING)),
            Set.of(new SetPropertyName<>("Métiers","Résumé métier secteur", PropertyType.STRING)),
            ",", "%s %s (%s)", List.of("Prénom", "Nom", "Promotion (X09, ...)"));
    
    private final Set<PropertyName<?>> properties;
    private final Set<? extends MultiplePropertyName<?,?>> multipleProperties;
    private final String separator;
    private final String nameFormat;
    private final List<String> nameProperties;
    private final Collection<String> allPropertiesHeaderNames;
    
    private PojoPersonConfiguration(Set<PropertyName<?>> properties, 
            Set<? extends MultiplePropertyName<?,?>> multipleProperties, 
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
    public Set<PropertyName<?>> getPropertiesNames() {
        return properties;
    }

    @Override
    public Set<? extends MultiplePropertyName<?,?>> getMultiplePropertiesNames() {
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
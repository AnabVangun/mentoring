package mentoring.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.PropertyName;

public enum PojoPersonConfiguration implements PersonConfiguration{
    MENTEE_CONFIGURATION(Set.of(new PropertyName("Anglais")), 
            Set.of(new PropertyName("Promotion")), 
            Set.of(), 
            Set.of(new PropertyName("Métiers","Activités et métiers"), 
                    new PropertyName("Motivation")), 
            ";", "%s %s (X%s)", List.of("Prénom","Nom","Promotion")),
    MENTOR_CONFIGURATION(Set.of(new PropertyName("Anglais")), 
            Set.of(new PropertyName("Promotion")), 
            Set.of(),
            Set.of(new PropertyName("Métiers","Activités et métiers"), 
                    new PropertyName("Motivation")),
            ";", "%s %s (X%s)", List.of("Prénom","Nom","Promotion")),
    MENTEE_CONFIGURATION_REAL_DATA(Set.of(),
            Set.of(new PropertyName("Promotion","Promotion, cycle"),
                    new PropertyName("Maturité","maturité")), 
            Set.of(new PropertyName("Email","E-mail")),
            Set.of(new PropertyName("Métiers","résumé métier secteur"), 
                    new PropertyName("Langue","Option : langue préférentielle")),
            ",", "%s %s (X%s)", List.of("Prénom", "NOM", "Promotion, cycle")),
    MENTOR_CONFIGURATION_REAL_DATA(Set.of(new PropertyName("Anglophone","anglophone")),
            Set.of(), 
            Set.of(new PropertyName("Promotion","Promotion (X09, ...)"), 
                    new PropertyName("Email","Adresse email")),
            Set.of(new PropertyName("Métiers","Résumé métier secteur")),
            ",", "%s %s (%s)", List.of("Prénom", "Nom", "Promotion (X09, ...)"));
    
    private final Set<PropertyName> booleanProperties;
    private final Set<PropertyName> integerProperties;
    private final Set<PropertyName> stringProperties;
    private final Set<PropertyName> multipleStringProperties;
    private final String separator;
    private final String nameFormat;
    private final List<String> nameProperties;
    private final Collection<String> allPropertiesHeaderNames;
    
    private PojoPersonConfiguration(Set<PropertyName> booleanProperties, 
            Set<PropertyName> integerProperties, 
            Set<PropertyName> stringProperties,
            Set<PropertyName> multipleStringProperties, 
            String separator, String nameFormat,
            List<String> nameProperties){
        List<String> tmpAllProperties = new ArrayList<>();
        this.booleanProperties = booleanProperties;
        this.booleanProperties.forEach(p -> tmpAllProperties.add(p.getHeaderName()));
        this.integerProperties = integerProperties;
        this.integerProperties.forEach(p -> tmpAllProperties.add(p.getHeaderName()));
        this.stringProperties = stringProperties;
        this.stringProperties.forEach(p -> tmpAllProperties.add(p.getHeaderName()));
        this.multipleStringProperties = multipleStringProperties;
        this.multipleStringProperties.forEach(p -> tmpAllProperties.add(p.getHeaderName()));
        this.separator = separator;
        this.nameFormat = nameFormat;
        this.nameProperties = nameProperties;
        tmpAllProperties.addAll(nameProperties);
        allPropertiesHeaderNames = Collections.unmodifiableCollection(tmpAllProperties);
    }

    @Override
    public Set<PropertyName> getBooleanPropertiesNames() {
        return booleanProperties;
    }

    @Override
    public Set<PropertyName> getIntegerPropertiesNames() {
        return integerProperties;
    }

    @Override
    public Set<PropertyName> getStringPropertiesNames() {
        return stringProperties;
    }

    @Override
    public Set<PropertyName> getMultipleStringPropertiesNames() {
        return multipleStringProperties;
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
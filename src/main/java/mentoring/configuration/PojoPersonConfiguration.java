package mentoring.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.Property;

public enum PojoPersonConfiguration implements PersonConfiguration{
    MENTEE_CONFIGURATION(Set.of(new Property("Anglais")), 
            Set.of(new Property("Promotion")), 
            Set.of(), 
            Set.of(new Property("Métiers","Activités et métiers"), new Property("Motivation")), 
            ";", "%s %s (X%s)", List.of("Prénom","Nom","Promotion")),
    MENTOR_CONFIGURATION(Set.of(new Property("Anglais")), 
            Set.of(new Property("Promotion")), 
            Set.of(),
            Set.of(new Property("Métiers","Activités et métiers"), new Property("Motivation")),
            ";", "%s %s (X%s)", List.of("Prénom","Nom","Promotion")),
    MENTEE_CONFIGURATION_REAL_DATA(Set.of(),
            Set.of(new Property("Promotion","Promotion, cycle"),
                new Property("Maturité","maturité")), 
            Set.of(new Property("Email","E-mail")),
            Set.of(new Property("Métiers","résumé métier secteur"), 
                new Property("Langue","Option : langue préférentielle")),
            ",", "%s %s (X%s)", List.of("Prénom", "NOM", "Promotion, cycle")),
    MENTOR_CONFIGURATION_REAL_DATA(Set.of(new Property("Anglophone","anglophone")),
            Set.of(), 
            Set.of(new Property("Promotion","Promotion (X09, ...)"), 
                new Property("Email","Adresse email")),
            Set.of(new Property("Métiers","Résumé métier secteur")),
            ",", "%s %s (%s)", List.of("Prénom", "Nom", "Promotion (X09, ...)"));
    private final Set<Property> booleanProperties;
    private final Set<Property> integerProperties;
    private final Set<Property> stringProperties;
    private final Set<Property> multipleStringProperties;
    private final String separator;
    private final String nameFormat;
    private final List<String> nameProperties;
    private final Collection<String> allPropertiesHeaderNames;
    
    private PojoPersonConfiguration(Set<Property> booleanProperties, 
            Set<Property> integerProperties, 
            Set<Property> stringProperties,
            Set<Property> multipleStringProperties, 
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
    public Set<Property> getBooleanProperties() {
        return booleanProperties;
    }

    @Override
    public Set<Property> getIntegerProperties() {
        return integerProperties;
    }

    @Override
    public Set<Property> getStringProperties() {
        return stringProperties;
    }

    @Override
    public Set<Property> getMultipleStringProperties() {
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
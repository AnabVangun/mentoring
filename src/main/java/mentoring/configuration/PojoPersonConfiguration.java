package mentoring.configuration;

import java.util.List;
import java.util.Set;
import mentoring.datastructure.IndexedPropertyName;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyName;
import mentoring.datastructure.SimplePropertyName;

/**
 * Example person configurations for test cases.
 */
public enum PojoPersonConfiguration{
    /** Configuration used in simple test cases. */
    TEST_CONFIGURATION(new PersonConfiguration("Test configuration",
            Set.of(new SimplePropertyName<>("Anglais", "Anglais", PropertyType.BOOLEAN),
                    new SimplePropertyName<>("Promotion", "Promotion", PropertyType.INTEGER)), 
            Set.of(new SetPropertyName<>("Métiers","Activités et métiers", PropertyType.STRING), 
                    new SetPropertyName<>("Motivation", "Motivation", PropertyType.STRING)), 
            ";", "%s %s (X%s)", List.of("Prénom","Nom","Promotion"))),
    TEST_CONFIGURATION_2(new PersonConfiguration("Test configuration 2",
            Set.of(new SimplePropertyName<>("Name", "Nom", PropertyType.STRING),
                    new SimplePropertyName<>("Anglais", "Anglais", PropertyType.BOOLEAN),
                    new SimplePropertyName<>("Promotion", "Promotion", PropertyType.INTEGER)), 
            Set.of(new SetPropertyName<>("Métiers","Activités et métiers", PropertyType.STRING), 
                    new SetPropertyName<>("Motivation", "Motivation", PropertyType.STRING)), 
            ";", "%s %s (X%s)", List.of("Prénom","Nom","Promotion"))),
    /**Configuration used for real mentee data in the preprocessed 2023 data set. */
    MENTEE_CONFIGURATION_2024_DATA(new PersonConfiguration("Mentee configuration for 2024 data",
            Set.of(new SimplePropertyName<>("Email", "email", PropertyType.STRING),
                    new SimplePropertyName<>("Portable", "Portable", PropertyType.STRING),
                    new SimplePropertyName<>("Promotion", "Promotion et cycle (normalement X22)", 
                            PropertyType.YEAR),
                    new SimplePropertyName<>("Maturité", 
                            "Quel est le degré de maturité de ton projet académique et professionnel actuel ?", 
                            PropertyType.INTEGER),
                    new SimplePropertyName<>("Explication de maturité", "Peux-tu expliquer ta réponse ?",
                            PropertyType.STRING),
                    new SimplePropertyName<>("Echanges", 
                            "Tiens-tu absolument à pouvoir rencontrer ton mentor en face à face en région parisienne ?",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new SimplePropertyName<>("Autres secteurs ?",
                            "Si tu as répondu \"Autres\", quel secteur d'activité aurais-tu vouloir voir apparaître ?",
                            PropertyType.STRING),
                    new SimplePropertyName<>("Autres métiers ?",
                            "Si tu as répondu \"Autre\", quel métier aurais-tu vouloir voir apparaître ?",
                            PropertyType.STRING),
                    new SimplePropertyName<>("Attentes",
                            "Qu’attends-tu de cette relation mentorale et de ton mentor ?",
                            PropertyType.STRING)),
            Set.of(
                    new IndexedPropertyName<>("Secteur", 
                            "secteur",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyName<>("Métier", 
                            "metier",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyName<>("Entreprise", 
                            "entreprise",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new SetPropertyName<>("Langue", 
                            "Dans quelle(s) langue(s) souhaites-tu échanger ?", 
                            PropertyType.SIMPLIFIED_LOWER_STRING)),
            ";", "%s %s (%s)", List.of("Prénom", "Nom", "Promotion et cycle (normalement X22)"))),
    /**Configuration used for real mentor data in the preprocessed 2023 data set. */
    MENTOR_CONFIGURATION_2024_DATA(new PersonConfiguration("Mentor configuration for 2024 data",
            Set.of(new SimplePropertyName<>("Email", "email", PropertyType.STRING),
                    new SimplePropertyName<>("Portable", "Portable", PropertyType.STRING),
                    new SimplePropertyName<>("Motivation", "Pourquoi souhaites-tu être mentor ?", 
                            PropertyType.STRING),
                    new SimplePropertyName<>("Promotion", "Promotion et cycle (par exemple X11)", 
                            PropertyType.YEAR),
                    new SimplePropertyName<>("Maturité", 
                            "Tu préfères mentorer un étudiant dont le projet professionnel est :", 
                            PropertyType.INTEGER),
                    new SimplePropertyName<>("Autres secteurs ?",
                            "Si tu as répondu \"Autres\", quel secteur d'activité aurais-tu vouloir voir apparaître ?",
                            PropertyType.STRING),
                    new SimplePropertyName<>("Autres métiers ?",
                            "Si tu as répondu \"Autre\", quel métier aurais-tu vouloir voir apparaître ?",
                            PropertyType.STRING),
                    new SimplePropertyName<>("Echanges", 
                            "Tiens-tu absolument à pouvoir rencontrer ton mentoré en face à face en région parisienne ?",
                            PropertyType.SIMPLIFIED_LOWER_STRING)),
            Set.of(
                    new IndexedPropertyName<>("Secteur", 
                            "secteur",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyName<>("Métier", 
                            "metier",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyName<>("Entreprise", 
                            "entreprise",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new SetPropertyName<>("Langue", 
                            "Dans quelle(s) langue(s) acceptes-tu d'échanger ?", 
                            PropertyType.SIMPLIFIED_LOWER_STRING)),
            ";", "%s %s (%s)", List.of("Prénom", "Nom", "Promotion et cycle (par exemple X11)")));
    
    private final PersonConfiguration configuration;
    
    private PojoPersonConfiguration(PersonConfiguration configuration){
        this.configuration = configuration;
    }
    
    public PersonConfiguration getConfiguration(){
        return this.configuration;
    }
}
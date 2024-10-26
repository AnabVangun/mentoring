package mentoring.configuration;

import java.util.List;
import java.util.Set;
import mentoring.datastructure.IndexedPropertyDescription;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyDescription;
import mentoring.datastructure.SimplePropertyDescription;

/**
 * Example person configurations for test cases.
 */
public enum PojoPersonConfiguration{
    /** Configuration used in simple test cases. */
    TEST_CONFIGURATION(new PersonConfiguration("Test configuration",
            Set.of(new SimplePropertyDescription<>("Anglais", "Anglais", PropertyType.BOOLEAN),
                    new SimplePropertyDescription<>("Promotion", "Promotion", PropertyType.INTEGER)), 
            Set.of(new SetPropertyDescription<>("Métiers","Activités et métiers", PropertyType.STRING), 
                    new SetPropertyDescription<>("Motivation", "Motivation", PropertyType.STRING)), 
            ";", "%s %s (X%s)", List.of("Prénom","Nom","Promotion"))),
    TEST_CONFIGURATION_2(new PersonConfiguration("Test configuration 2",
            Set.of(new SimplePropertyDescription<>("Name", "Nom", PropertyType.STRING),
                    new SimplePropertyDescription<>("Anglais", "Anglais", PropertyType.BOOLEAN),
                    new SimplePropertyDescription<>("Promotion", "Promotion", PropertyType.INTEGER)), 
            Set.of(new SetPropertyDescription<>("Métiers","Activités et métiers", PropertyType.STRING), 
                    new SetPropertyDescription<>("Motivation", "Motivation", PropertyType.STRING)), 
            ";", "%s %s (X%s)", List.of("Prénom","Nom","Promotion"))),
    /**Configuration used for real mentee data in the preprocessed 2023 data set. */
    MENTEE_CONFIGURATION_2024_DATA(new PersonConfiguration("Mentee configuration for 2024 data",
            Set.of(new SimplePropertyDescription<>("Email", "email", PropertyType.STRING),
                    new SimplePropertyDescription<>("Portable", "Portable", PropertyType.STRING),
                    new SimplePropertyDescription<>("Promotion", "Promotion et cycle (normalement X22)", 
                            PropertyType.YEAR),
                    new SimplePropertyDescription<>("Maturité", 
                            "Quel est le degré de maturité de ton projet académique et professionnel actuel ?", 
                            PropertyType.INTEGER),
                    new SimplePropertyDescription<>("Explication de maturité", "Peux-tu expliquer ta réponse ?",
                            PropertyType.STRING),
                    new SimplePropertyDescription<>("Echanges", 
                            "Tiens-tu absolument à pouvoir rencontrer ton mentor en face à face en région parisienne ?",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new SimplePropertyDescription<>("Autres secteurs ?",
                            "Si tu as répondu \"Autres\", quel secteur d'activité aurais-tu vouloir voir apparaître ?",
                            PropertyType.STRING),
                    new SimplePropertyDescription<>("Autres métiers ?",
                            "Si tu as répondu \"Autre\", quel métier aurais-tu vouloir voir apparaître ?",
                            PropertyType.STRING),
                    new SimplePropertyDescription<>("Attentes",
                            "Qu'attends-tu de cette relation mentorale et de ton mentor ?",
                            PropertyType.STRING)),
            Set.of(new IndexedPropertyDescription<>("Secteur", 
                            "secteur",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyDescription<>("Métier", 
                            "metier",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyDescription<>("Entreprise", 
                            "entreprise",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new SetPropertyDescription<>("Langue", 
                            "Dans quelle(s) langue(s) souhaites-tu échanger ?", 
                            PropertyType.SIMPLIFIED_LOWER_STRING)),
            ";", "%s %s (%s)", List.of("Prénom", "Nom", "Promotion et cycle (normalement X22)"))),
    /**Configuration used for real mentor data in the preprocessed 2023 data set. */
    MENTOR_CONFIGURATION_2024_DATA(new PersonConfiguration("Mentor configuration for 2024 data",
            Set.of(new SimplePropertyDescription<>("Email", "email", PropertyType.STRING),
                    new SimplePropertyDescription<>("Portable", "Portable", PropertyType.STRING),
                    new SimplePropertyDescription<>("Motivation", "Pourquoi souhaites-tu être mentor ?", 
                            PropertyType.STRING),
                    new SimplePropertyDescription<>("Promotion", "Promotion et cycle (par exemple X11)", 
                            PropertyType.YEAR),
                    new SimplePropertyDescription<>("Maturité", 
                            "Tu préfères mentorer un étudiant dont le projet professionnel est :", 
                            PropertyType.INTEGER),
                    new SimplePropertyDescription<>("Autres secteurs ?",
                            "Si tu as répondu \"Autres\", quel secteur d'activité aurais-tu vouloir voir apparaître ?",
                            PropertyType.STRING),
                    new SimplePropertyDescription<>("Autres métiers ?",
                            "Si tu as répondu \"Autre\", quel métier aurais-tu vouloir voir apparaître ?",
                            PropertyType.STRING),
                    new SimplePropertyDescription<>("Echanges", 
                            "Tiens-tu absolument à pouvoir rencontrer ton mentoré en face à face en région parisienne ?",
                            PropertyType.SIMPLIFIED_LOWER_STRING)),
            Set.of(new IndexedPropertyDescription<>("Secteur", 
                            "secteur",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyDescription<>("Métier", 
                            "metier",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new IndexedPropertyDescription<>("Entreprise", 
                            "entreprise",
                            PropertyType.SIMPLIFIED_LOWER_STRING),
                    new SetPropertyDescription<>("Langue", 
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
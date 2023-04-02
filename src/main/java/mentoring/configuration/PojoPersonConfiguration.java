package mentoring.configuration;

import java.util.List;
import java.util.Set;
import mentoring.datastructure.IndexedPropertyName;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyName;

/**
 * Example person configurations for test cases.
 */
public enum PojoPersonConfiguration{
    /** Configuration used in simple test cases. */
    TEST_CONFIGURATION(new PersonConfiguration("Test configuration",
            Set.of(new PropertyName<>("Anglais", "Anglais", PropertyType.BOOLEAN),
                    new PropertyName<>("Promotion", "Promotion", PropertyType.INTEGER)), 
            Set.of(new SetPropertyName<>("Métiers","Activités et métiers", PropertyType.STRING), 
                    new SetPropertyName<>("Motivation", "Motivation", PropertyType.STRING)), 
            ";", "%s %s (X%s)", List.of("Prénom","Nom","Promotion"))),
    /**Configuration used for real mentee data in the preprocessed 2022 data set. */
    MENTEE_CONFIGURATION_2023_DATA(new PersonConfiguration("Mentee configuration for 2023 data",
            Set.of(new PropertyName<>("Email", "email", PropertyType.STRING),
                    new PropertyName<>("Promotion", "Promotion et cycle (par exemple X20)", 
                            PropertyType.YEAR),
                    new PropertyName<>("Maturité", 
                            "Quel est le degré de maturité de ton projet académique et professionnel actuel ?", 
                            PropertyType.INTEGER),
                    new PropertyName<>("Echanges", 
                            "Tiens-tu absolument à pouvoir rencontrer ton mentor en face à face en région parisienne ?",
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
                            "Dans quelle(s) langue(s) souhaites-tu échanger ?", 
                            PropertyType.SIMPLIFIED_LOWER_STRING)),
            ";", "%s %s (%s)", List.of("Prénom", "Nom", "Promotion et cycle (par exemple X20)"))),
    /**Configuration used for real mentor data in the preprocessed 2022 data set. */
    MENTOR_CONFIGURATION_2023_DATA(new PersonConfiguration("Mentor configuration for 2023 data",
            Set.of(new PropertyName<>("Email", "email", PropertyType.STRING),
                    new PropertyName<>("Promotion", "Promotion et cycle (par exemple X11)", 
                            PropertyType.YEAR),
                    new PropertyName<>("Maturité", 
                            "Tu préfères mentorer un étudiant dont le projet professionnel est :", 
                            PropertyType.INTEGER),
                    new PropertyName<>("Echanges", 
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
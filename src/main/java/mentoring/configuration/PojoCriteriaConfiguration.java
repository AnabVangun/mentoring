package mentoring.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mentoring.datastructure.Person;
import mentoring.match.ProgressiveCriterion;
import mentoring.match.NecessaryCriterion;

/**
 * Example criteria configurations for test cases.
 */
public enum PojoCriteriaConfiguration implements CriteriaConfiguration<Person,Person>{    
    //TODO: just like PropertyType, replace enum with class and private constructor
    /** Configuration used in simple test cases. */
    CRITERIA_CONFIGURATION(List.of(
            (mentee, mentor) ->
                    Constants.YEAR_WEIGHT * (Constants.MENTEE_YEAR
                            - mentor.getPropertyAs(Constants.YEAR_PROPERTY, Integer.class)),
            (mentee, mentor) -> {
                Set<String> menteeActivities = mentee
                        .getPropertyAsSetOf(Constants.ACTIVITIES_PROPERTY, String.class);
                Set<String> mentorActivities = mentor
                        .getPropertyAsSetOf(Constants.ACTIVITIES_PROPERTY, String.class);
                return CriteriaToolbox.computeSetDistance(menteeActivities, mentorActivities);
            }, (mentee, mentor) -> {
                Set<String> menteeMotivation =
                        mentee.getPropertyAsSetOf(Constants.MOTIVATION_PROPERTY, String.class);
                Set<String> mentorMotivation =
                        mentor.getPropertyAsSetOf(Constants.MOTIVATION_PROPERTY, String.class);
                return CriteriaToolbox.computeSetDistance(menteeMotivation, mentorMotivation);
            }), List.of((mentee, mentor) -> CriteriaToolbox.logicalNotAOrB(
                    mentee.getPropertyAs(Constants.ENGLISH_PROPERTY, Boolean.class),
                    mentor.getPropertyAs(Constants.ENGLISH_PROPERTY, Boolean.class)))),
    /** Configuration used for real 2021 data. */
    CRITERIA_CONFIGURATION_REAL_DATA(            
        List.of((mentee, mentor) -> {
            return Constants.YEAR_WEIGHT 
                * (mentee.getPropertyAs(Constants.YEAR_PROPERTY, Integer.class) 
                    - CriteriaToolbox.getYear(mentor.getPropertyAs(Constants.YEAR_PROPERTY, 
                            String.class)));
            },
            (mentee, mentor) -> {
                Set<String> menteeActivities = 
                        mentee.getPropertyAsSetOf(Constants.ACTIVITIES_PROPERTY, String.class);
                Set<String> mentorActivities = 
                        mentor.getPropertyAsSetOf(Constants.ACTIVITIES_PROPERTY, String.class);
                return CriteriaToolbox.computeSetDistance(menteeActivities, mentorActivities);
        }),
        List.of((mentee, mentor) -> {
            return (mentee.getPropertyAsSetOf(Constants.LANGUAGES_PROPERTY, String.class)
                    .contains("Français")
                    || mentor.getPropertyAs(Constants.ENGLISH_SPEAKING_PROPERTY, Boolean.class));
        })),
    /** Configuration used for the preprocessed 2022 data set. */
    CRITERIA_CONFIGURATION_2023_DATA(
            List.of(
                    (mentee, mentor) -> CriteriaToolbox.exponentialDistance(Constants.MEETING_INDICES, 
                            mentee.getPropertyAs(Constants.MEETING2023, String.class), 
                            mentor.getPropertyAs(Constants.MEETING2023, String.class), 
                            Constants.MEETING_WEIGHT),
                    (mentee, mentor ) -> 
                        Constants.YEAR_WEIGHT * Math.abs(
                                CriteriaToolbox.getYear(mentee.getPropertyAs(
                                        Constants.YEAR_PROPERTY, String.class)) - 10 
                                - CriteriaToolbox.getYear(mentor.getPropertyAs(
                                        Constants.YEAR_PROPERTY, String.class))), 
                    (mentee, mentor) -> Constants.MATURITY_WEIGHT * 
                            Math.abs(mentee.getPropertyAs(Constants.MATURITY2023, Integer.class)
                                    - mentor.getPropertyAs(Constants.MATURITY2023, Integer.class)), 
                    (mentee, mentor) -> Constants.INTEREST_AMPLIFIER2023
                            * CriteriaToolbox.computeWeightedAsymetricMapDistance(
                            mentee.getPropertyAsMapOf(Constants.SECTOR2023, String.class, 
                                    Integer.class), 
                            mentor.getPropertyAsSetOf(Constants.SECTOR2023, String.class),
                            mentee.getPropertyAs(Constants.MATURITY2023, Integer.class)
                                    / Constants.MATURITE_MAX_2023), 
                    (mentee, mentor) -> Constants.INTEREST_AMPLIFIER2023
                            * CriteriaToolbox.computeWeightedAsymetricMapDistance(
                            mentee.getPropertyAsMapOf(Constants.JOB2023, String.class, 
                                    Integer.class), 
                            mentor.getPropertyAsSetOf(Constants.JOB2023, String.class),
                            mentee.getPropertyAs(Constants.MATURITY2023, Integer.class)
                                    / Constants.MATURITE_MAX_2023), 
                    (mentee, mentor) -> Constants.INTEREST_AMPLIFIER2023
                            * CriteriaToolbox.computeWeightedAsymetricMapDistance(
                            mentee.getPropertyAsMapOf(Constants.COMPANY2023, String.class, 
                                    Integer.class), 
                            mentor.getPropertyAsSetOf(Constants.COMPANY2023, String.class),
                            mentee.getPropertyAs(Constants.MATURITY2023, Integer.class)
                                    / Constants.MATURITE_MAX_2023)
            ),
            List.of((mentee, mentor) -> {
                boolean found = false;
                Set<String> mentorLanguages = mentor.getPropertyAsSetOf(Constants.LANGUAGES_PROPERTY, 
                        String.class);
                for (String language : mentee.getPropertyAsSetOf(Constants.LANGUAGES_PROPERTY, 
                        String.class)){
                    if (mentorLanguages.contains(language)){
                        found = true;
                        break;
                    }
                }
                return found;
            }));
    
    private static class Constants {
        private static final String LANGUAGES_PROPERTY = "Langue";
        private static final String ENGLISH_PROPERTY = "Anglais";
        private static final String ENGLISH_SPEAKING_PROPERTY = "Anglophone";
        private static final int YEAR_WEIGHT = 10;
        private static final int MENTEE_YEAR = 2020;
        private static final String YEAR_PROPERTY = "Promotion";
        private static final String ACTIVITIES_PROPERTY = "Métiers";
        private static final String MOTIVATION_PROPERTY = "Motivation";
        private static final int MATURITE_MAX_2023 = 5;
        private static final String SECTOR2023 = "Secteur";
        private static final String MATURITY2023 = "Maturité";
        private static final String JOB2023 = "Métier";
        private static final String COMPANY2023 = "Entreprise";
        private static final int MATURITY_WEIGHT = 50;
        private static final Map<String, Integer> MEETING_INDICES = Map.of("oui",0,"sipossible",1,
                "pasnecessairement",2);
        private static final String MEETING2023 = "Echanges";
        private static final int MEETING_WEIGHT = 150;
        private static final int INTEREST_AMPLIFIER2023 = 3;
    }
    private final Collection<ProgressiveCriterion<Person, Person>> progressiveCriteria;
    private final List<NecessaryCriterion<Person, Person>> necessaryCriteria;
    
    @Override
    public Collection<ProgressiveCriterion<Person, Person>> getProgressiveCriteria(){
        return Collections.unmodifiableCollection(progressiveCriteria);
    }
    
    @Override
    public List<NecessaryCriterion<Person, Person>> getNecessaryCriteria(){
        return Collections.unmodifiableList(necessaryCriteria);
    }
    
    private PojoCriteriaConfiguration(
        Collection<ProgressiveCriterion<Person, Person>> progressiveCriteria,
        List<NecessaryCriterion<Person, Person>> necessaryCriteria){
        this.progressiveCriteria = progressiveCriteria;
        this.necessaryCriteria = necessaryCriteria;
    }
}

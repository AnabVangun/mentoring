package mentoring.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.Person;
import mentoring.match.ProgressiveCriterion;
import mentoring.match.NecessaryCriterion;

/**
 * Example configurations for test cases.
 */
public enum PojoCriteriaConfiguration implements CriteriaConfiguration<Person,Person>{    
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
        })
    );
    
    private static class Constants {
        private static final String LANGUAGES_PROPERTY = "Langue";
        private static final String ENGLISH_PROPERTY = "Anglais";
        private static final String ENGLISH_SPEAKING_PROPERTY = "Anglophone";
        private static final int YEAR_WEIGHT = 10;
        private static final int MENTEE_YEAR = 2020;
        private static final String YEAR_PROPERTY = "Promotion";
        private static final String ACTIVITIES_PROPERTY = "Métiers";
        private static final String MOTIVATION_PROPERTY = "Motivation";
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

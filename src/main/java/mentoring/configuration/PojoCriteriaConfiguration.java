package mentoring.configuration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import mentoring.datastructure.Person;
import mentoring.match.ProgressiveCriterion;
import mentoring.match.NecessaryCriterion;

public enum PojoCriteriaConfiguration implements CriteriaConfiguration<Person,Person>{
    CRITERIA_CONFIGURATION(List.of(
        (mentee, mentor) ->
            Constants.YEAR_WEIGHT * 
                (Constants.MENTEE_YEAR 
                        - mentor.getPropertyAs(Constants.MENTOR_YEAR_HEADER, Integer.class)),
        (mentee, mentor) -> {
            Set<String> menteeActivities = mentee
                    .getPropertyAsSetOf(Constants.MENTEE_ACTIVITIES_HEADER, String.class);
            Set<String> mentorActivities = mentor
                    .getPropertyAsSetOf(Constants.MENTOR_ACTIVITIES_HEADER, String.class);
            return CriteriaToolbox.computeSetProximity(menteeActivities, mentorActivities);
        },
        (mentee, mentor) -> {
            Set<String> menteeMotivation = 
                    mentee.getPropertyAsSetOf(Constants.MENTEE_MOTIVATION_HEADER, String.class);
            Set<String> mentorMotivation = 
                    mentor.getPropertyAsSetOf(Constants.MENTOR_MOTIVATION_HEADER, String.class);
            return CriteriaToolbox.computeSetProximity(menteeMotivation, mentorMotivation);
        }), 
    List.of((mentee, mentor) -> CriteriaToolbox.logicalNotAOrB(
            mentee.getPropertyAs(Constants.MENTEE_ENGLISH_HEADER, Boolean.class), 
            mentor.getPropertyAs(Constants.MENTOR_ENGLISH_HEADER, Boolean.class)))),
    CRITERIA_CONFIGURATION_REAL_DATA(            
        List.of((mentee, mentor) -> {
            //age criteria
            return Constants.YEAR_WEIGHT 
                * (mentee.getPropertyAs("Promotion", Integer.class) 
                    - CriteriaToolbox.getYear(mentor.getPropertyAs("Promotion", String.class)));
            },
            (mentee, mentor) -> {
                Set<String> menteeActivities = mentee.getPropertyAsSetOf("Métiers", String.class);
                Set<String> mentorActivities = mentor.getPropertyAsSetOf("Métiers", String.class);
                return CriteriaToolbox.computeSetProximity(menteeActivities, mentorActivities);
        }),
        List.of((mentee, mentor) -> {
            return (mentee.getPropertyAsSetOf("Langue", String.class).contains("Français")
                    || mentor.getPropertyAs("Anglophone", Boolean.class));
        })
    );
    
    private static class Constants {
        private static final String MENTEE_ENGLISH_HEADER = "Anglais";
        private static final String MENTOR_ENGLISH_HEADER = "Anglais";
        private static final int YEAR_WEIGHT = 10;
        private static final int MENTEE_YEAR = 2020;
        private static final String MENTOR_YEAR_HEADER = "Promotion";
        private static final String MENTEE_ACTIVITIES_HEADER = "Métiers";
        private static final String MENTOR_ACTIVITIES_HEADER = "Métiers";
        private static final String MENTEE_MOTIVATION_HEADER = "Motivation";
        private static final String MENTOR_MOTIVATION_HEADER = "Motivation";
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

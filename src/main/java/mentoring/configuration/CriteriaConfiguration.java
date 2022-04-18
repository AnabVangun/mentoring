package mentoring.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import mentoring.io.Person;
import mentoring.match.ProgressiveCriterion;
import mentoring.match.NecessaryCriterion;

public class CriteriaConfiguration {
    /*
    TODO improve
    Conditions required:
    1. Improve structure of CSV file
    2. Add PersonParser to have property nicely parsed inside Person
    Example:
    1. Check if mentee requires an English-speaking mentor as a boolean
    */
    private Collection<ProgressiveCriterion<Person, Person>> progressiveCriteria;
    private List<NecessaryCriterion<Person, Person>> necessaryCriteria;
    private static final String MENTEE_ENGLISH_HEADER = "Anglais";
    private static final String MENTOR_ENGLISH_HEADER = "Anglais";
    private static final int YEAR_WEIGHT = 10;
    private static final int MENTEE_YEAR = 2020;
    private static final String MENTOR_YEAR_HEADER = "Promotion";
    private static final String MENTEE_ACTIVITIES_HEADER = "Activités et métiers";
    private static final String MENTOR_ACTIVITIES_HEADER = "Activités et métiers";
    private static final String MENTEE_MOTIVATION_HEADER = "Motivation";
    private static final String MENTOR_MOTIVATION_HEADER = "Motivation";
    
    public Collection<ProgressiveCriterion<Person, Person>> getProgressiveCriteria(){
        return Collections.unmodifiableCollection(progressiveCriteria);
    }
    
    public List<NecessaryCriterion<Person, Person>> getNecessaryCriteria(){
        return Collections.unmodifiableList(necessaryCriteria);
    }
    
    public CriteriaConfiguration(){
        this.progressiveCriteria = new ArrayList<>();
        this.necessaryCriteria = new ArrayList<>();
        this.necessaryCriteria.add((mentee, mentor) -> {
            return BaseCriteria.logicalNotAOrB(
                mentee.getProperty(MENTEE_ENGLISH_HEADER).size() > 0
                    && Boolean.parseBoolean(
                        mentee.getProperty(MENTEE_ENGLISH_HEADER).iterator().next()), 
                mentor.getProperty(MENTOR_ENGLISH_HEADER).size() > 0
                    && Boolean.parseBoolean(
                        mentor.getProperty(MENTOR_ENGLISH_HEADER).iterator().next()));
        });
        this.progressiveCriteria.add((mentee, mentor) -> {
            return YEAR_WEIGHT * (MENTEE_YEAR - 
                    Integer.parseInt(mentor.getProperty(MENTOR_YEAR_HEADER).iterator().next()));
        });
        this.progressiveCriteria.add((mentee, mentor) -> {
           Set<String> menteeActivities = mentee.getPropertyAsSet(MENTEE_ACTIVITIES_HEADER, ";");
           Set<String> mentorActivities = mentor.getPropertyAsSet(MENTOR_ACTIVITIES_HEADER, ";");
           return BaseCriteria.computeSetProximity(menteeActivities, mentorActivities);
        });
        this.progressiveCriteria.add((mentee, mentor) -> {
           Set<String> menteeMotivation = mentee.getPropertyAsSet(MENTEE_MOTIVATION_HEADER, ";");
           Set<String> mentorMotivation = mentor.getPropertyAsSet(MENTOR_MOTIVATION_HEADER, ";");
           return BaseCriteria.computeSetProximity(menteeMotivation, mentorMotivation);
        });
    }
    
    
}

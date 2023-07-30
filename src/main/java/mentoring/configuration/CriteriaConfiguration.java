package mentoring.configuration;

import java.util.Collection;
import java.util.List;
import mentoring.match.NecessaryCriterion;
import mentoring.match.ProgressiveCriterion;

public abstract class CriteriaConfiguration<Mentee,Mentor> 
        extends Configuration<CriteriaConfiguration<Mentee,Mentor>>{
    
    protected CriteriaConfiguration(String configurationName){
        super(configurationName);
    }
    
    public abstract Collection<ProgressiveCriterion<Mentee, Mentor>> getProgressiveCriteria();
    
    public abstract List<NecessaryCriterion<Mentee, Mentor>> getNecessaryCriteria();
}

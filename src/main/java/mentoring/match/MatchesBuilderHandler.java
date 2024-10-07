package mentoring.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
//TODO refactor: here, mentoring.match relies on mentoring.configuration and vice-versa.
import mentoring.configuration.CriteriaConfiguration;

/**
 * Handler forging and providing {@link MatchesBuilder} instances. 
 * The handler does not actually run the {@link Future} objects it receives, it only waits for their
 * result.
 * 
 * <p>MatchesBuilderHandler is thread-safe: it uses internal synchronisation to make sure that
 * the MatchesBuilder instance returned is consistent with the previous setter calls.
 * @param <Mentee> class representing an individual mentee
 * @param <Mentor> class representing an individual mentor
 */
public class MatchesBuilderHandler<Mentee, Mentor> {
    private final SupplierList suppliers = new SupplierList();
    private MatchesBuilder<Mentee, Mentor> lastBuilder;
    /**
     * Instantiates a MatchesBuilderHandler instance.
     */
    public MatchesBuilderHandler(){}
    
    public ForbiddenMatches<Mentee, Mentor> getForbiddenMatches(){
        return suppliers.forbiddenMatches;
    }
    
    /**
     * Forges a MatchesBuilder if necessary and returns it.
     * @return a MatchesBuilder consistent with the latest calls to this instance's setters.
     * @throws IllegalStateException if some mandatory setters have not been called before calling
     * this method.
     * @throws java.lang.InterruptedException if the execution of a supplier is interrupted
     * @throws java.util.concurrent.ExecutionException if the computation throws an exception
     */
    public MatchesBuilder<Mentee, Mentor> get() throws IllegalStateException, InterruptedException,
            ExecutionException{
        synchronized(this){
            if (!suppliers.hasChanged){
                return lastBuilder;
            }
        }
        SupplierList synchronizedSuppliers = suppliers.atomicCopy();
        MatchesBuilder<Mentee, Mentor> builder;
        try {
            ParametersList parameters = new ParametersList(synchronizedSuppliers);
            builder = new MatchesBuilder<>(
                    parameters.mentees, parameters.mentors,
                    parameters.criteria.getProgressiveCriteria());
            if (parameters.criteria.getNecessaryCriteria() != null){
                builder.withNecessaryCriteria(parameters.criteria.getNecessaryCriteria());
            }
            if (parameters.placeholderMentee != null 
                    && parameters.placeholderMentor != null){
                builder.withPlaceholderPersons(parameters.placeholderMentee, 
                        parameters.placeholderMentor);
            }
            builder.withForbiddenMatches(parameters.forbiddenMatches);
        } catch (InterruptedException ex) {
            throw new InterruptedException();
        }
        synchronized(this){
            lastBuilder = builder;
        }
        return builder;
    }
    
    /**
     * Provides a supplier for mentees for the next call to {@link #get()}.
     * Only the last call to this method is kept: each call overrides the previous one. 
     * This setter is mandatory.
     * @param menteesSupplier list of mentees encapsulated in a Future container
     */
    public synchronized void setMenteesSupplier(Future<List<Mentee>> menteesSupplier){
        suppliers.menteesSupplier = Objects.requireNonNull(menteesSupplier, 
                "mentees supplier cannot be null");
        suppliers.hasChanged = true;
        /*TODO coordinate: it is likely that setMenteesSupplier and SetMentorsSupplier will be both
        called, no need to create two ForbiddenMatches each time*/
        //FIXME forbiddenMatches should be cleared rather than replaced
        suppliers.forbiddenMatches = new ForbiddenMatches<>();
    }
    
    /**
     * Provides a supplier for mentors for the next call to {@link #get()}.
     * Only the last call to this method is kept: each call overrides the previous one. 
     * This setter is mandatory.
     * @param mentorsSupplier list of mentors encapsulated in a Future container
     */
    public synchronized void setMentorsSupplier(Future<List<Mentor>> mentorsSupplier){
        suppliers.mentorsSupplier = Objects.requireNonNull(mentorsSupplier,
                "mentors supplier cannot be null");
        suppliers.hasChanged = true;
        suppliers.forbiddenMatches = new ForbiddenMatches<>();
    }
    
    /**
     * Provides a supplier for criteria for the next call to {@link #get()}.
     * Only the last call to this method is kept: each call overrides the previous one.
     * This setter is mandatory.
     * @param criteriaSupplier criteria encapsulated in a 
     *      Future container
     */
    public synchronized void setCriteriaSupplier(
            Future<CriteriaConfiguration<Mentee, Mentor>> criteriaSupplier){
        suppliers.criteriaSupplier = Objects.requireNonNull(criteriaSupplier,
                "criteria supplier cannot be null");
        suppliers.hasChanged = true;
    }
    
    /**
     * Provides a supplier for default mentee and mentor for the next call to {@link #get()}.
     * Only the last call to this method is kept: each call overrides the previous one.
     * This setter is not mandatory.
     * @param defaultMentee placeholder mentee encapsulated in a Future container
     * @param defaultMentor placeholder mentor encapsulated in a Future container
     */
    public synchronized void setPlaceholderPersonsSupplier(Future<Mentee> defaultMentee, 
            Future<Mentor> defaultMentor){
        if(defaultMentee == null ^ defaultMentor == null){
            throw new IllegalArgumentException("""
                Default mentee and default mentor must be both 
                null or non-null, not one of each. Received """ + defaultMentee + " and " 
                    + defaultMentor);
        }
        suppliers.placeholderMenteeSupplier = defaultMentee;
        suppliers.placeholderMentorSupplier = defaultMentor;
        suppliers.hasChanged = true;
    }
    
    private class SupplierList {
        Future<List<Mentee>> menteesSupplier;
        Future<List<Mentor>> mentorsSupplier;
        Future<CriteriaConfiguration<Mentee, Mentor>> criteriaSupplier;
        Future<Mentee> placeholderMenteeSupplier;
        Future<Mentor> placeholderMentorSupplier;
        ForbiddenMatches<Mentee, Mentor> forbiddenMatches = new ForbiddenMatches<>();
        private boolean hasChanged = true;
        
        SupplierList(){}
        
        SupplierList atomicCopy(){
            SupplierList result = new SupplierList();
            synchronized(MatchesBuilderHandler.this){
                result.menteesSupplier = menteesSupplier;
                result.mentorsSupplier = mentorsSupplier;
                result.criteriaSupplier = criteriaSupplier;
                result.placeholderMenteeSupplier = placeholderMenteeSupplier;
                result.placeholderMentorSupplier = placeholderMentorSupplier;
                result.forbiddenMatches = forbiddenMatches;
                result.hasChanged = hasChanged;
                this.hasChanged = false;
            }
            return result;
        }
    }
    
    private class ParametersList {
        List<Mentee> mentees;
        List<Mentor> mentors;
        CriteriaConfiguration<Mentee, Mentor> criteria;
        Mentee placeholderMentee;
        Mentor placeholderMentor;
        ForbiddenMatches<Mentee, Mentor> forbiddenMatches;
        
        ParametersList(SupplierList suppliers) throws IllegalStateException, InterruptedException,
                ExecutionException {
            verifyMandatorySuppliers(suppliers);
            mentees = suppliers.menteesSupplier.get();
            mentors = suppliers.mentorsSupplier.get();
            criteria = suppliers.criteriaSupplier.get();
            if(suppliers.placeholderMenteeSupplier != null){
                placeholderMentee = suppliers.placeholderMenteeSupplier.get();
            }
            if(suppliers.placeholderMentorSupplier != null){
                placeholderMentor = suppliers.placeholderMentorSupplier.get();
            }
            forbiddenMatches = suppliers.forbiddenMatches;
        }
    
        private void verifyMandatorySuppliers(SupplierList suppliers) throws IllegalStateException {
            if(suppliers.menteesSupplier == null
                    || suppliers.mentorsSupplier == null 
                    || suppliers.criteriaSupplier == null){
                throw new IllegalStateException(
                        forgeMissingMandatorySettersErrorMessage(
                                suppliers.menteesSupplier,
                                suppliers.mentorsSupplier, 
                                suppliers.criteriaSupplier));
            }
        }
        
        private String forgeMissingMandatorySettersErrorMessage(Future<List<Mentee>> menteesSupplier,
                Future<List<Mentor>> mentorsSupplier,
                Future<CriteriaConfiguration<Mentee, Mentor>> criteriaSupplier) {
            List<String> missingFields = new ArrayList<>(3);
            addFieldLabelIfNull(menteesSupplier, "mentees supplier", missingFields);
            addFieldLabelIfNull(mentorsSupplier, "mentors supplier", missingFields);
            addFieldLabelIfNull(criteriaSupplier, "progressive criteria supplier", missingFields);
            return "Some mandatory setters are missing: " + missingFields;
        }

        private void addFieldLabelIfNull(Object field, String label, List<String> list){
            if (field == null){
                list.add(label);
            }
        }
    }
}

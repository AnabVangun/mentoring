package mentoring.match;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
    private final Map<Mentee,Set<Mentor>> forbiddenMatches = new HashMap<>();
    //TODO refactor to extract allowMatch and forbidMatch and share them with builder
    //TODO optimise: when #get() is called several times without changing the parameters, do not
    //recompute result
    /**
     * Instantiates a MatchesBuilderHandler instance.
     */
    public MatchesBuilderHandler(){}
    
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
        SupplierList synchronizedSuppliers = suppliers.atomicCopy();
        MatchesBuilder<Mentee, Mentor> builder;
        try {
            ParametersList parameters = new ParametersList(synchronizedSuppliers);
            builder = new MatchesBuilder<>(
                    parameters.mentees, parameters.mentors,
                    parameters.progressiveCriteria);
            if (parameters.necessaryCriteria != null){
                builder.withNecessaryCriteria(parameters.necessaryCriteria);
            }
            if (parameters.placeholderMentee != null 
                    && parameters.placeholderMentor != null){
                builder.withPlaceholderPersons(parameters.placeholderMentee, 
                        parameters.placeholderMentor);
            }
        } catch (InterruptedException ex) {
            throw new InterruptedException();
        }
        synchronized(this){
            /*
            FIXME: race condition, forbiddenMatches may have been cleared by concurrent calls to 
            setMenteesSupplier or setMentorsSupplier. A copy should be made in the first atomic copy.
            */
            for (Entry<Mentee, Set<Mentor>> entry: forbiddenMatches.entrySet()){
                Mentee mentee = entry.getKey();
                for (Mentor mentor : entry.getValue()){
                    builder.forbidMatch(mentee, mentor);
                }
            }
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
        forbiddenMatches.clear();
        suppliers.menteesSupplier = Objects.requireNonNull(menteesSupplier, 
                "mentees supplier cannot be null");
    }
    
    /**
     * Provides a supplier for mentors for the next call to {@link #get()}.
     * Only the last call to this method is kept: each call overrides the previous one. 
     * This setter is mandatory.
     * @param mentorsSupplier list of mentors encapsulated in a Future container
     */
    public synchronized void setMentorsSupplier(Future<List<Mentor>> mentorsSupplier){
        forbiddenMatches.clear();
        suppliers.mentorsSupplier = Objects.requireNonNull(mentorsSupplier,
                "mentors supplier cannot be null");
    }
    
    /**
     * Provides a supplier for progressive criteria for the next call to {@link #get()}.
     * Only the last call to this method is kept: each call overrides the previous one.
     * This setter is mandatory.
     * @param progressiveCriteriaSupplier collection of progressive criteria encapsulated in a 
     *      Future container
     */
    public synchronized void setProgressiveCriteriaSupplier(
            Future<Collection<ProgressiveCriterion<Mentee, Mentor>>> progressiveCriteriaSupplier){
        suppliers.progressiveCriteriaSupplier = Objects.requireNonNull(progressiveCriteriaSupplier,
                "progressive criteria supplier cannot be null");
    }
    
    /**
     * Provides a supplier for necessary criteria for the next call to {@link #get()}.
     * Only the last call to this method is kept: each call overrides the previous one.
     * This setter is not mandatory.
     * @param necessaryCriteriaSupplier collection of necessary criteria encapsulated in a 
     *      Future container
     */
    public synchronized void setNecessaryCriteriaSupplier(
            Future<Collection<NecessaryCriterion<Mentee, Mentor>>> necessaryCriteriaSupplier){
        suppliers.necessaryCriteriaSupplier = necessaryCriteriaSupplier;
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
    }
    
    /**
     * Forbids matches between the input mentee and mentor.
     * All successive calls to this method are forwarded to all successive instances of 
     * {@link MatchesBuilder} that use the same mentees and mentors as the one available when the
     * call to this method was made. When the mentees and/or mentors change, the list of 
     * forbidden matches is cleared.
     * @param mentee that cannot be matched with the mentor
     * @param mentor that cannot be matched with the mentee
     */
    public synchronized void forbidMatch(Mentee mentee, Mentor mentor){
        if(!forbiddenMatches.containsKey(mentee)){
            forbiddenMatches.put(mentee, new HashSet<>());
        }
        forbiddenMatches.get(mentee).add(mentor);
    }
    
    /**
     * Allows matches between the input mentee and mentor.
     * Cancels a previous call to {@link #forbidMatch(java.lang.Object, java.lang.Object) }, has no
     * effect if no such call has been made.
     * @param mentee that can be matched with the mentor
     * @param mentor that can be matched with the mentee
     */
    public synchronized void allowMatch(Mentee mentee, Mentor mentor){
        if(forbiddenMatches.containsKey(mentee) && forbiddenMatches.get(mentee).contains(mentor)){
            forbiddenMatches.get(mentee).remove(mentor);
        }
    }
    
    private class SupplierList {
        Future<List<Mentee>> menteesSupplier;
        Future<List<Mentor>> mentorsSupplier;
        Future<Collection<ProgressiveCriterion<Mentee, Mentor>>> progressiveCriteriaSupplier;
        Future<Collection<NecessaryCriterion<Mentee, Mentor>>> necessaryCriteriaSupplier;
        Future<Mentee> placeholderMenteeSupplier;
        Future<Mentor> placeholderMentorSupplier;
        
        SupplierList(){}
        
        SupplierList atomicCopy(){
            SupplierList result = new SupplierList();
            synchronized(MatchesBuilderHandler.this){
                result.menteesSupplier = menteesSupplier;
                result.mentorsSupplier = mentorsSupplier;
                result.progressiveCriteriaSupplier = progressiveCriteriaSupplier;
                result.necessaryCriteriaSupplier = necessaryCriteriaSupplier;
                result.placeholderMenteeSupplier = placeholderMenteeSupplier;
                result.placeholderMentorSupplier = placeholderMentorSupplier;
            }
            return result;
        }
    }
    
    private class ParametersList {
        List<Mentee> mentees;
        List<Mentor> mentors;
        Collection<ProgressiveCriterion<Mentee, Mentor>> progressiveCriteria;
        Collection<NecessaryCriterion<Mentee, Mentor>> necessaryCriteria;
        Mentee placeholderMentee;
        Mentor placeholderMentor;
        
        ParametersList(SupplierList suppliers) throws IllegalStateException, InterruptedException,
                ExecutionException {
            verifyMandatorySuppliers(suppliers);
            mentees = suppliers.menteesSupplier.get();
            mentors = suppliers.mentorsSupplier.get();
            progressiveCriteria = suppliers.progressiveCriteriaSupplier.get();
            if(suppliers.necessaryCriteriaSupplier != null){
                necessaryCriteria = suppliers.necessaryCriteriaSupplier.get();
            }
            if(suppliers.placeholderMenteeSupplier != null){
                placeholderMentee = suppliers.placeholderMenteeSupplier.get();
            }
            if(suppliers.placeholderMentorSupplier != null){
                placeholderMentor = suppliers.placeholderMentorSupplier.get();
            }
        }
    
        private void verifyMandatorySuppliers(SupplierList suppliers) throws IllegalStateException {
            if(suppliers.menteesSupplier == null
                    || suppliers.mentorsSupplier == null 
                    || suppliers.progressiveCriteriaSupplier == null){
                throw new IllegalStateException(
                        forgeMissingMandatorySettersErrorMessage(
                                suppliers.menteesSupplier,
                                suppliers.mentorsSupplier, 
                                suppliers.progressiveCriteriaSupplier));
            }
        }
        
        private String forgeMissingMandatorySettersErrorMessage(Future<List<Mentee>> menteesSupplier,
                Future<List<Mentor>> mentorsSupplier,
                Future<Collection<ProgressiveCriterion<Mentee, Mentor>>> progressiveCriteriaSupplier) {
            List<String> missingFields = new ArrayList<>(3);
            addFieldLabelIfNull(menteesSupplier, "mentees supplier", missingFields);
            addFieldLabelIfNull(mentorsSupplier, "mentors supplier", missingFields);
            addFieldLabelIfNull(progressiveCriteriaSupplier, "progressive criteria supplier", missingFields);
            return "Some mandatory setters are missing: " + missingFields;
        }

        private void addFieldLabelIfNull(Object field, String label, List<String> list){
            if (field == null){
                list.add(label);
            }
        }
    }
}

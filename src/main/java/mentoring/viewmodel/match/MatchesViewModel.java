package mentoring.viewmodel.match;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import mentoring.configuration.ResultConfiguration;
import mentoring.io.ResultWriter;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.viewmodel.base.ConfigurableViewModel;
import mentoring.viewmodel.base.SimpleObservableViewModel;
import mentoring.viewmodel.base.TabularDataViewModel;

/**
 * ViewModel responsible for representing a {@link Matches} object. 
 * <p> For each instance of this class, a call to 
 * {@link #setConfiguration(mentoring.configuration.ResultConfiguration) } should be made before
 * calling any other method.
 * 
 * @param <Mentee> type of the first element of the {@link Match} objects to encapsulate
 * @param <Mentor> type of the second element of the {@link Match} objects to encapsulate
 * @param <VM> type of the {@link MatchViewModel} used to represent each individual match
 */
public class MatchesViewModel<Mentee, Mentor, VM extends MatchViewModel<Mentee, Mentor>> 
        extends SimpleObservableViewModel implements TabularDataViewModel<VM>,
        ConfigurableViewModel<ResultConfiguration<Mentee, Mentor>> {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<String> headerContent = new ArrayList<>();
    private final List<VM> content = new ArrayList<>();
    private ResultConfiguration<Mentee, Mentor> configuration = null;
    private Matches<Mentee, Mentor> pendingMatches = null;
    private final BiFunction<ResultConfiguration<Mentee, Mentor>, Match<Mentee, Mentor>, 
            VM> vmFactory;
    private boolean invalidated = false;
    private boolean invalidatedHeader = false;
    
    /**
     * Builds a new MatchesViewModel instance.
     * @param vmFactory factory building each ViewModel encapsulating the individual {@link Match}
     *      objects of the {@link Matches} object represented by this ViewModel.
     */
    protected MatchesViewModel(BiFunction<ResultConfiguration<Mentee, Mentor>, 
            Match<Mentee, Mentor>, VM> vmFactory){
        this.vmFactory = vmFactory;
    }
    
    /**
     * Returns the header of the {@link Matches} object.
     * The header contains the attributes kept in the {@link ResultConfiguration} argument of the 
     * last call to 
     * {@link #update(mentoring.configuration.ResultConfiguration, mentoring.match.Matches) }.
     */
    @Override
    public List<String> getHeaders(){
        updateIfNecessary();
        return headerContent;
    }
    
    /**
     * Returns the content of the represented {@link Matches} object.
     */
    @Override
    public List<VM> getContent(){
        updateIfNecessary();
        return content;
    }
    
    /**
     * Set how the encapsulated {@link Matches} object should be displayed, and what data to expose.
     * @param configuration used to select the attributes to represent
     */
    @Override
    public synchronized void setConfiguration(ResultConfiguration<Mentee, Mentor> configuration){
        Objects.requireNonNull(configuration);
        if(!configuration.equals(this.configuration)){
            this.configuration = configuration;
            invalidatedHeader = true;
            if(! content.isEmpty()){
                invalidated = true;
            }
            notifyListeners();
        }
    }
    
    /**
     * Add an item to the encapsulated {@link Matches} instance.
     * @param match to add
     * @throws IllegalStateException if no configuration has been defined yet
     */
    public synchronized void add(Match<Mentee, Mentor> match) throws IllegalStateException {
        Objects.requireNonNull(match);
        if(configuration == null){
            throw new IllegalStateException("Attempted to add match " + match + " to " + this 
                    + " before setting configuration");
        }
        //TODO concurrency : make lazy modification
        updateIfNecessary();
        content.add(vmFactory.apply(configuration, match));
        notifyListeners();
    }
    
    /**
     * Replace the content of the encapsulated {@link Matches} with the specified elements
     * @param matches to be encapsulated
     * @throws IllegalStateException if no configuration has been defined yet
     */
    public synchronized void setAll(Matches<Mentee, Mentor> matches) throws IllegalStateException {
        Objects.requireNonNull(matches);
        if(configuration == null){
            throw new IllegalStateException("Attempted to add matches " + matches + " to " + this 
                    + " before setting configuration");
        }
        this.pendingMatches = matches;
        this.invalidated = true;
        notifyListeners();
    }
    
    private void updateIfNecessary(){
        if(invalidated || invalidatedHeader){
            actuallyUpdate();
        }
    }
    
    private synchronized void actuallyUpdate(){
        /*FIXME possible race condition between update and actuallyUpdate: verify if synchronized 
        methods are all protected by the same intrinsic lock.
        To help fixing race conditions, consider adding a pendingConfiguration like for the pending matches
        */
        if(invalidatedHeader){
            prepareHeader(configuration);
            invalidatedHeader = false;
        }
        if(invalidated){
            prepareItems(configuration, pendingMatches);
            invalidated = false;
        }
    }
    
    private void prepareHeader(ResultConfiguration<Mentee, Mentor> configuration) {
        this.configuration = configuration;
        headerContent.clear();
        headerContent.addAll(configuration.getResultHeader());
    }
            
    private void prepareItems(
            ResultConfiguration<Mentee, Mentor> configuration, Matches<Mentee, Mentor> matches) {
        content.clear();
        for (Match<Mentee, Mentor> match : matches){
            content.add(vmFactory.apply(configuration, match));
        }
    }
    
    /**
     * Remove an item from the encapsulated {@link Matches} instance if it is present (optional 
     * operation).
     * @param item ViewModel encapsulating the element to remove from this ViewModel, if present 
     * @return true if an element was removed as a result of this call
     * @see Collection#remove(java.lang.Object) 
     */
    public synchronized boolean remove(VM item){
        if(getContent().remove(item)){
            notifyListeners();
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Write the current matches in a given stream.
     * @param writer where to write the results
     * @param configuration how to write the results
     * @param writeHeader if true, the header is written before the data
     * @throws IOException when the output stream cannot be written
     */
    public synchronized void writeMatches(Writer writer, 
            ResultConfiguration<Mentee, Mentor> configuration,
            boolean writeHeader) throws IOException {
        Objects.requireNonNull(writer);
        Objects.requireNonNull(configuration);
        ResultWriter<Mentee, Mentor> resultWriter = new ResultWriter<>(configuration);
        List<Match<Mentee, Mentor>> tmpMatches = new ArrayList<>();
        for (MatchViewModel<Mentee, Mentor> vm : getContent()) {
            tmpMatches.add(vm.getData());
        }
        Matches<Mentee, Mentor> matches = new Matches<>(tmpMatches);
        resultWriter.writeMatches(matches, writer, writeHeader);
        
    }
}

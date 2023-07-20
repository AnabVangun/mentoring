package mentoring.viewmodel.match;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import mentoring.configuration.ResultConfiguration;
import mentoring.io.ResultWriter;
import mentoring.match.Match;
import mentoring.match.Matches;
import mentoring.viewmodel.base.SimpleObservableViewModel;
import mentoring.viewmodel.base.TabularDataViewModel;

/**
 * Viewmodel responsible for representing a {@link Matches} object. The object is not ready until
 * {@link #update(mentoring.configuration.ResultConfiguration, mentoring.match.Matches) } has been
 * called.
 * 
 * @param <Mentee> type of the first element of a {@link Match}.
 * @param <Mentor> type of the second element of a {@link Match}.
 * @param <VM> type of the {@link MatchViewModel} used to represent each match.
 */
public class MatchesViewModel<Mentee, Mentor, VM extends MatchViewModel<Mentee, Mentor>> 
        extends SimpleObservableViewModel implements TabularDataViewModel<VM> {
    //TODO refactor: separate in two classes, one for batch items and one for transferred items
    //The two classes will have links: the headers can be bound
    private final List<String> headerContent = new ArrayList<>();
    private final List<VM> batchUpdateItems = new ArrayList<>();
    private final List<VM> transferredItems = new ArrayList<>();
    private ResultConfiguration<Mentee, Mentor> configuration = null;
    private Matches<Mentee, Mentor> pendingMatches = null;
    private boolean ready = false;
    private final BiFunction<ResultConfiguration<Mentee, Mentor>, Match<Mentee, Mentor>, 
            VM> vmFactory;
    private boolean invalidated = false;
    private boolean invalidatedHeader = false;
    
    /**
     * Builds a new {@code MatchesViewModel} object.
     * @param vmFactory factory building the viewmodel encapsulating the individual {@link Match}
     *      objects of the {@link Matches} object represented by this viewmodel.
     */
    protected MatchesViewModel(BiFunction<ResultConfiguration<Mentee, Mentor>, 
            Match<Mentee, Mentor>, VM> vmFactory){
        this.vmFactory = vmFactory;
    }
    
    /**
     * Returns true if the viewmodel represents valid data.
     */
    public boolean isValid(){
        return ready || invalidated;
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
        return batchUpdateItems;
    }
    
    /**
     * Returns the representation of the {@link Match} objects that have been transferred.
     */
    public List<VM> getTransferredItems(){
        //TODO rename to getManualItems
        updateIfNecessary();
        return transferredItems;
    }
    
    /**
     * Updates this viewmodel to represent a {@link Matches} object.
     * @param configuration used to select the attributes to represent.
     * @param matches the data to represent.
     */
    public synchronized void update(ResultConfiguration<Mentee, Mentor> configuration,
            Matches<Mentee, Mentor> matches){
        Objects.requireNonNull(configuration);
        if(!configuration.equals(this.configuration)){
            this.configuration = configuration;
            invalidatedHeader = true;
        }
        this.pendingMatches = matches;
        invalidated = true;
        notifyListeners();
    }
    
    private void updateIfNecessary(){
        if(invalidated){
            actuallyUpdate();
        }
    }
    
    private synchronized void actuallyUpdate(){
        /*FIXME possible race condition between update and actuallyUpdate: verify if synchronized 
        methods are all protected by the same intrinsic lock.
        */
        if(invalidated){
            if(invalidatedHeader){
                prepareHeader(configuration);
                updateManualItems();
                invalidatedHeader = false;
            }
            prepareItems(configuration, pendingMatches);
            ready = true;
            invalidated = false;
        }
    }
    
    private void prepareHeader(ResultConfiguration<Mentee, Mentor> configuration) {
        this.configuration = configuration;
        headerContent.clear();
        headerContent.addAll(Arrays.asList(configuration.getResultHeader()));
    }
    
    private void updateManualItems(){
        transferredItems.replaceAll(vm -> vmFactory.apply(configuration, vm.getData()));
    }
            
    private void prepareItems(
            ResultConfiguration<Mentee, Mentor> configuration, Matches<Mentee, Mentor> matches) {
        batchUpdateItems.clear();
        for (Match<Mentee, Mentor> match : matches){
            batchUpdateItems.add(vmFactory.apply(configuration, match));
        }
    }
    
    /**
     * Add an item to the manual items list.
     * @param item to add
     * @throws IllegalStateException if this instance is not ready when calling this method
     */
    public synchronized void addManualItem(Match<Mentee, Mentor> item){
        //TODO make lazy modification
        //TODO mark the batch items invalid if the Match is in conflict with one from the batch.
        Objects.requireNonNull(item);
        transferredItems.add(vmFactory.apply(configuration, item));
        notifyListeners();
    }
    
    /**
     * Remove an item from the manual items list, if it is present (optional operation).
     * @param item element to be removed from this view model, if present
     * @return true if an element was removed as a result of this call
     * @see Collection#remove(java.lang.Object) 
     */
    public synchronized boolean removeManualItem(VM item){
        Objects.requireNonNull(item);
        if(getTransferredItems().remove(item)){
            notifyListeners();
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Write the current matches in a given stream. The manual matches are written out before the
     * automated ones.
     * @param os where to write the results
     * @param configuration how to write the results
     * @throws IOException when the output stream cannot be written
     */
    public synchronized void writeMatches(OutputStream os, 
            ResultConfiguration<Mentee, Mentor> configuration) throws IOException {
        Objects.requireNonNull(os);
        Objects.requireNonNull(configuration);
        ResultWriter<Mentee, Mentor> resultWriter = new ResultWriter<>(configuration);
        List<Match<Mentee, Mentor>> tmpMatches = new ArrayList<>();
        for (MatchViewModel<Mentee, Mentor> vm : getTransferredItems()){
            tmpMatches.add(vm.getData());
        }
        for (MatchViewModel<Mentee, Mentor> vm : getContent()) {
            tmpMatches.add(vm.getData());
        }
        Matches<Mentee, Mentor> matches = new Matches<>(tmpMatches);
        try(Writer writer = new PrintWriter(os, true, Charset.forName("utf-8"))){
            resultWriter.writeMatches(matches, writer);
        }
    }
}

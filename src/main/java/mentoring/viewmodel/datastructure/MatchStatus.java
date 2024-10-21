package mentoring.viewmodel.datastructure;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import mentoring.viewmodel.base.Parameters;

/**
 * Description of the type of matches an external object is involved in.
 */
public class MatchStatus {
    
    public enum MatchFlag{
        MANUAL_MATCH(Parameters.MANUAL_MATCH_PSEUDOCLASS),
        COMPUTED_MATCH(Parameters.COMPUTED_MATCH_PSEUDOCLASS);
        
        private final PseudoClass pseudoClass;
        private MatchFlag(String pseudoClass){
            this.pseudoClass = PseudoClass.getPseudoClass(pseudoClass);
        }
        
        public PseudoClass getPseudoClass(){
            return pseudoClass;
        }
    }
    
    private static final List<PseudoClass> ALL_PSEUDO_CLASSES = Collections.unmodifiableList(
            Arrays.stream(MatchFlag.values()).map(MatchFlag::getPseudoClass)
                    .collect(Collectors.toList()));
    
    /**
     * Get all the {@link PseudoClass} that can be associated with {@link MatchStatus}.
     * @return an unmodifiable list.
     */
    public static List<PseudoClass> getAllPseudoClasses(){
        return ALL_PSEUDO_CLASSES;
    }
    
    private final ObservableSet<PseudoClass> modifiablePseudoClassState = FXCollections.observableSet();
    private final ObservableSet<PseudoClass> pseudoClassState = 
            FXCollections.unmodifiableObservableSet(modifiablePseudoClassState);
    
    
    /**
     * Add a new flag to the current status. 
     * Optional operation, does nothing if the flag was already set.
     * @param flag to add
     * @return true if the operation was performed, false otherwise
     */
    public boolean add(MatchFlag flag){
        return modifiablePseudoClassState.add(flag.pseudoClass);
    }
    
    /**
     * Remove a flag from the current status.
     * Optional operation, does nothing if the flag was not already set.
     * @param flag to remove
     * @return true if the operation was performed, false otherwise
     */
    public boolean remove(MatchFlag flag){
        return modifiablePseudoClassState.remove(flag.pseudoClass);
    }
    
    /**
     * Get the style classes that represent the current status.
     * @return an observable representing the current status
     */
    public ObservableSet<PseudoClass> getPseudoClassState(){
        return pseudoClassState;
    }
}

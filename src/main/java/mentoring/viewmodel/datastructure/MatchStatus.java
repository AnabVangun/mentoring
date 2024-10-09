package mentoring.viewmodel.datastructure;

import java.util.EnumSet;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mentoring.viewmodel.base.Parameters;

/**
 * Description of the type of matches an external object is involved in.
 */
public class MatchStatus {
    
    public enum MatchFlag{
        MANUAL_MATCH(Parameters.MANUAL_MATCH_PSEUDOCLASS),
        COMPUTED_MATCH(Parameters.COMPUTED_MATCH_PSEUDOCLASS);
        
        final String styleClass;
        private MatchFlag(String pseudoClass){
            this.styleClass = pseudoClass;
        }
    }
    
    private final Set<MatchFlag> status = EnumSet.noneOf(MatchFlag.class);
    private final ObservableList<String> modifiableStyleClass = FXCollections.observableArrayList();
    private final ObservableList<String> styleClass = 
            FXCollections.unmodifiableObservableList(modifiableStyleClass);
    
    
    /**
     * Add a new flag to the current status. 
     * Optional operation, does nothing if the flag was already set.
     * @param flag to add
     * @return true if the operation was performed, false otherwise
     */
    public boolean add(MatchFlag flag){
        boolean result = status.add(flag);
        if(result){
            modifiableStyleClass.add(flag.styleClass);
        }
        return result;
    }
    
    /**
     * Remove a flag from the current status.
     * Optional operation, does nothing if the flag was not already set.
     * @param flag to remove
     * @return true if the operation was performed, false otherwise
     */
    public boolean remove(MatchFlag flag){
        boolean result = status.remove(flag);
        if(result){
            modifiableStyleClass.remove(flag.styleClass);
        }
        return result;
    }
    
    /**
     * Get the style classes that represent the current status.
     * @return an observable representing the current status
     */
    public ObservableList<String> getStyleClass(){
        return styleClass;
    }
}

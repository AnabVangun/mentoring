
package mentoring.viewmodel.base;

import java.util.Collection;
import java.util.List;

/**
 * Interface for view models encapsulating tabular data. Tabular data consists of a collection of
 * items that all share a common description that can typically be pictured as the header of a 
 * table.
 * @param <E> the type of encapsulated data
 */
public interface TabularDataViewModel<E> {
    /**
     * Get the header describing the content of the view model.
     * @return a list of String objects typically corresponding to property names.
     */
    List<String> getHeaders();
    /**
     * Get the data encapsulated by the view model. Implementations of this interface are free to
     * choose between all the types of collections, there are no guarantees as regards indexing, 
     * unicity or null-handling.
     * @return a collection of the data that can be represented using the headers.
     */
    Collection<E> getContent();
    
}

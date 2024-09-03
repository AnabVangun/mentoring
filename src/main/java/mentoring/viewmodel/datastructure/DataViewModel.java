package mentoring.viewmodel.datastructure;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

/**
 * ViewModel responsible for displaying arbitrary data. The data SHOULD be immutable.
 * @param <T> type of data displayed by the ViewModel.
 */
public abstract class DataViewModel<T> {
    //TODO add tests
    private final Map<String, Object> formattedData;
    private final T data;
    
    /**
     * Builds a new DataViewModel.
     * @param data to encapsulate in the ViewModel
     * @param formatter used to prepare the data
     */
    protected DataViewModel(T data, Function<T, Iterator<Map.Entry<String, Object>>> formatter){
        this.data = data;
        Map<String, Object> modifiableData = new HashMap<>();
        formatter.apply(data).forEachRemaining(
                entry -> modifiableData.put(entry.getKey(), entry.getValue()));
        this.formattedData = Collections.unmodifiableMap(modifiableData);
    }
    
    /**
     * Get the encapsulated data formatted as a map of Strings.
     * @return the formatted data
     */
    public Map<String, Object> getFormattedData(){
        return formattedData;
    }
    
    /**
     * Get the raw encapsulated data.
     * @return the data
     */
    public T getData(){
        return data;
    }
}

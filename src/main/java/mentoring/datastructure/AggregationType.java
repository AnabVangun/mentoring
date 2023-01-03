package mentoring.datastructure;

import java.util.HashMap;
import java.util.Map;

public enum AggregationType {
    INDEXED("indexed"),
    SET("set");

    private final String stringValue;
    private AggregationType(String s){
        this.stringValue = s;
    }

    private static final Map<String, AggregationType> lookup = new HashMap<>();

    static {
        for(AggregationType value : AggregationType.values()){
            lookup.put(value.stringValue, value);
        }
    }

    /**
     * Returns the enum constant of the specified enum type with the specified name. 
     * As opposed to {@link #valueOf(java.lang.String) }, the input value corresponds to the 
     * string associated with the enum constant rather than its identifier.
     * @param value the string attached to a specific {@code AggregationType}
     * @return the AggregationType constant corresponding to the value
     * @throws IllegalArgumentException if the specified value does not correspond to an 
     * AggregationType constant.
     */
    public static AggregationType getValueOf(String value) throws IllegalArgumentException{
        String simplified = value.toLowerCase();
        if(! lookup.containsKey(simplified)){
            throw new IllegalArgumentException("Aggregation type %s is invalid, valid values"
                    + " are %s".formatted(value, lookup.keySet()));
        }
        return lookup.get(simplified);
    }
}
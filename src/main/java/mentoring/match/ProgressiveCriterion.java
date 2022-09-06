package mentoring.match;

import java.util.function.ToIntBiFunction;
/**
 * The general contract that a progressive criterion must follow.
 * 
 * A progressive criterion is an integer criterion that must always be positive or zero. The higher 
 * its value, the less viable the pairing between the input mentee and mentor.
 * A progressive criterion should be stable: given a mentee and a mentor, it 
 * should always return the same value as long as they both remain unchanged.
 * No additional constraints are set on criteria: specifically, they need not 
 * be symmetrical (and might no be able to if {@code Mentee} and {@code Mentor} do not represent
 * the same class).
 * @param <Mentee> class representing an individual mentee
 * @param <Mentor> class representing an individual mentor
 */
public interface ProgressiveCriterion<Mentee, Mentor> extends ToIntBiFunction<Mentee, Mentor> {
}
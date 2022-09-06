package mentoring.match;

import java.util.function.BiPredicate;
/**
 * The general contract that a necessary criterion must follow.
 * 
 * A necessary criterion is a boolean criterion that must be true for a pairing between a mentee and
 * a mentor to be viable. A necessary criterion should be stable: given a mentee and a mentor, it 
 * should always return the same value as long as they both remain unchanged.
 * No additional constraints are set on criteria: specifically, they need not 
 * be symmetrical (and might no be able to if {@code Mentee} and {@code Mentor} do not represent
 * the same class).
 * @param <Mentee> class representing an individual mentee
 * @param <Mentor> class representing an individual mentor
 */
public interface NecessaryCriterion<Mentee, Mentor> extends BiPredicate<Mentee, Mentor> {
}

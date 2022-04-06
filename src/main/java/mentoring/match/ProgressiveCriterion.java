package mentoring.match;

import java.util.function.ToIntBiFunction;

public interface ProgressiveCriterion<Mentee, Mentor> extends ToIntBiFunction<Mentee, Mentor> {
}
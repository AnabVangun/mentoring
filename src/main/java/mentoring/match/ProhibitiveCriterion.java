package mentoring.match;

import java.util.function.BiPredicate;

public interface ProhibitiveCriterion<Mentee, Mentor> extends BiPredicate<Mentee, Mentor> {
}

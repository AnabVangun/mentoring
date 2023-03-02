package mentoring.io;

import java.util.function.Function;
import mentoring.datastructure.Person;
import mentoring.match.Match;

/**
 * Parser used to build functions from strings.
 */
class MatchFunctionBuilder {
    private static final String STANDARD_TOKEN = "§§";
    private static final String CUSTOM_PROPERTY_TOKEN = "§_§";
    private static final String CUSTOM_MULTIPLE_PROPERTY_TOKEN = "§_M§";
    static final String NAME_PROPERTY_TOKEN = STANDARD_TOKEN + "Name";
    static final String COST_TOKEN = STANDARD_TOKEN + "Cost";
    
    /**
     * Build a function to apply on a match between two persons.
     * @param input a String containing the function to apply.
     * @return the function as a callable Java object.
     */
    public static Function<Match<Person, Person>, String> buildMatchFunction(String input){
        Function<Match<Person, Person>, Person> personAccessor;
        int prefixLength;
        if (input.startsWith(STANDARD_TOKEN + "Mentor")){
            personAccessor = Match::getMentor;
            prefixLength = STANDARD_TOKEN.length() + "Mentor".length();
        } else if (input.startsWith(STANDARD_TOKEN + "Mentee")){
            personAccessor = Match::getMentee;
            prefixLength = STANDARD_TOKEN.length() + "Mentee".length();
        } else if (input.equals(COST_TOKEN)){
            return match -> Integer.toString(match.getCost());
        } else {
            throw new IllegalArgumentException(
                    "Input string \"" + input + "\" could not be parsed as a match function");
        }
        return personAccessor
                .andThen(buildPersonPropertyGetter(input.substring(prefixLength)))
                .andThen(Object::toString);
    }
    
    /**
     * Build the getter to a person's property.
     * @param input a String containing the getter token and the name of the property to parse
     * @return a getter for the property.
     */
    public static Function<Person, Object> buildPersonPropertyGetter(String input){
        if (input.startsWith(NAME_PROPERTY_TOKEN)){
            return person -> person.getFullName();
        } else if (input.startsWith(CUSTOM_PROPERTY_TOKEN)){
            return buildPersonGenericPropertyGetter(input.substring(CUSTOM_PROPERTY_TOKEN.length()));
        } else if (input.startsWith(CUSTOM_MULTIPLE_PROPERTY_TOKEN)){
            return buildPersonGenericMultiplePropertyGetter(
                    input.substring(CUSTOM_MULTIPLE_PROPERTY_TOKEN.length()));
        } else {
            throw new IllegalArgumentException("Could not parse command " + input);
        }
    }
    
    private static Function<Person, Object> buildPersonGenericPropertyGetter(String input){
        return person -> person.getPropertyAs(input, Object.class);
    }
    
    private static Function<Person, Object> buildPersonGenericMultiplePropertyGetter(String input){
        return person -> person.getPropertyAsMapOf(input, Object.class, Object.class);
    }
    /*
    TODO: implement
    Pitfalls: common behaviour between strings for ResultConfigurationParser and 
    CriteriaConfigurationParser but different functions: 
    Function<Match...> vs BiFunction<Mentee, Mentor, String>.
    Build behaviour from String:
        read first token
            if not a valid token, crash
            else, while token needs args, recursively parse next token
    
    Four types of tokens :
        1. Base tokens (§§Mentee, §§Mentor, §§Cost for ResultConfigurationParser)
        2. Property tokens (§_§Ville) that retrieve a property of a base token
        3. Binary tokens (+, *) that combine previous and next operations
        4. Functions (SET_DISTANCE) that combine a number of following operations
        5. scalars (at least constant number for math operations)
    Difficulties: 
        1. precedence between token evaluation (+ vs * vs SET_DISTANCE)
        2. Word order in a sentence: some tokens come before, others after the tokens they combine.
    */
    
}

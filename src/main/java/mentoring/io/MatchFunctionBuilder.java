package mentoring.io;

import java.util.function.Function;
import mentoring.datastructure.Person;
import mentoring.match.Match;

/**
 * Parser used to build functions from strings.
 */
class MatchFunctionBuilder {
    private static final String FIRST_LEVEL_TOKEN = "§§";
    private static final String SECOND_LEVEL_TOKEN = "§_§";
    static final String NAME_PROPERTY_TOKEN = FIRST_LEVEL_TOKEN + "Name";
    
    /**
     * Build a function to apply on a match between two persons.
     * @param input a String containing the function to apply.
     * @return the function as a callable Java object.
     */
    public static Function<Match<Person, Person>, String> buildMatchFunction(String input){
        Function<Match<Person, Person>, Person> personAccessor;
        int prefixLength;
        if (input.startsWith(FIRST_LEVEL_TOKEN + "Mentor")){
            personAccessor = Match::getMentor;
            prefixLength = FIRST_LEVEL_TOKEN.length() + "Mentor".length();
        } else if (input.startsWith(FIRST_LEVEL_TOKEN + "Mentee")){
            personAccessor = Match::getMentee;
            prefixLength = FIRST_LEVEL_TOKEN.length() + "Mentee".length();
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
        }else if(input.startsWith(SECOND_LEVEL_TOKEN)){
            return buildPersonGenericPropertyGetter(input.substring(SECOND_LEVEL_TOKEN.length()));
        } else {
            throw new IllegalArgumentException("Could not parse command " + input);
        }
    }
    
    private static Function<Person, Object> buildPersonGenericPropertyGetter(String input){
        return person -> person.getPropertyAs(input, Object.class);
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
    Difficulties: 
        1. precedence between token evaluation (+ vs * vs SET_DISTANCE)
        2. Word order in a sentence: some tokens come before, others after the tokens they combine.
    */
    
}

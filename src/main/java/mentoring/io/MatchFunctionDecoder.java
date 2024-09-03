package mentoring.io;

import java.util.function.Function;
import mentoring.datastructure.Person;
import mentoring.match.Match;

    /*
    TODO: implement criteria function builder, common parsing architecture but :
        a. result is a ProgressiveCriterion or a NecessaryCriterion
        b. need operations between properties (addition, multiplication...), 
            access to toolbox functions and scalars
    */

/**
 * Decoder used to decode functions from strings.
 */
class MatchFunctionDecoder {
    static final String STANDARD_TOKEN = "§§";
    static final String CUSTOM_PROPERTY_TOKEN = "§_§";
    static final String CUSTOM_MULTIPLE_PROPERTY_TOKEN = "§_M§";
    static final String MENTOR_TOKEN = STANDARD_TOKEN + "Mentor";
    static final String MENTEE_TOKEN = STANDARD_TOKEN + "Mentee";
    static final String NAME_PROPERTY_TOKEN = STANDARD_TOKEN + "Name";
    static final String COST_TOKEN = STANDARD_TOKEN + "Cost";
    
    /**
     * Decode a function to apply on a match between two persons.
     * @param input a String containing the function to apply.
     * @return the function as a callable Java object.
     */
    public static Function<Match<Person, Person>, Object> decodeMatchFunction(String input){
        Function<Match<Person, Person>, Person> personAccessor;
        int prefixLength;
        if (input.startsWith(MENTOR_TOKEN)){
            personAccessor = Match::getMentor;
            prefixLength = MENTOR_TOKEN.length();
        } else if (input.startsWith(MENTEE_TOKEN)){
            personAccessor = Match::getMentee;
            prefixLength = MENTEE_TOKEN.length();
        } else if (input.equals(COST_TOKEN)){
            return match -> match.getCost();
        } else {
            throw new IllegalArgumentException(
                    "Input string \"" + input + "\" could not be parsed as a match function");
        }
        return personAccessor
                .andThen(decodePersonPropertyGetter(input.substring(prefixLength)));
    }
    
    /**
     * Decode the getter to a person's property.
     * @param input a String containing the getter token and the name of the property to parse
     * @return a getter for the property.
     */
    static Function<Person, Object> decodePersonPropertyGetter(String input){
        if (input.startsWith(NAME_PROPERTY_TOKEN)){
            return person -> person.getFullName();
        } else if (input.startsWith(CUSTOM_PROPERTY_TOKEN)){
            return decodePersonGenericPropertyGetter(input.substring(CUSTOM_PROPERTY_TOKEN.length()));
        } else if (input.startsWith(CUSTOM_MULTIPLE_PROPERTY_TOKEN)){
            return decodePersonGenericMultiplePropertyGetter(
                    input.substring(CUSTOM_MULTIPLE_PROPERTY_TOKEN.length()));
        } else {
            throw new IllegalArgumentException("Could not parse command " + input);
        }
    }
    
    private static Function<Person, Object> decodePersonGenericPropertyGetter(String input){
        return person -> person.getPropertyAs(input, Object.class);
    }
    
    private static Function<Person, Object> decodePersonGenericMultiplePropertyGetter(String input){
        return person -> person.getPropertyAsMapOf(input, Object.class, Object.class);
    }
}

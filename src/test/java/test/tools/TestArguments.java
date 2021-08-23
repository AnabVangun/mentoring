package test.tools;

/**
 * Interface defining the general contract that inner classes should implement
 * to ease the unit testing.
 * 
 * Concrete classes implementing it should provide a unique constructor similar
 * to the main one of the class parameter, and override the toString object 
 * method.
 *
 * @param <T>	class under test: the arguments will be used to generate 
 * instances of that class.
 * @author AnabVangun
 */
public interface TestArguments<T> {
	/**
	 * Initialises an object to use in a test.
	 * @return
	 */
	T convert();
}

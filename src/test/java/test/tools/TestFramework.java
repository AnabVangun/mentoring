package test.tools;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

/**
 * Standard test framework for all test classes. This interface provides two pairs of methods to run
 * simple and nested tests: one based on the class-level {@link #argumentsSupplier()} method, the 
 * other using a specified stream of arguments.
 * 
 * @param <T> the type of arguments used to run the tests. The {@code toString()} method SHOULD be
 * overridden to provide a description of the specific test case represented by the argument.
 */
public interface TestFramework<T> {
	/**Returns a {@link Stream} of arguments to perform a test. */
	Stream<T> argumentsSupplier();
	
        /**
         * Generate a standard name for a test case.
         * 
         * @param methodName name of the method to test, optionally followed by details of the 
         *      specific aspects of the method under test. 
         * @param args arguments of the test case.
         * @return a standard name for the test case based on the test class, the method name and 
         *      the arguments.
         */
        default String testName(String methodName, T args){
            return String.format("%s.%s on %s", this.getClass().getCanonicalName(), methodName, 
                args);
        }
	/**
	 * Forges a {@link DynamicNode} to run the input test for each element 
	 * returned by {@link #argumentsSupplier()}.
	 * @param methodName String to set as the test name.
	 * @param tester Consumer to run on each element of the argument stream.
	 * @return a stream of nodes, each running a test.
	 */
	default Stream<DynamicNode> test(String methodName, Consumer<T> tester){
		return test(argumentsSupplier(), methodName, tester);
	}

	/**
	 * Forges a {@link Stream} of {@link DynamicNode} objects that run in independent
	 * {@link DynamicTest} instances each {@link Executable} returned by the 
	 * input {@link Function} on each element returned by 
	 * {@link #argumentsSupplier()}.
	 * @param methodName String to set as the test name.
	 * @param testerStream {@code Function} that converts a single argument into a 
         * {@code Stream} of test cases, each comprising of a name (added as a suffix in the test 
         * name) and an {@code Executable}.
	 * @return a stream of nodes, each running a series of tests.
	 */
	default Stream<DynamicNode> testContainer(String methodName, 
            Function<T, Stream<Map.Entry<String, Executable>>> testerStream){
		return testContainer(argumentsSupplier(), methodName, testerStream);
	}
	/**
	 * Forges a {@link DynamicTest} to run the input test for each element 
	 * of a {@link Stream} of arguments.
	 * @param stream Stream of arguments. A single test case is generated for each one of them.
	 * @param methodName String to set as the test name.
	 * @param tester Consumer to run on each element of the argument stream.
	 * @return a stream of nodes, each running a test.
	 */
	default Stream<DynamicNode> test(Stream<T> stream, String methodName, Consumer<T> tester){
            return stream.map(args -> 
                dynamicTest(testName(methodName, args), () -> tester.accept(args)));
	}
	/**
	 * Forges a {@link Stream} of {@link DynamicNode} objects that run in independent
	 * {@link DynamicTest} instances each {@link Executable} returned by the 
	 * input {@link Function} on each element of the input {@link Stream}.
	 * @param stream Stream of arguments. A test case container is generated for each one of 
         *      them.
	 * @param methodName String to set as the test name.
	 * @param testerStream {@code Function} that converts a single argument into a 
         * {@code Stream} of test cases, each comprising of a name (added as a suffix in the test 
         * name) and an {@code Executable}.
	 * @return a stream of nodes, each running a series of tests.
	 */
	default Stream<DynamicNode> testContainer(Stream<T> stream, String methodName, 
            Function<T, Stream<Map.Entry<String, Executable>>> testerStream){
		return stream.map(args -> {
                    String message = testName(methodName, args);
                    return dynamicContainer(message, 
			testerStream.apply(args).map(entry -> 
                            dynamicTest(message + entry.getKey(), entry.getValue())));
				});
	}
}
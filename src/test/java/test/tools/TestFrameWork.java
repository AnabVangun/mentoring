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

public interface TestFrameWork<T, S extends TestArguments<T>> {
	/**
	 * @return a {@link Stream} of arguments to initialise an object to test.
	 */
	Stream<S> argumentsSupplier();
	
        default <U> String testName(String methodName, U args){
            return String.format("%s.%s on %s", 
                    this.getClass().getCanonicalName(), methodName, args);
        }
	/**
	 * Forges a {@link DynamicTest} to run the input test for each element 
	 * returned by the implementation of 
	 * {@link TestFrameWork#argumentsSupplier()}.
	 * @param methodName	to set as the test name.
	 * @param tester		to run as the test.
	 * @return	a stream of nodes running the test.
	 */
	default Stream<DynamicTest> test(String methodName, Consumer<S> tester){
		return test(argumentsSupplier(), methodName, tester);
	}

	/**
	 * Forges a {@link Stream} of {@link DynamicNode} that runs in independent
	 * {@link DynamicTest} instances each {@link Executable} returned by the 
	 * input {@link Function} on each element returned by the implementation of
	 * {@link TestFrameWork#argumentsSupplier()}.
	 * @param methodName	to set as the test container's name.
	 * @param testerStream	to generate the {@link Stream} of test using for 
	 * each element the {@link String} as a suffix in the test name and the
	 * {@link Executable} as the test to run.
	 * @return	a stream of nodes running the tests.
	 */
	default Stream<DynamicNode> testContainer(String methodName, 
			Function<S, Stream<Map.Entry<String, Executable>>> testerStream){
		return testContainer(argumentsSupplier(), methodName, testerStream);
	}
	/**
	 * Forges a {@link DynamicTest} to run the input test for each element 
	 * of a {@link Stream} of arguments.
         * @param <U> type of the stream of test cases.
	 * @param stream		of arguments, the tests will be run on each 
	 * element.
	 * @param methodName	to set as the test name.
	 * @param tester		to run as the test.
	 * @return	a stream of nodes running the tests.
	 */
	default <U> Stream<DynamicTest> test(Stream<U> stream, String methodName, Consumer<U> tester){
		return stream.map(args
				-> dynamicTest(testName(methodName, args), () -> tester.accept(args)));
	}
	/**
	 * Forges a {@link Stream} of {@link DynamicNode} that runs in independent
	 * {@link DynamicTest} instances each {@link Executable} returned by the 
	 * input {@link Function} on each element of the input {@link Stream}.
         * @param <U> type of the stream of test cases.
	 * @param stream		of arguments, the tests will be run on each 
	 * element.
	 * @param methodName	to set as the test container's name.
	 * @param testerStream	to generate the {@link Stream} of test using for 
	 * each element the {@link String} as a suffix in the test name and the
	 * {@link Executable} as the test to run.
	 * @return	a stream of nodes running the tests.
	 */
	default <U> Stream<DynamicNode> testContainer(Stream<U> stream, String methodName, 
			Function<U, Stream<Map.Entry<String, Executable>>> testerStream){
		return stream.map(args
				-> {
					String message = testName(methodName, args);
					return dynamicContainer(message, 
							testerStream.apply(args).map(entry 
									-> dynamicTest(message + entry.getKey(), entry.getValue())));
				});
	}
}

package mentoring.configuration;

import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

public interface ExtendedPersonConfigurationTest<T extends ExtendedPersonConfigurationArgs>
        extends SimplePersonConfigurationTest<T> {
    
    String builderName();
    
    @TestFactory
    default Stream<DynamicNode> assertPersonConfigurationAsExpected(){
        return test(builderName() + " returns the expected PersonConfiguration", args -> {
            PersonConfiguration actual = args.convert();
            T.assertResultAsExpected(args, actual);
        });
    }
}

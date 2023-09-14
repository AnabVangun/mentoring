package mentoring.view.base;

import java.util.stream.Stream;
import javafx.stage.Stage;
import mentoring.view.base.WrappableTableColumnTest.WrappableTableColumnArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import test.tools.TestArgs;
import test.tools.TestFramework;

@ExtendWith(ApplicationExtension.class)
class WrappableTableColumnTest implements TestFramework<WrappableTableColumnArgs>{
    //TODO see how to test if column is wrappable
    @Start
    private void start(Stage stage) {
        //no-op
    }
    
    @Override
    public Stream<WrappableTableColumnArgs> argumentsSupplier(){
        return Stream.of(new WrappableTableColumnArgs("unique test case", "header"));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_expectedHeader(){
        return test("constructor sets the expected header", args ->
                Assertions.assertEquals(args.header, args.convert().getText()));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test(Stream.of("specific test case"), "constructor throws an NPE on null input", 
                args -> Assertions.assertThrows(NullPointerException.class,
                        () -> new WrappableTableColumn<>(null)));
    }
    
    static class WrappableTableColumnArgs extends TestArgs {
        final String header;
        
        WrappableTableColumnArgs(String testCase, String header){
            super(testCase);
            this.header = header;
        }
        
        WrappableTableColumn<Object> convert(){
            return new WrappableTableColumn<>(header);
        }
    }
}

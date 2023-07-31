package mentoring.view.base;

import java.util.List;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mentoring.view.base.ViewToolsTest.ViewToolsArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import test.tools.TestFramework;

@ExtendWith(ApplicationExtension.class)
class ViewToolsTest implements TestFramework<ViewToolsArgs>{
    
    @Start
    private void start(Stage stage) {
        //no-op
    }

    @Override
    public Stream<ViewToolsArgs> argumentsSupplier(){
        return Stream.of(new ViewToolsArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> configureFileChooser_configureTitle(){
        return test("configureFileChooser() configures the title of the file chooser", args -> {
            String expectedTitle = "foo";
            FileChooser underTest = ViewTools.configureFileChooser(expectedTitle, List.of());
            Assertions.assertEquals(expectedTitle, underTest.getTitle());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> configureFileChooser_configureFilters(){
        return test("configureFileChooser() configures the filters of the file chooser", args -> {
            List<FileChooser.ExtensionFilter> expectedFilters = List.of(
                    new FileChooser.ExtensionFilter("first filter", "first filter extension"),
                    new FileChooser.ExtensionFilter("second filter", "second filter extension"),
                    new FileChooser.ExtensionFilter("third filter", "third filter extension"));
            FileChooser underTest = ViewTools.configureFileChooser("foo", expectedFilters);
            Assertions.assertEquals(expectedFilters, underTest.getExtensionFilters());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> configureFileChooser_NPE(){
        return test("configureFileChooser() throws NPE on null input", args ->
                Assertions.assertAll(
                        assertThrowsNPE(() -> ViewTools.configureFileChooser(null, List.of())),
                        assertThrowsNPE(() -> ViewTools.configureFileChooser("foo", null))));
    }
    
    @TestFactory
    Stream<DynamicNode> configureButton_configureCaption(){
        return test("configureButton() configures the caption of the button", args -> {
            Button button = new Button();
            String expectedTitle = "foo";
            ViewTools.configureButton(button, expectedTitle, event -> {});
            Assertions.assertEquals(expectedTitle, button.getText());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> configureButton_configureAction(){
        return test("configureButton() configures the caption of the button", args -> {
            Button button = new Button();
            @SuppressWarnings("unchecked")
            EventHandler<ActionEvent> handler = Mockito.mock(EventHandler.class);
            ViewTools.configureButton(button, "foo", handler);
            Assertions.assertEquals(handler, button.getOnAction());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> configureButton_NPE(){
        return test("configureButton() throws NPE on null input", args ->
                Assertions.assertAll(
                        assertThrowsNPE(() -> ViewTools.configureButton(null, "foo", event -> {})),
                        assertThrowsNPE(() -> ViewTools.configureButton(new Button(), null, event -> {})),
                        assertThrowsNPE(() -> ViewTools.configureButton(new Button(), "foo", null))));
    }
    
    static Executable assertThrowsNPE(Executable executable){
        return () -> Assertions.assertThrows(NullPointerException.class, executable);
    }
    
    static record ViewToolsArgs(String testCase){
        @Override
        public String toString(){
            return testCase;
        }
    }
}

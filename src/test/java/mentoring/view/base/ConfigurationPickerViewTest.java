package mentoring.view.base;

import java.util.Map;
import java.util.stream.Stream;
import javafx.beans.binding.ObjectBinding;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import mentoring.view.base.ConfigurationPickerViewTest.ConfigurationPickerViewArgs;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.base.ConfigurationPickerViewModel.ConfigurationType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import test.tools.TestArgs;
import test.tools.TestFramework;

@ExtendWith(ApplicationExtension.class)
class ConfigurationPickerViewTest implements TestFramework<ConfigurationPickerViewArgs>{
    @Start
    private void start(Stage stage) {
        //no-op
    }
    @Override
    public Stream<ConfigurationPickerViewArgs> argumentsSupplier(){
        return Stream.of(new ConfigurationPickerViewArgs("unique test case"));
    }
    
    @TestFactory
    Stream<DynamicNode> forgeConfigurationTypeGetterBinding_expectedBinding(){
        return test("forgeConfigurationTypeGetterBinding() returns the expected binding", args -> {
            ObjectBinding<ConfigurationPickerViewModel.ConfigurationType> binding = args.getBinding();
            args.group.selectToggle(args.knownToggle);
            Assertions.assertEquals(ConfigurationType.KNOWN, binding.get());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> forgeConfigurationTypeGetterBinding_expectedBinding_repeated(){
        return test("forgeConfigurationTypeGetterBinding() returns the expected result after several modifications",
                args -> {
                        ObjectBinding<ConfigurationPickerViewModel.ConfigurationType> binding = 
                                args.getBinding();
                        args.group.selectToggle(args.knownToggle);
                        binding.get();
                        args.group.selectToggle(args.secondFileToggle);
                        Assertions.assertEquals(ConfigurationType.FILE, binding.get());
        });
    }
    
    @TestFactory
    @SuppressWarnings("ThrowableResultIgnored")
    Stream<DynamicNode> forgeConfigurationTypeGetterBinding_invalidToggle(){
        return test("forgeConfigurationTypeGetterBinding() throws an exception on invalid toggle", args -> {
            ObjectBinding<ConfigurationPickerViewModel.ConfigurationType> binding = args.getBinding();
            args.group.selectToggle(new ToggleButton("unexpected toggle"));
            Assertions.assertThrows(IllegalStateException.class,
                    () -> binding.get());
        });
    }
    
    @TestFactory
    Stream<DynamicNode> forgeConfigurationTypeGetterBinding_NPE(){
        ToggleGroup group = new ToggleGroup();
        return test(Stream.of("unique test case"), 
                "forgeConfigurationTypeGetterBinding() throws NPE on null input", args ->
                        Assertions.assertAll(
                                assertForgeConfigurationTypeGetterBindingThrowsNPE(null, Map.of()),
                                assertForgeConfigurationTypeGetterBindingThrowsNPE(group, null)));
    }
    
    static Executable assertForgeConfigurationTypeGetterBindingThrowsNPE(ToggleGroup group, 
            Map<Toggle, ConfigurationPickerViewModel.ConfigurationType> map){
        return () -> Assertions.assertThrows(NullPointerException.class,
                () -> ConfigurationPickerView.forgeConfigurationTypeGetterBinding(group, map));
    }
    
    static class ConfigurationPickerViewArgs extends TestArgs{
        ToggleGroup group = new ToggleGroup();
        Toggle firstFileToggle = new ToggleButton("first");
        Toggle knownToggle = new ToggleButton("second");
        Toggle secondFileToggle = new ToggleButton("third");
        Map<Toggle, ConfigurationPickerViewModel.ConfigurationType> map = Map.of(
                firstFileToggle, ConfigurationType.FILE,
                knownToggle, ConfigurationType.KNOWN,
                secondFileToggle, ConfigurationType.FILE);
        
        ConfigurationPickerViewArgs(String testCase){
            super(testCase);
            group.getToggles().addAll(firstFileToggle, knownToggle, secondFileToggle);
        }
        
         ObjectBinding<ConfigurationPickerViewModel.ConfigurationType> getBinding(){
             return ConfigurationPickerView.forgeConfigurationTypeGetterBinding(group, map);
         }
    }
}

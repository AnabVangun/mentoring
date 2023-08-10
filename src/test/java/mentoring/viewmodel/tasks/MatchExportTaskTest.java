package mentoring.viewmodel.tasks;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import mentoring.configuration.PojoResultConfiguration;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.tasks.MatchExportTaskTest.MatchExportTaskArgs;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InOrder;
import org.mockito.Mockito;
import test.tools.TestArgs;
import test.tools.TestFramework;

class MatchExportTaskTest implements TestFramework<MatchExportTaskArgs>{

    @Override
    public Stream<MatchExportTaskArgs> argumentsSupplier() {
        return Stream.of(new MatchExportTaskArgs("one VM", 0),
                new MatchExportTaskArgs("two VMs", 1));
    }
    
    @TestFactory
    Stream<DynamicNode> exportMatches_NPE(){
        return test(Stream.of(new MatchExportTaskArgs("unique test case", 2)),
                "call() throws an NPE on null input", args -> {
                    Class<NullPointerException> NPE = NullPointerException.class;
                    PersonMatchesViewModel[] vmsWithNull = 
                            new PersonMatchesViewModel[]{args.exportedVMs[0], null};
                    Assertions.assertAll(assertConstructorThrowsNPE(null, args.configurationVM, args.firstExportedVM, 
                                    args.exportedVMs),
                            assertConstructorThrowsNPE(args.supplier, null, args.firstExportedVM, 
                                    args.exportedVMs),
                            assertConstructorThrowsNPE(args.supplier, args.configurationVM, null, 
                                    args.exportedVMs),
                            assertConstructorThrowsNPE(args.supplier, args.configurationVM, 
                                    args.firstExportedVM, (PersonMatchesViewModel[]) null),
                            assertConstructorThrowsNPE(args.supplier, args.configurationVM, 
                                    args.firstExportedVM, vmsWithNull));
                });
    }
    
    static Executable assertConstructorThrowsNPE(MatchExportTask.WriterSupplier supplier, 
            ConfigurationPickerViewModel<ResultConfiguration<Person,Person>> configurationVM, 
            PersonMatchesViewModel vm, PersonMatchesViewModel... extraVMs){
        return () -> Assertions.assertThrows(NullPointerException.class, 
                () -> new MatchExportTask(supplier, configurationVM, vm, extraVMs));
    }
    
    @TestFactory
    Stream<DynamicNode> exportMatches_vmExported(){
        return test("call() exports all the matches", args -> {
            runTask(args.convert());
            List<Executable> toVerify = new ArrayList<>();
            toVerify.add(() -> assertVmExported(args.firstExportedVM, args, true));
            for(PersonMatchesViewModel vm : args.exportedVMs){
                toVerify.add(() -> assertVmExported(vm, args, false));
            }
            Assertions.assertAll(toVerify);
        });
    }
    
    static void assertVmExported(PersonMatchesViewModel vm, MatchExportTaskArgs args, boolean header){
        try {
                Mockito.verify(vm).writeMatches(
                        args.writer, 
                        args.getResultConfiguration(),
                        header);
            } catch (IOException e){
                Assertions.fail("Something went wrong during test verification", e);
            }
    }
    
    @TestFactory
    Stream<DynamicNode> exportMatches_vmExportedInOrder(){
        return test("call() exports the matches in the right order", args -> {
            runTask(args.convert());
            PersonMatchesViewModel[] viewModels = 
                    new PersonMatchesViewModel[args.exportedVMs.length + 1];
            viewModels[0] = args.firstExportedVM;
            System.arraycopy(args.exportedVMs, 0, viewModels, 1, args.exportedVMs.length);
            InOrder verificator = Mockito.inOrder((Object[]) viewModels);
            for (PersonMatchesViewModel vm : args.exportedVMs){
                assertVerificationInOrder(verificator, vm);
            }
        });
    }
    
    static void assertVerificationInOrder(InOrder verificator, PersonMatchesViewModel vm){
        try {
            verificator.verify(vm)
                    .writeMatches(Mockito.any(), Mockito.any(), Mockito.anyBoolean());
        } catch (IOException e){
            Assertions.fail("something went wrong during test verification", e);
        }
    }
    
    @TestFactory
    Stream<DynamicNode> exportMatches_closeWriter(){
        return test("call() closes the supplied writer", args -> {
            runTask(args.convert());
            try {
                Mockito.verify(args.writer).close();
            } catch (IOException e){
                Assertions.fail(e);
            }
        });
    }
    
    static void runTask(MatchExportTask task){
        try {
            task.call();
        } catch (Exception e){
            Assertions.fail(e);
        }
    }
    
    static class MatchExportTaskArgs extends TestArgs{
        final Writer writer = Mockito.mock(Writer.class);
        final MatchExportTask.WriterSupplier supplier = () -> writer;
        @SuppressWarnings("unchecked")
        final ConfigurationPickerViewModel<ResultConfiguration<Person,Person>> configurationVM = 
                Mockito.mock(ConfigurationPickerViewModel.class);
        final PersonMatchesViewModel firstExportedVM = Mockito.mock(PersonMatchesViewModel.class);
        final PersonMatchesViewModel[] exportedVMs;
        
        MatchExportTaskArgs(String testCase, int numberOfExtraVMs){
            super(testCase);
            exportedVMs = new PersonMatchesViewModel[numberOfExtraVMs];
            for (int i = 0; i < numberOfExtraVMs; i++){
                exportedVMs[i] = Mockito.mock(PersonMatchesViewModel.class);
            }
            try {
                Mockito.when(configurationVM.getConfiguration())
                        .thenReturn(PojoResultConfiguration.NAMES_AND_SCORE.getConfiguration());
            } catch (IOException e){
                Assertions.fail("normally unreachable code", e);
            }
        }
        
        ResultConfiguration<Person, Person> getResultConfiguration(){
            try {
                return configurationVM.getConfiguration();
            } catch (IOException e){
                throw new UncheckedIOException(e);
            }
        }
        
        MatchExportTask convert(){
            return new MatchExportTask(supplier, configurationVM, firstExportedVM, exportedVMs);
        }
    }
}

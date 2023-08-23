package mentoring.viewmodel.datastructure;

import java.util.stream.Stream;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.viewmodel.datastructure.ForbiddenMatchViewModelTest.ForbiddenMatchViewModelArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class ForbiddenMatchViewModelTest implements TestFramework<ForbiddenMatchViewModelArgs>{
    @Override
    public Stream<ForbiddenMatchViewModelArgs> argumentsSupplier(){
        return Stream.of(new ForbiddenMatchViewModelArgs("unique test case", "mentee", "mentor"));
    }
    
    @TestFactory
    Stream<DynamicNode> getMenteeName_expectedResult(){
        return test("getMenteeName() returns the expected name", args ->
                Assertions.assertEquals(args.expectedMenteeName, args.convert().getMenteeName()));
    }
    
    @TestFactory
    Stream<DynamicNode> getMentorName_expectedResult(){
        return test("getMentorName() returns the expected name", args ->
                Assertions.assertEquals(args.expectedMentorName, args.convert().getMentorName()));
    }
    
    @TestFactory
    Stream<DynamicNode> getMentee_expectedResult(){
        return test("getMentee() returns the expected person", args ->
                Assertions.assertEquals(args.expectedMentee, args.convert().getMentee()));
    }
    
    @TestFactory
    Stream<DynamicNode> getMentor_expectedResult(){
        return test("getMentor() returns the expected name", args ->
                Assertions.assertEquals(args.expectedMentor, args.convert().getMentor()));
    }
    
    @TestFactory
    Stream<DynamicNode> constructor_NPE(){
        return test("constructor throws an NPE on null input", args -> {
            Person person = new PersonBuilder().build();
            Class<NullPointerException> exception = NullPointerException.class;
            Assertions.assertAll(
                    () -> Assertions.assertThrows(exception, 
                            () -> new ForbiddenMatchViewModel(null, person), "null mentee"),
                    () -> Assertions.assertThrows(exception, 
                            () -> new ForbiddenMatchViewModel(person, null), "null mentor"));
        });
    }
    
    static class ForbiddenMatchViewModelArgs extends TestArgs{
        final String expectedMenteeName;
        final String expectedMentorName;
        final Person expectedMentee;
        final Person expectedMentor;
        
        ForbiddenMatchViewModelArgs(String testCase, String expectedMenteeName, String expectedMentorName){
            super(testCase);
            this.expectedMenteeName = expectedMenteeName;
            this.expectedMentorName = expectedMentorName;
            PersonBuilder builder = new PersonBuilder();
            expectedMentee = builder.withFullName(expectedMenteeName).build();
            expectedMentor = builder.withFullName(expectedMentorName).build();
        }
        
        ForbiddenMatchViewModel convert(){
            return new ForbiddenMatchViewModel(expectedMentee, expectedMentor);
        }
    }
}

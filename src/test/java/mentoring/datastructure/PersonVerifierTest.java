package mentoring.datastructure;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvConstraintViolationException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestFramework;

public class PersonVerifierTest implements TestFramework<PersonVerifierArgument>{

    @Override
    public Stream<PersonVerifierArgument> argumentsSupplier() {
        throw new UnsupportedOperationException("standard supplier not implemented");
    }
    
    Stream<PersonVerifierArgument> invalidArgumentsSupplier() throws IOException{
        Triple<String, String, Integer>[] args = new Triple[]{
            Triple.of("simple invalid file", "Parrainage_test_invalid.csv", 3)
        };
        return argumentsSupplierHelper(args, false);
    }
    Stream<PersonVerifierArgument> validArgumentsSupplier() throws IOException {
        //TODO add cases to fully cover verifier
        Triple<String, String, Integer>[] args = new Triple[]{
            Triple.of("simple valid file", "Parrainage_test_validSimple.csv", 3),
            Triple.of("Valid file with colliding headers", "Parrainage_test_validNullAnswers.csv", 
                3)
        };
        return argumentsSupplierHelper(args, true);
    }
    
    private Stream<PersonVerifierArgument> argumentsSupplierHelper (
            Triple<String, String, Integer>[] args, boolean valid) throws IOException{
        Triple<String, FileReader, Integer>[] withReaders = new Triple[args.length];
        for (int i = 0 ; i < args.length ; i++){
            withReaders[i] = Triple.of(args[i].getLeft(),
                    new FileReader("resources\\test\\" + args[i].getMiddle(), 
                        Charset.forName("utf-8")),
                    args[i].getRight());
        }
        return Stream.of(withReaders).map(t -> new PersonVerifierArgument(t.getLeft(),
            new CsvToBeanBuilder<Person>(t.getMiddle())
            .withType(Person.class).withOrderedResults(false).withVerifier(Person.VERIFIER).build(),
            valid, t.getRight()));
    }
    
    @TestFactory
    Stream<DynamicNode> verifier_validFile(){
        try{
            return test(validArgumentsSupplier(), "verifier (validFile)", args -> {
                List<Person> persons = args.parser.parse();
                //Check that no entry was ignored.
                Assertions.assertEquals(args.numberOfEntries, persons.size());
            });
        } catch (IOException e){
            return Stream.of(dynamicTest("verifier (validFile)", () -> Assertions.fail(e)));
        }
    }
    
    @TestFactory
    Stream<DynamicNode> verifier_invalidFile(){
        try{
            return test(invalidArgumentsSupplier(), "verifier (invalidFile)", args -> {
                Exception e = Assertions.assertThrows(RuntimeException.class, () -> 
                    args.parser.parse());
                Assertions.assertEquals(CsvConstraintViolationException.class, 
                        e.getCause().getClass());
            });
        } catch (IOException e){
            return Stream.of(dynamicTest("verifier (validFile)", () -> Assertions.fail(e)));
        }
    }
}

class PersonVerifierArgument{
    final String name;
    final CsvToBean<Person> parser;
    final boolean validFile;
    final int numberOfEntries;
    
    PersonVerifierArgument(String name, CsvToBean<Person> parser, boolean validFile, 
        int numberOfEntries){
        this.name = name;
        this.parser = parser;
        this.validFile = validFile;
        this.numberOfEntries = numberOfEntries;
    }
    
    @Override
    public String toString(){
        return this.name;
    }
}
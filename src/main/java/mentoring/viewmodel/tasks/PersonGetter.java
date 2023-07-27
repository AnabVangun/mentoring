package mentoring.viewmodel.tasks;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import javafx.concurrent.Task;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.io.PersonFileParser;
import mentoring.viewmodel.RunConfiguration;
import mentoring.viewmodel.datastructure.PersonListViewModel;
import mentoring.viewmodel.datastructure.PersonType;

/**
 * Class used to get persons and update an input view model.
 */
public class PersonGetter extends Task<List<Person>> {
    private final PersonListViewModel resultVM;
    private final RunConfiguration data;
    private final PersonType type;
    private final ReaderGenerator supplier;
    private PersonConfiguration personConfiguration;
    private List<Person> persons;

    /**
     * Initialise a {@code PersonGetter} object.
     * @param resultVM the view model that will be updated when the task completes
     * @param data where to get data from
     * @param type type of person to get
     * @param supplier to supply the reader used to read the data
     */
    public PersonGetter(PersonListViewModel resultVM, RunConfiguration data, PersonType type, 
            ReaderGenerator supplier) {
        Objects.requireNonNull(data);
        Objects.requireNonNull(resultVM);
        Objects.requireNonNull(type);
        Objects.requireNonNull(supplier);
        this.data = data;
        this.resultVM = resultVM;
        this.type = type;
        this.supplier = supplier;
    }

    @Override
    protected List<Person> call() throws Exception {
        personConfiguration = getPersonConfiguration(data, type);
        persons = getPersons(data, supplier, personConfiguration, type);
        return persons;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        resultVM.update(personConfiguration, persons);
    }

    private static List<Person> getPersons(RunConfiguration data, ReaderGenerator supplier,
            PersonConfiguration configuration, PersonType type) throws IOException {
        List<Person> result = parsePersonList(configuration, supplier, 
                type.getFilePathFromConfiguration(data));
        return result;
    }

    private static PersonConfiguration getPersonConfiguration(RunConfiguration data, 
            PersonType type) throws IOException {
        return switch (type) {
            case MENTEE -> data.getMenteeConfiguration();
            case MENTOR -> data.getMentorConfiguration();
        };
    }

    private static List<Person> parsePersonList(PersonConfiguration personConfiguration, 
            ReaderGenerator supplier, String filePath) throws IOException {
        try (final Reader personFile = supplier.generate(filePath)) {
            return new PersonFileParser(personConfiguration).parse(personFile);
        }
    }
    
    /**
     * Represents an operation that accepts a single input argument and returns a {@link Reader}. 
     * A typical implementation would return a FileReader using the input as a file path. 
     */
    @FunctionalInterface
    public static interface ReaderGenerator {
        Reader generate(String input) throws IOException;
    }
}
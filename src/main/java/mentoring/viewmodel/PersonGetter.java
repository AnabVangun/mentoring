package mentoring.viewmodel;

import mentoring.viewmodel.datastructure.PersonType;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import javafx.concurrent.Task;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.Person;
import mentoring.io.PersonFileParser;
import mentoring.viewmodel.datastructure.PersonListViewModel;

/**
 * Class used to get persons and update an input view model.
 */
class PersonGetter extends Task<List<Person>> {
    //TODO test
    private final PersonListViewModel resultVM;
    private final RunConfiguration data;
    private final PersonType type;
    private PersonConfiguration personConfiguration;
    private List<Person> persons;

    /**
     * Initialise a {@code PersonGetter} object.
     * @param resultVM the view model that will be updated when the task completes
     * @param data where to get data from
     * @param type type of person to get
     */
    PersonGetter(PersonListViewModel resultVM, RunConfiguration data, PersonType type) {
        this.data = data;
        this.resultVM = resultVM;
        this.type = type;
    }

    @Override
    protected List<Person> call() throws Exception {
        try {
            personConfiguration = getPersonConfiguration(data, type);
            persons = getPersons(data, personConfiguration, type);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO mark task as failed
        }
        return persons;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        resultVM.update(personConfiguration, persons);
    }

    private static List<Person> getPersons(RunConfiguration data, 
            PersonConfiguration configuration, PersonType type) throws IOException {
        List<Person> result = parsePersonList(configuration, type.getFilePathFromConfiguration(data));
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
            String personFilePath) throws IOException {
        try (final FileReader personFile = 
                new FileReader(personFilePath, Charset.forName("utf-8"))) {
            return new PersonFileParser(personConfiguration).parse(personFile);
        }
    }
}

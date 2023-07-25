package mentoring.viewmodel;

import java.io.IOException;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.PersonConfiguration;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;

/**
 * TODO document when configuration on the fly is implemented.
 */
public interface RunConfiguration {
    String getMenteeFilePath();
    PersonConfiguration getMenteeConfiguration() throws IOException;
    Person getDefaultMentee();
    String getMentorFilePath();
    PersonConfiguration getMentorConfiguration() throws IOException;
    Person getDefaultMentor();
    CriteriaConfiguration<Person, Person> getCriteriaConfiguration();
    ResultConfiguration<Person, Person> getResultConfiguration() throws IOException;
    String getDestinationFilePath();
}

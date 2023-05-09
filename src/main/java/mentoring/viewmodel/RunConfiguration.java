package mentoring.viewmodel;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.PersonConfiguration;
import mentoring.configuration.PojoCriteriaConfiguration;
import mentoring.configuration.PojoPersonConfiguration;
import mentoring.configuration.PojoResultConfiguration;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.io.Parser;
import mentoring.io.PersonConfigurationParser;
import mentoring.io.ResultConfigurationParser;
import mentoring.io.datareader.YamlReader;

/**
 * Standard configurations. This class will be rewritten when the configuration view is made.
 */
public enum RunConfiguration {
    TEST,
    TEST_CONFIGURATION_FILE,
    REAL2023;
    
    public String getMenteeFilePath(){
        return switch(this){
            case TEST -> "resources\\main\\Filleul_Trivial.csv";
            case TEST_CONFIGURATION_FILE -> "resources\\main\\Filleul_Trivial.csv";
            case REAL2023 -> "..\\..\\..\\AX\\2023_Mentoring\\Adapter\\20221016_new_eleves.csv";
        };
    }
    
    public PersonConfiguration getMenteeConfiguration() throws IOException{
        return switch(this){
            case TEST -> PojoPersonConfiguration.TEST_CONFIGURATION.getConfiguration();
            case TEST_CONFIGURATION_FILE -> parseConfigurationFile(
                    new PersonConfigurationParser(new YamlReader()), 
                    "resources\\main\\testPersonConfiguration.yaml");
            case REAL2023 -> 
                PojoPersonConfiguration.MENTEE_CONFIGURATION_2023_DATA.getConfiguration();
        };
    }
    
    public Person getDefaultMentee() {
        return new PersonBuilder().withProperty("Email", "").withFullName("PAS DE MENTORÉ").build();
    }
    
    public String getMentorFilePath(){
        return switch(this){
            case TEST -> "resources\\main\\Mentor_Trivial.csv";
            case TEST_CONFIGURATION_FILE -> "resources\\main\\Mentor_Trivial.csv";
            case REAL2023 -> "..\\..\\..\\AX\\2023_Mentoring\\Adapter\\20221016_new_mentors.csv";
        };
    }
    
    public PersonConfiguration getMentorConfiguration() throws IOException{
        return switch(this){
            case TEST -> PojoPersonConfiguration.TEST_CONFIGURATION.getConfiguration();
            case TEST_CONFIGURATION_FILE -> parseConfigurationFile(
                    new PersonConfigurationParser(new YamlReader()), 
                    "resources\\main\\testPersonConfiguration.yaml");
            case REAL2023 -> PojoPersonConfiguration.MENTOR_CONFIGURATION_2023_DATA
                        .getConfiguration();
        };
    }
    
    public Person getDefaultMentor() {
        return new PersonBuilder().withProperty("Email", "").withFullName("PAS DE MENTOR").build();
    }
    
    public CriteriaConfiguration<Person, Person> getCriteriaConfiguration(){
        return switch(this){
            case TEST -> PojoCriteriaConfiguration.CRITERIA_CONFIGURATION;
            case TEST_CONFIGURATION_FILE -> PojoCriteriaConfiguration.CRITERIA_CONFIGURATION;
            case REAL2023 -> PojoCriteriaConfiguration.CRITERIA_CONFIGURATION_2023_DATA;
        };
    }
    
    public ResultConfiguration<Person, Person> getResultConfiguration() throws IOException {
        return switch(this){
            case TEST -> PojoResultConfiguration.NAMES_AND_SCORE.getConfiguration();
            case TEST_CONFIGURATION_FILE -> parseConfigurationFile(
                    new ResultConfigurationParser(new YamlReader()), 
                        "resources\\main\\testResultConfiguration.yaml");
            case REAL2023 -> PojoResultConfiguration.NAMES_EMAILS_AND_SCORE.getConfiguration();
        };
    }
    
    private static <T> T parseConfigurationFile(Parser<T> parser, String filePath) throws IOException{
        try (FileReader configurationFile = new FileReader(filePath, Charset.forName("utf-8"))){
            return parser.parse(configurationFile);
        }
    }
    
    public String getDestinationFilePath(){
        return switch(this){
            case TEST, TEST_CONFIGURATION_FILE -> "resources\\main\\Results_Trivial.csv";
            case REAL2023 -> "..\\..\\..\\AX\\2023_Mentoring\\Adapter\\20221016_result.csv";
        };
    }
}
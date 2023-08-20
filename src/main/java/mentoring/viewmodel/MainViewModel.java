package mentoring.viewmodel;

import mentoring.viewmodel.tasks.PersonGetterTask;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.inject.Inject;
import mentoring.concurrency.ConcurrencyHandler;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.PersonConfiguration;
import mentoring.configuration.PojoCriteriaConfiguration;
import mentoring.configuration.PojoPersonConfiguration;
import mentoring.configuration.PojoResultConfiguration;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.io.PersonConfigurationParser;
import mentoring.io.PersonFileParser;
import mentoring.io.ResultConfigurationParser;
import mentoring.io.datareader.YamlReader;
import mentoring.viewmodel.base.BoundableConfigurationPickerViewModel;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
import mentoring.viewmodel.base.FilePickerViewModel;
import mentoring.viewmodel.datastructure.PersonListViewModel;
import mentoring.viewmodel.datastructure.PersonMatchViewModel;
import mentoring.viewmodel.datastructure.PersonMatchesViewModel;
import mentoring.viewmodel.datastructure.PersonViewModel;
import mentoring.viewmodel.tasks.ConfigurationGetterTask;
import mentoring.viewmodel.tasks.MatchExportTask;
import mentoring.viewmodel.tasks.MultipleMatchTask;
import mentoring.viewmodel.tasks.SingleMatchRemovalTask;
import mentoring.viewmodel.tasks.SingleMatchTask;
import mentoring.viewmodel.base.function.FileParser;
import mentoring.viewmodel.datastructure.ForbiddenMatchListViewModel;
import mentoring.viewmodel.datastructure.PersonType;
import mentoring.viewmodel.tasks.AbstractTask;
import mentoring.viewmodel.tasks.ForbiddenMatchTask;
import org.apache.commons.lang3.tuple.Pair;

/**
 * ViewModel responsible for handling the main window of the application.
 */
public class MainViewModel {
    /*
    TODO: handle concurrency. For each subtask, test and document method
        make sure that a global match cannot be run while a single match is running
        make sure that only one global match can be run at the same time
        make sure that if several single matches are running, they only handle different persons
    */
    private final ConcurrencyHandler matchMaker;
    
    private final EnumMap<PersonType, ConfigurationPickerViewModel<PersonConfiguration>> 
            personConfigurations = new EnumMap<>(PersonType.class);
    private final ConfigurationPickerViewModel<CriteriaConfiguration<Person, Person>> matchConfiguration;
    private final ConfigurationPickerViewModel<ResultConfiguration<Person, Person>> resultConfiguration;
    private final ConfigurationPickerViewModel<ResultConfiguration<Person, Person>> exportConfiguration;
    private final EnumMap<PersonType, FilePickerViewModel<List<Person>>> personPickers =
            new EnumMap<>(PersonType.class);
    private final ForbiddenMatchListViewModel extraForbiddenMatches = 
            new ForbiddenMatchListViewModel();
    
    /**
     * Create a new {@code MainViewModel}.
     * @param executor Executor service that will receive the task to run the application.
     */
    @Inject
    MainViewModel(ConcurrencyHandler concurrencyHandler){
        matchMaker = concurrencyHandler;
        matchConfiguration = forgeMatchConfigurationPickerViewModel();
        resultConfiguration = forgeResultConfigurationPickerViewModel();
        exportConfiguration = new BoundableConfigurationPickerViewModel<>(resultConfiguration);
        for(PersonType type : PersonType.values()){
            personConfigurations.put(type, forgePersonConfigurationPickerViewModel());
            personPickers.put(type, forgePersonListPickerViewModel(type));
        }
    }
    
    /**
     * Get a list of persons.
     * @param resultVM the ViewModel to update with the results
     * @param type of person to load
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> getPersons(PersonListViewModel resultVM, PersonType type){
        return matchMaker.submit(new PersonGetterTask(resultVM, personPickers.get(type), 
                personConfigurations.get(type)));
    }
    
    /**
     * Run the application: get the relevant data, and make matches.
     * @param menteeVM the ViewModel containing the mentees
     * @param mentorVM the ViewModel containing the mentors
     * @param resultVM the ViewModel to update with the results
     * @param excludedMatchesVM the optional ViewModel containing matches that should be excluded
     *      from the match-making process
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> makeMatches(PersonListViewModel menteeVM, PersonListViewModel mentorVM,
            PersonMatchesViewModel resultVM, PersonMatchesViewModel excludedMatchesVM){
        return matchMaker.submit(new MultipleMatchTask(resultVM, excludedMatchesVM, 
                matchConfiguration, extraForbiddenMatches,
                menteeVM.getUnderlyingData(),
                mentorVM.getUnderlyingData()));
    }
    
    /**
     * Create a match between two selected persons.
     * @param menteeVM the ViewModel containing the mentee
     * @param mentorVM the ViewModel containing the mentor
     * @param resultVM the ViewModel to update with the results
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> makeSingleMatch(PersonViewModel menteeVM, PersonViewModel mentorVM,
            PersonMatchesViewModel resultVM){
        return matchMaker.submit(new SingleMatchTask(resultVM, matchConfiguration, 
                menteeVM.getPerson(), 
                mentorVM.getPerson()));
    }
    
    /**
     * Remove a match between two persons.
     * @param toRemove the ViewModel containing the match to remove
     * @param resultVM the ViewModel to update
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> removeSingleMatch(PersonMatchViewModel toRemove, PersonMatchesViewModel resultVM){
        return matchMaker.submit(new SingleMatchRemovalTask(resultVM, toRemove));
    }
    
    /**
     * Export the current matches in a file.
     * @param outputFile the destination file
     * @param callback the method to call when the task has run
     * @param toExportWithHeader a mandatory first ViewModel containing matches to export
     * @param toExport optional additional ViewModels containing the matches to export
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> exportMatches(File outputFile, 
            AbstractTask.TaskCompletionCallback<Void, MatchExportTask> callback,
            PersonMatchesViewModel toExportWithHeader, PersonMatchesViewModel... toExport){
        return matchMaker.submit(new MatchExportTask(
                () -> new PrintWriter(outputFile, Charset.forName("utf-8")), 
                callback, exportConfiguration, 
                toExportWithHeader, toExport));
    }
    
    public Future<?> addForbiddenMatch(PersonViewModel menteeVM, PersonViewModel mentorVM){
        return matchMaker.submit(new ForbiddenMatchTask(extraForbiddenMatches, 
                menteeVM.getPerson(), mentorVM.getPerson()));
    }
    
    /**
     * Get a {@link ResultConfiguration}.
     * @param resultVMs the ViewModels to update with the configuration
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> getResultConfiguration(List<? extends PersonMatchesViewModel> resultVMs) {
        return matchMaker.submit(new ConfigurationGetterTask<>(getResultConfiguration(), resultVMs));
    }
    
    //TODO document
    public ConfigurationPickerViewModel<ResultConfiguration<Person,Person>> getResultConfiguration(){
        return resultConfiguration;
    }
    
    //TODO document
    public ConfigurationPickerViewModel<ResultConfiguration<Person,Person>> getExportConfiguration(){
        return exportConfiguration;
    }
    
    //TODO document
    public FilePickerViewModel<List<Person>> getPersonPicker(PersonType type){
        return personPickers.get(type);
    }
    
    //TODO document
    public ConfigurationPickerViewModel<PersonConfiguration> getPersonConfiguration(PersonType type){
        return personConfigurations.get(type);
    }
    
    //TODO document
    public ConfigurationPickerViewModel<CriteriaConfiguration<Person,Person>> getMatchConfiguration(){
        return matchConfiguration;
    }
    
    //TODO document
    public ForbiddenMatchListViewModel getForbiddenMatches(){
        return extraForbiddenMatches;
    }
    
    private ConfigurationPickerViewModel<ResultConfiguration<Person, Person>> 
            forgeResultConfigurationPickerViewModel(){
        //TODO refactor forgeXXPickerViewModel to emphasize structure
        ResultConfiguration<Person, Person> configuration = 
                PojoResultConfiguration.NAMES_AND_SCORE.getConfiguration();
        String defaultPath = "";
        ConfigurationPickerViewModel.ConfigurationType type = 
                ConfigurationPickerViewModel.ConfigurationType.KNOWN;
        FileParser<ResultConfiguration<Person, Person>> parser = file -> {
            try (FileReader reader = new FileReader(file, Charset.forName("utf-8"))){
                return new ResultConfigurationParser(new YamlReader()).parse(reader);
            }
        };
        List<ResultConfiguration<Person,Person>> values = 
                Arrays.stream(PojoResultConfiguration.values())
                        .map(config -> config.getConfiguration())
                        .collect(Collectors.toList());
        List<Pair<String, List<String>>> extensions = List.of(
                Pair.of("YAML files", List.of("*.yaml")),
                Pair.of("All files", List.of("*.*")));
        FilePickerViewModel<ResultConfiguration<Person, Person>> filePicker = 
                new FilePickerViewModel<>(defaultPath, parser, extensions);
        return new ConfigurationPickerViewModel<>(configuration, values, filePicker, type);
    }
            
    private ConfigurationPickerViewModel<PersonConfiguration> forgePersonConfigurationPickerViewModel(){
        PersonConfiguration configuration = 
                PojoPersonConfiguration.TEST_CONFIGURATION.getConfiguration();
        String defaultPath = "";
        ConfigurationPickerViewModel.ConfigurationType type = 
                ConfigurationPickerViewModel.ConfigurationType.KNOWN;
        FileParser<PersonConfiguration> parser = file -> {
            try (FileReader reader = new FileReader(file, Charset.forName("utf-8"))){
                return new PersonConfigurationParser(new YamlReader()).parse(reader);
            }
        };
        List<PersonConfiguration> values = 
                Arrays.stream(PojoPersonConfiguration.values())
                        .map(config -> config.getConfiguration())
                        .collect(Collectors.toList());
        List<Pair<String, List<String>>> extensions = List.of(
                Pair.of("YAML files", List.of("*.yaml")),
                Pair.of("All files", List.of("*.*")));
        FilePickerViewModel<PersonConfiguration> filePicker = 
                new FilePickerViewModel<>(defaultPath, parser, extensions);
        return new ConfigurationPickerViewModel<>(configuration, values, filePicker, type);
    }
    
    private ConfigurationPickerViewModel<CriteriaConfiguration<Person,Person>> 
            forgeMatchConfigurationPickerViewModel(){
        CriteriaConfiguration<Person, Person> configuration = 
                PojoCriteriaConfiguration.CRITERIA_CONFIGURATION;
        String defaultPath = "";
        ConfigurationPickerViewModel.ConfigurationType type = 
                ConfigurationPickerViewModel.ConfigurationType.KNOWN;
        FileParser<CriteriaConfiguration<Person, Person>> parser = file -> {
            throw new UnsupportedOperationException("not implemented yet");
        };
        List<CriteriaConfiguration<Person, Person>> values = 
                List.of(PojoCriteriaConfiguration.CRITERIA_CONFIGURATION, 
                        PojoCriteriaConfiguration.CRITERIA_CONFIGURATION_2023_DATA);
        List<Pair<String, List<String>>> extensions = List.of(
                Pair.of("YAML files", List.of("*.yaml")),
                Pair.of("All files", List.of("*.*")));
        FilePickerViewModel<CriteriaConfiguration<Person, Person>> filePicker = 
                new FilePickerViewModel<>(defaultPath, parser, extensions);
        return new ConfigurationPickerViewModel<>(configuration, values, filePicker, type);
    }
    
    private FilePickerViewModel<List<Person>> forgePersonListPickerViewModel(PersonType type){
        String defaultPath = "";
        FileParser<List<Person>> parser = file -> {
            try (FileReader reader = new FileReader(file, Charset.forName("utf-8"))){
                return new PersonFileParser(personConfigurations.get(type).getConfiguration())
                        .parse(reader);
            }
        };
        List<Pair<String, List<String>>> extensions = List.of(
                Pair.of("CSV files", List.of("*.csv")),
                Pair.of("All files", List.of("*.*")));
        return new FilePickerViewModel<>(defaultPath, parser, extensions);
    }
}

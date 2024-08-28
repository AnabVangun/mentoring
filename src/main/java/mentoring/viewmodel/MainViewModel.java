package mentoring.viewmodel;

import mentoring.viewmodel.tasks.PersonGetterTask;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import javafx.concurrent.Task;
import javax.inject.Inject;
import mentoring.concurrency.ConcurrencyHandler;
import mentoring.configuration.Configuration;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.configuration.PersonConfiguration;
import mentoring.configuration.PojoCriteriaConfiguration;
import mentoring.configuration.PojoPersonConfiguration;
import mentoring.configuration.PojoResultConfiguration;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.datastructure.PersonBuilder;
import mentoring.io.PersonConfigurationParser;
import mentoring.io.PersonFileParser;
import mentoring.io.ResultConfigurationParser;
import mentoring.io.datareader.YamlReader;
import mentoring.match.MatchesBuilderHandler;
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
import mentoring.viewmodel.datastructure.ForbiddenMatchViewModel;
import mentoring.viewmodel.datastructure.PersonType;
import mentoring.viewmodel.tasks.AbstractTask;
import mentoring.viewmodel.tasks.ForbiddenMatchRemovalTask;
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
    private final ConcurrencyHandler taskHandler;
    
    private final EnumMap<PersonType, ConfigurationPickerViewModel<PersonConfiguration>> 
            personConfigurations = new EnumMap<>(PersonType.class);
    private final ConfigurationPickerViewModel<CriteriaConfiguration<Person, Person>> matchConfiguration;
    private final ConfigurationPickerViewModel<ResultConfiguration<Person, Person>> resultConfiguration;
    private final ConfigurationPickerViewModel<ResultConfiguration<Person, Person>> exportConfiguration;
    private final EnumMap<PersonType, FilePickerViewModel<List<Person>>> personPickers =
            new EnumMap<>(PersonType.class);
    private final ForbiddenMatchListViewModel extraForbiddenMatches = 
            new ForbiddenMatchListViewModel();
    
    private final static List<Pair<String, List<String>>> YAML_EXTENSIONS = List.of(
                Pair.of("YAML files", List.of("*.yaml")),
                Pair.of("All files", List.of("*.*")));
    private final static List<Pair<String, List<String>>> CSV_EXTENSIONS = List.of(
                Pair.of("CSV files", List.of("*.csv")),
                Pair.of("All files", List.of("*.*")));
    
    //FIXME make sure that matchesBuilderHandler receives the match configuration
    //TODO check how forbidden matches are handled: maybe in two different ways?
    private final MatchesBuilderHandler<Person, Person> matchesBuilderHandler = 
            new MatchesBuilderHandler<>();
    
    /**
     * Create a new {@code MainViewModel}.
     * @param executor Executor service that will receive the task to run the application.
     */
    @Inject
    MainViewModel(ConcurrencyHandler concurrencyHandler){
        taskHandler = concurrencyHandler;
        matchConfiguration = forgeMatchConfigurationPickerViewModel();
        resultConfiguration = forgeResultConfigurationPickerViewModel();
        exportConfiguration = new BoundableConfigurationPickerViewModel<>(resultConfiguration);
        for(PersonType type : PersonType.values()){
            personConfigurations.put(type, forgePersonConfigurationPickerViewModel());
            personPickers.put(type, forgePersonListPickerViewModel(type));
        }
        /*FIXME: defaultMentee and defaultMentor should be configured somewhere
        (probably in result configuration).
        Additionally, this is really ugly. 
        Maybe a DummyFuture as used in tests would be more appropriate?
        */
        Person defaultMentee = new PersonBuilder().withProperty("Email", "")
                .withFullName("PAS DE MENTORÉ").build();
        Person defaultMentor = new PersonBuilder().withProperty("Email", "")
                .withFullName("PAS DE MENTOR").build();
        FutureTask<Person> defaultMenteeSupplier = new FutureTask<>(() -> defaultMentee);
        defaultMenteeSupplier.run();
        FutureTask<Person> defaultMentorSupplier = new FutureTask<>(() -> defaultMentor);
        defaultMentorSupplier.run();
        matchesBuilderHandler.setPlaceholderPersonsSupplier(defaultMenteeSupplier, 
                defaultMentorSupplier);
    }
    
    /**
     * Get a list of persons.
     * @param resultVM the ViewModel to update with the results
     * @param type of person to load
     * @param callback the method to call when the task has run
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> getPersons(PersonListViewModel resultVM, PersonType type,
            AbstractTask.TaskCompletionCallback<? super List<Person>> callback){
        //TODO add to callback to signal that person type has been loaded
        Task<List<Person>> result = new PersonGetterTask(resultVM, personPickers.get(type), 
                        personConfigurations.get(type), callback);
        taskHandler.submit(result);
        switch(type){
            case MENTEE -> matchesBuilderHandler.setMenteesSupplier(result);
            case MENTOR -> matchesBuilderHandler.setMentorsSupplier(result);
        }
        return result;
    }
    
    /**
     * Run the application: get the relevant data, and make matches.
     * @param menteeVM the ViewModel containing the mentees
     * @param mentorVM the ViewModel containing the mentors
     * @param resultVM the ViewModel to update with the results
     * @param excludedMatchesVM the optional ViewModel containing matches that should be excluded
     *      from the match-making process
     * @param callback the method to call when the task has run
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> makeMatches(PersonListViewModel menteeVM, PersonListViewModel mentorVM,
            PersonMatchesViewModel resultVM, PersonMatchesViewModel excludedMatchesVM,
            AbstractTask.TaskCompletionCallback<? super Void> callback){
        //FIXME ugly hack to supply the configuration to matchesBuilderHandler.
        FutureTask<CriteriaConfiguration<Person, Person>> configuration = new FutureTask<>(() -> 
                matchConfiguration.getConfiguration());
        taskHandler.submit(configuration);
        matchesBuilderHandler.setCriteriaSupplier(configuration);
        return taskHandler.submit(new MultipleMatchTask(resultVM, excludedMatchesVM, 
                matchesBuilderHandler,
                menteeVM.getUnderlyingData(),
                mentorVM.getUnderlyingData(), callback));
    }
    
    /**
     * Create a match between two selected persons.
     * @param menteeVM the ViewModel containing the mentee
     * @param mentorVM the ViewModel containing the mentor
     * @param resultVM the ViewModel to update with the results
     * @param callback the method to call when the task has run
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> makeSingleMatch(PersonViewModel menteeVM, PersonViewModel mentorVM,
            PersonMatchesViewModel resultVM, AbstractTask.TaskCompletionCallback<Object> callback){
        return taskHandler.submit(new SingleMatchTask(resultVM, matchConfiguration, 
                menteeVM.getData(), mentorVM.getData(), callback));
    }
    
    /**
     * Remove a match between two persons.
     * @param toRemove the ViewModel containing the match to remove
     * @param resultVM the ViewModel to update
     * @param callback the method to call when the task has run
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> removeSingleMatch(PersonMatchViewModel toRemove, 
            PersonMatchesViewModel resultVM, 
            AbstractTask.TaskCompletionCallback<? super Void> callback){
        return taskHandler.submit(new SingleMatchRemovalTask(resultVM, toRemove, callback));
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
            AbstractTask.TaskCompletionCallback<? super Void> callback,
            PersonMatchesViewModel toExportWithHeader, PersonMatchesViewModel... toExport){
        return taskHandler.submit(new MatchExportTask(
                () -> new PrintWriter(outputFile, Charset.forName("utf-8")), 
                callback, exportConfiguration, 
                toExportWithHeader, toExport));
    }
    
    /**
     * Declare a match between a mentee and a mentor as forbidden.
     * @param menteeVM the ViewModel encapsulating the mentee that must not be matched
     * @param mentorVM the ViewModel encapsulating the mentor that must not be matched
     * @param callback the method to call when the task has run
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> addForbiddenMatch(PersonViewModel menteeVM, PersonViewModel mentorVM,
            AbstractTask.TaskCompletionCallback<? super Void> callback){
        //TODO refactor: forbidding a match should not need to be done twice
        //FIXME currently, forbidding a match make both persons unmatchable.
        return taskHandler.submit(new ForbiddenMatchTask(extraForbiddenMatches, 
                menteeVM.getData(), mentorVM.getData(), matchesBuilderHandler, callback));
    }
    
    /**
     * Declare a forbidden match between a mentee and a mentor as allowed.
     * @param toRemove the ViewModel encapsulating the forbidden match to allow
     * @param callback the method to call when the task has run
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> removeForbiddenMatch(ForbiddenMatchViewModel toRemove,
            AbstractTask.TaskCompletionCallback<? super Void> callback){
        //TODO refactor: forbidding a match should not need to be done twice
        return taskHandler.submit(new ForbiddenMatchRemovalTask(extraForbiddenMatches, 
                toRemove, matchesBuilderHandler,
                callback));
    }
    
    /**
     * Get a {@link ResultConfiguration}.
     * @param resultVMs the ViewModels to update with the configuration
     * @param callback the method to call when the task has run
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> getResultConfiguration(List<? extends PersonMatchesViewModel> resultVMs, 
            AbstractTask.TaskCompletionCallback<Object> callback) {
        return taskHandler.submit(new ConfigurationGetterTask<>(getResultConfiguration(), 
                resultVMs, callback));
    }
    
    /**
     * Get the ViewModel responsible for picking the {@link ResultConfiguration} used to display 
     * the matches.
     * @return a ConfigurationPickerViewModel specialised for ResultConfiguration
     */
    public ConfigurationPickerViewModel<ResultConfiguration<Person,Person>> getResultConfiguration(){
        return resultConfiguration;
    }
    
    /**
     * Get the ViewModel responsible for picking the {@link ResultConfiguration} used to export
     * the matches.
     * @return a ConfigurationPickerViewModel specialised for ResultConfiguration
     */
    public ConfigurationPickerViewModel<ResultConfiguration<Person,Person>> getExportConfiguration(){
        return exportConfiguration;
    }
    
    /**
     * Get the ViewModel responsible for picking a list of {@link Person}.
     * @param type of persons selected by the ViewModel
     * @return a FilePickerViewModel specialised for lists of Person
     */
    public FilePickerViewModel<List<Person>> getPersonPicker(PersonType type){
        return personPickers.get(type);
    }
    
    /**
     * Get the ViewModel responsible for picking the {@link PersonConfiguration}.
     * @param type of persons selected by the ViewModel
     * @return a ConfigurationPickerViewModel specialised for PersonConfiguration
     */
    public ConfigurationPickerViewModel<PersonConfiguration> getPersonConfiguration(PersonType type){
        return personConfigurations.get(type);
    }
    
    /**
     * Get the ViewModel responsible for picking the {@link CriteriaConfiguration}.
     * @return a ConfigurationPickerViewModel specialised for CriteriaConfiguration
     */
    public ConfigurationPickerViewModel<CriteriaConfiguration<Person,Person>> getMatchConfiguration(){
        return matchConfiguration;
    }
    
    /**
     * Get the ViewModel encapsulating the list of matches declared as forbidden.
     * @return a ViewModel encapsulating forbidden matches
     */
    public ForbiddenMatchListViewModel getForbiddenMatches(){
        return extraForbiddenMatches;
    }
    
    private ConfigurationPickerViewModel<ResultConfiguration<Person, Person>> 
            forgeResultConfigurationPickerViewModel(){
        ResultConfiguration<Person, Person> configuration = 
                PojoResultConfiguration.NAMES_AND_SCORE.getConfiguration();
        List<ResultConfiguration<Person,Person>> values = 
                Arrays.stream(PojoResultConfiguration.values())
                        .map(config -> config.getConfiguration())
                        .collect(Collectors.toList());
        IOFunction<ResultConfiguration<Person, Person>> parser = reader ->
                new ResultConfigurationParser(new YamlReader()).parse(reader);
        FilePickerViewModel<ResultConfiguration<Person, Person>> filePicker = 
                forgeFilePickerViewModel(parser, YAML_EXTENSIONS);
        return forgeConfigurationPickerViewModel(configuration, values, filePicker);
    }
            
    private ConfigurationPickerViewModel<PersonConfiguration> forgePersonConfigurationPickerViewModel(){
        PersonConfiguration configuration = 
                PojoPersonConfiguration.TEST_CONFIGURATION.getConfiguration();
        List<PersonConfiguration> values = 
                Arrays.stream(PojoPersonConfiguration.values())
                        .map(config -> config.getConfiguration())
                        .collect(Collectors.toList());
        IOFunction<PersonConfiguration> parser = reader -> 
                new PersonConfigurationParser(new YamlReader()).parse(reader);
        FilePickerViewModel<PersonConfiguration> filePicker = 
                forgeFilePickerViewModel(parser, YAML_EXTENSIONS);
        return forgeConfigurationPickerViewModel(configuration, values, filePicker);
    }
    
    private ConfigurationPickerViewModel<CriteriaConfiguration<Person,Person>> 
            forgeMatchConfigurationPickerViewModel(){
        CriteriaConfiguration<Person, Person> configuration = 
                PojoCriteriaConfiguration.CRITERIA_CONFIGURATION;
        List<CriteriaConfiguration<Person, Person>> values = 
                List.of(PojoCriteriaConfiguration.CRITERIA_CONFIGURATION, 
                        PojoCriteriaConfiguration.CRITERIA_CONFIGURATION_2023_DATA);
        IOFunction<CriteriaConfiguration<Person, Person>> parser = file -> {
            throw new UnsupportedOperationException("not implemented yet");
        };
        FilePickerViewModel<CriteriaConfiguration<Person, Person>> filePicker = 
                forgeFilePickerViewModel(parser, YAML_EXTENSIONS);
        return forgeConfigurationPickerViewModel(configuration, values, filePicker);
    }
    
    private <T extends Configuration<T>> ConfigurationPickerViewModel<T> forgeConfigurationPickerViewModel(
            T initialValue, List<T> knownValues, FilePickerViewModel<T> filePicker){
        return new ConfigurationPickerViewModel<>(initialValue, knownValues, filePicker, 
                ConfigurationPickerViewModel.ConfigurationType.KNOWN);
    }
            
    private FilePickerViewModel<List<Person>> forgePersonListPickerViewModel(PersonType type){
        IOFunction<List<Person>> parser = reader -> 
                new PersonFileParser(personConfigurations.get(type).getConfiguration())
                        .parse(reader);
        return forgeFilePickerViewModel(parser, CSV_EXTENSIONS);
    }
    
    private <T> FilePickerViewModel<T> forgeFilePickerViewModel(IOFunction<T> parser, 
            List<Pair<String, List<String>>> extensions) {
        String defaultPath = "";
        FileParser<T> actualParser = file -> {
            try (FileReader reader = new FileReader(file, Charset.forName("utf-8"))){
                return parser.apply(reader);
            }
        };
        return new FilePickerViewModel<>(defaultPath, actualParser, extensions);
    }
    
    @FunctionalInterface
    private static interface IOFunction<T> {
        public T apply(FileReader reader) throws IOException;
    }
}

package mentoring.viewmodel;

import mentoring.viewmodel.tasks.PersonGetter;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import mentoring.viewmodel.datastructure.PersonType;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.inject.Inject;
import mentoring.concurrency.ConcurrencyHandler;
import mentoring.configuration.PojoResultConfiguration;
import mentoring.configuration.ResultConfiguration;
import mentoring.datastructure.Person;
import mentoring.io.ResultConfigurationParser;
import mentoring.io.datareader.YamlReader;
import mentoring.viewmodel.base.ConfigurationPickerViewModel;
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
    
    /**
     * Create a new {@code MainViewModel}.
     * @param executor Executor service that will receive the task to run the application.
     */
    @Inject
    MainViewModel(ConcurrencyHandler concurrencyHandler){
        this.matchMaker = concurrencyHandler;
    }
    
    /**
     * Get a list of persons.
     * @param resultVM the ViewModel to update with the results
     * @param data how to get the person data and configuration
     * @param type the type of persons to get
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> getPersons(PersonListViewModel resultVM, RunConfiguration data,
            PersonType type){
        return matchMaker.submit(new PersonGetter(resultVM, data, type, 
                fileName -> new FileReader(fileName, Charset.forName("utf-8"))));
    }
    
    /**
     * Run the application: get the relevant data, and make matches.
     * @param menteeVM the ViewModel containing the mentees
     * @param mentorVM the ViewModel containing the mentors
     * @param resultVM the ViewModel to update with the results
     * @param excludedMatchesVM the optional ViewModel containing matches that should be excluded
     *      from the match-making process
     * @param data how to get the configuration data
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> makeMatches(PersonListViewModel menteeVM, PersonListViewModel mentorVM,
            PersonMatchesViewModel resultVM, PersonMatchesViewModel excludedMatchesVM, RunConfiguration data){
        return matchMaker.submit(new MultipleMatchTask(resultVM, excludedMatchesVM, data,
                menteeVM.getUnderlyingData(),
                mentorVM.getUnderlyingData()));
    }
    
    /**
     * Create a match between two selected persons.
     * @param menteeVM the ViewModel containing the mentee
     * @param mentorVM the ViewModel containing the mentor
     * @param resultVM the ViewModel to update with the results
     * @param data how to get the configuration data
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> makeSingleMatch(PersonViewModel menteeVM, PersonViewModel mentorVM,
            PersonMatchesViewModel resultVM, RunConfiguration data){
        return matchMaker.submit(new SingleMatchTask(resultVM, data, menteeVM.getPerson(), 
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
     * @param data how to get the configuration data
     * @param toExportWithHeader a mandatory first ViewModel containing matches to export
     * @param toExport optional additional ViewModels containing the matches to export
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> exportMatches(File outputFile, RunConfiguration data, 
            PersonMatchesViewModel toExportWithHeader, PersonMatchesViewModel... toExport){
        //TODO: use a ResultConfiguration obtained from getResultConfiguration
        return matchMaker.submit(new MatchExportTask(
                () -> new PrintWriter(outputFile, Charset.forName("utf-8")), data, 
                toExportWithHeader, toExport));
    }
    
    /**
     * Get a {@link ResultConfiguration}.
     * @param configurationVM the ViewModel containing the configuration
     * @param resultVMs the ViewModels to update with the configuration
     * @return a Future object that can be used to control the execution and completion of the task.
     */
    public Future<?> getResultConfiguration(
            ConfigurationPickerViewModel<ResultConfiguration<Person, Person>> configurationVM,
            List<? extends PersonMatchesViewModel> resultVMs) {
        return matchMaker.submit(new ConfigurationGetterTask<>(configurationVM, resultVMs));
    }
    
    public ConfigurationPickerViewModel<ResultConfiguration<Person, Person>> 
            forgeConfigurationPickerViewModel(){
                //TODO refactor to return the viewModel needed to initialise the full configuration panel
                //The viewModel should by default show the previous selected configuration, if any.
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
                return new ConfigurationPickerViewModel<>(configuration, values, defaultPath, type,
                        parser, 
                        List.of(Pair.of("YAML files", List.of("*.yaml")), 
                                Pair.of("All files", List.of("*.*"))));
            }
}

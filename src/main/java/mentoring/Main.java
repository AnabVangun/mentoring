package mentoring;

/**
 * Proof of concept of the mentoring application.
 */
public class Main {
    public static void main(String[] args) {
        MainApplication.launch(MainApplication.class, args);
    }
    /**
     * TODO: link GUI to code.
     * Fix styling of mentees and mentors when columns are sorted
     * Fix configure: when applying a new configuration, the mentee and mentor lists are not cleared
     * Update dependencies:
     *      Update NetBeans to newest version
     *      Update JDK to newest version
     *      Update Gradle to newest version
     *      Update shadow to com.gradleup.shadow version 8.3.3
     *      Update snakeyaml engine to 2.8
     *      Update Guice to 7.0.0
     *      Update JUnit to 5.11.2
     *      Update Mockito to 5.14.1
     *      Update TestFX to 4.0.18
     * 7. Add an indicator for when "Run" should be clicked
     * 9a. Add global configuration parameters for magic numbers.
     * 9b. Internationalize GUI
     * 9e. Add save/load button to load configuration (including forbidden matches) and results from a file
     * 10. Handle concurrency TODOs
     * 11. Alert if configuration is not consistent with data file:
     * for person conf, missing columns in file header
     * 12. Alert if criteria configuration is not consistent with person configuration
     * 13. Choose criteria configuration (file)
     * Rationalise tests (see ForbiddenMatchTaskTest for NPE testing)
     * 14. Modify assignmentproblem to handle cancellation and offer progress status
     * 15. Use new version of assignmentproblem to allow cancellation and display progress status
     * 16. Check good practice for storing FXML files (resources vs in packages)
     * 16a. Handle "Unsupported JavaFX configuration: classes were loaded from" warning because of TestFX
     * 17. Add undo/redo option (see command design pattern)
     * Handle person asking for several matches
     */
}

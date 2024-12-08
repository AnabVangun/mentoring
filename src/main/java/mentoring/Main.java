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
     * Add control in PersonFileParser: ignore empty lines
     * Fix IndexedPropertyDescription: when the indices are not 0,1,..., 
     *      then getting the list fails
     * Add an "integer or default" type: when value is not set, use a provided default value
     * Modify maturity criterion to ignore criterion when value is not set
     * Fix coloring of mentors: they are not colored when matching completes but
     *      after an action in the GUI and when associated with the default 
     *      mentee, they are colored even though they shouldn't. It may be 
     *      linked to duplicate mentors.
     * 7. Add an indicator for when "Run" should be clicked
     * Fix Gradle deprecation warning
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

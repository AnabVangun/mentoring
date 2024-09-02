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
     * Fix display: column "cost" should be properly sorted as a number and not a string
     * 6. Handle refactoring TODOs
     * Add status for person: "not matched", "manual match", "automated match"
     * 7. Add an indicator for when "Run" should be clicked
     * 9a. Add global configuration parameters for magic numbers.
     * 9b. Internationalize GUI
     * 9e. Add save/load button to load configuration (including forbidden matches) and results from a file
     * 10. Handle concurrency TODOs
     * 10a. Handle disabled property for all buttons in MainView
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
     */
    /*TODO add CLI*/
}

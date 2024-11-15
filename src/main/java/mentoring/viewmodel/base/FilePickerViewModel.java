package mentoring.viewmodel.base;

import java.io.File;
import java.io.IOException;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import mentoring.viewmodel.base.function.FileParser;

/**
 * ViewModel made to pick and parse files containing a Java object (that may be
 * a collection).
 * @param<T> type of the Java object to get from the selected file
 */
public abstract class FilePickerViewModel<T> {
    
    private final ReadOnlyObjectWrapper<File> selectedFile = new ReadOnlyObjectWrapper<>();
    //TODO investigate how to make fileParser private without breaking deep copy constructor
    protected final FileParser<T> fileParser;

    protected FilePickerViewModel(FileParser<T> fileParser) {
        this.fileParser = fileParser;
    }

    /**
     * Get the currently selected file. This observable may be invalidated by calls to
     * {@link #setCurrentFile(java.io.File)}.
     * @return an observable describing the currently selected file
     */
    public final ReadOnlyObjectProperty<File> getCurrentFile() {
        return selectedFile.getReadOnlyProperty();
    }

    /**
     * Select the input file. Calls to this method will invalidate the properties
     * returned by {@link #getCurrentFile()} and {@link #getCurrentFilePath()} if appropriate.
     * @param file the file to select
     */
    public final void setCurrentFile(File file) {
        File safeFile = verifyOrCureFile(file);
        selectedFile.set(safeFile);
        setFileDependentAttributes();
    }
    
    /**
     * Check that the file is acceptable. Implementing subclasses MAY either
     * return a default or adapted file, or throw an exception on an invalid 
     * input.
     * @param file to check
     * @return a cured file
     */
    protected abstract File verifyOrCureFile(File file);
    
    /**
     * Method used in {@link #setCurrentFile(java.io.File)} to set subclass 
     * specific attributes after setting the file.
     */
    protected void setFileDependentAttributes(){}

    /**
     * Parse the currently selected file.
     * @return the data contained in the file as a Java object
     * @throws IOException if anything goes wrong during the parsing
     */
    public final T parseCurrentFile() throws IOException {
        //TODO add tests
        return fileParser.apply(getCurrentFile().get());
    }   
}

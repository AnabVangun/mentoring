package mentoring.viewmodel.base;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import mentoring.viewmodel.base.function.FileParser;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Abstract class with a basic implementation for ViewModels made to pick specific files.
 */
abstract class FilePickerViewModel<T> {
    private final ReadOnlyStringWrapper selectedFilePath = new ReadOnlyStringWrapper();
    private final ReadOnlyObjectWrapper<File> selectedFile = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<File> selectedFileDirectory = new ReadOnlyObjectWrapper<>();
    private final FileParser<T> fileParser;
    
    protected FilePickerViewModel(String defaultFilePath, FileParser<T> fileParser){
        this.fileParser = Objects.requireNonNull(fileParser);
        setCurrentFile(getFileOrDefaultDirectory(defaultFilePath));
    }
    
    private static File getFileOrDefaultDirectory(String filePath) {
        return getFileOrDefaultDirectory((File) (filePath == null ? null : new File(filePath)));
    }
    
    private static File getFileOrDefaultDirectory(File file) {
        //TODO extract default file as a configurable parameter
        return (file == null || !file.exists()) ? new File(System.getProperty("user.home")) : file;
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
        File safeFile = getFileOrDefaultDirectory(file);
        selectedFile.set(safeFile);
        selectedFilePath.set(safeFile.getPath());
        selectedFileDirectory.set(safeFile.isFile() ? safeFile.getParentFile() : safeFile);
    }

    /**
     * Get an absolute path to the currently selected file. This observable may be invalidated by
     * calls to {@link #setCurrentFile(java.io.File)}.
     * @return an observable describing an absolute path to the currently selected file
     */
    public final ReadOnlyStringProperty getCurrentFilePath() {
        return selectedFilePath.getReadOnlyProperty();
    }

    /**
     * Get the directory containing the currently selected file.
     * This observable may be invalidated by calls to {@link #setCurrentFile(java.io.File)}.
     * @return an observable describing the currently selected directory
     */
    public final ReadOnlyObjectProperty<File> getCurrentFileDirectory() {
        return selectedFileDirectory.getReadOnlyProperty();
    }
    
    /**
     * Parse the currently selected file.
     * @return the data contained in the file as a Java object
     * @throws IOException if anything goes wrong during the parsing
     */
    public final T parseCurrentFile() throws IOException{
        return fileParser.apply(getCurrentFile().get());
    }
    
    /**
     * Return the standard file extensions for this picker as a pair with a description and the 
     * associated extensions. Each extension SHOULD be of the form {@code *.<extension>}.
     * @return a mapping between descriptions and a list of associated file extensions
     */
    public abstract List<Pair<String, List<String>>> getStandardExtensions();
}

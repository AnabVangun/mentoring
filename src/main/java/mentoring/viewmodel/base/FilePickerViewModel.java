package mentoring.viewmodel.base;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import mentoring.viewmodel.base.function.FileParser;
import org.apache.commons.lang3.tuple.Pair;

/**
 * ViewModel made to pick and parse files containing a Java object (that may be a collection).
 * @param<T> type of the Java object to get from the selected file
 */
public class FilePickerViewModel<T> {
    private final ReadOnlyStringWrapper selectedFilePath = new ReadOnlyStringWrapper();
    private final ReadOnlyObjectWrapper<File> selectedFile = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<File> selectedFileDirectory = new ReadOnlyObjectWrapper<>();
    private final FileParser<T> fileParser;
    private final List<Pair<String, List<String>>> fileExtensions;
    
    /**
     * Build a new ConfigurationPickerViewModel instance.
     * @param defaultFilePath path to a file that can be parsed, MAY be a null or empty String
     * @param fileParser to parse the file
     * @param fileExtensions the standard file extensions for this picker as a pair with a 
     *      description and the associated extensions, where each extension SHOULD be of the form 
     *      {@code *.<extension>}
     */
    public FilePickerViewModel(String defaultFilePath, FileParser<T> fileParser,
            List<Pair<String, List<String>>> fileExtensions){
        this.fileParser = Objects.requireNonNull(fileParser);
        this.fileExtensions = makeUnmodifiableCopy(fileExtensions);
        setCurrentFile(getFileOrDefaultDirectory(defaultFilePath));
    }
    
    /**
     * Deep-copy constructor: build a new independent instance with equal values.
     * @param toCopy the other instance to copy
     */
    FilePickerViewModel(FilePickerViewModel<T> toCopy){
        //TODO test
        this.fileParser = toCopy.fileParser;
        this.fileExtensions = toCopy.fileExtensions;
        setCurrentFile(toCopy.getCurrentFile().get());
    }
    
    private static <K, V> List<Pair<K, List<V>>> makeUnmodifiableCopy(List<Pair<K, List<V>>> input){
        List<Pair<K, List<V>>> modifiable = input.stream()
                .map(pair -> Pair.of(pair.getLeft(), List.copyOf(pair.getRight())))
                .collect(Collectors.toList());
        return Collections.unmodifiableList(modifiable);
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
    public final List<Pair<String, List<String>>> getStandardExtensions(){
        return this.fileExtensions;
    }
}

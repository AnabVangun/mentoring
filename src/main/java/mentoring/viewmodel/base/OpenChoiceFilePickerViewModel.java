package mentoring.viewmodel.base;

import java.io.File;
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
public class OpenChoiceFilePickerViewModel<T> extends FilePickerViewModel<T> {
    private final ReadOnlyStringWrapper selectedFilePath = new ReadOnlyStringWrapper();
    private final ReadOnlyObjectWrapper<File> selectedFileDirectory = new ReadOnlyObjectWrapper<>();
    private final List<Pair<String, List<String>>> fileExtensions;
    
    @Override
    protected File verifyOrCureFile(File file) throws IllegalArgumentException {
        //TODO throw exception if file is not null but does not exist
        return getFileOrDefaultDirectory(file);
    }

    @Override
    protected void setFileDependentAttributes() {
        File file = getCurrentFile().get();
        selectedFileDirectory.set(file.isFile() ? file.getParentFile() : file);
        selectedFilePath.set(file.getPath());
    }
    
    private OpenChoiceFilePickerViewModel(File initialFile, FileParser<T> fileParser,
            List<Pair<String, List<String>>> fileExtensions){
        super(fileParser);
        setCurrentFile(initialFile);
        this.fileExtensions = fileExtensions;
    }
    
    /**
     * Build a new instance.
     * @param defaultFilePath path to a file that can be parsed, MAY be a null or empty String
     * @param fileParser to parse the file
     * @param fileExtensions the standard file extensions for this picker as a pair with a 
     *      description and the associated extensions, where each extension SHOULD be of the form 
     *      {@code *.<extension>}
     */
    public OpenChoiceFilePickerViewModel(String defaultFilePath, FileParser<T> fileParser,
            List<Pair<String, List<String>>> fileExtensions){
        this(getFileOrDefaultDirectory(defaultFilePath), 
                Objects.requireNonNull(fileParser),
                makeUnmodifiableCopy(fileExtensions));
    }
    
    /**
     * Deep-copy constructor: build a new independent instance with equal values.
     * @param toCopy the other instance to copy
     */
    OpenChoiceFilePickerViewModel(OpenChoiceFilePickerViewModel<T> toCopy){
        this(toCopy.getCurrentFile().get(), toCopy.fileParser, toCopy.fileExtensions);
    }
    
    private static <K, V> List<Pair<K, List<V>>> makeUnmodifiableCopy(
            List<Pair<K, List<V>>> input){
        List<Pair<K, List<V>>> modifiable = input.stream()
                .map(pair -> Pair.of(pair.getLeft(), List.copyOf(pair.getRight())))
                .collect(Collectors.toList());
        return Collections.unmodifiableList(modifiable);
    }
    
    private static File getFileOrDefaultDirectory(String filePath) {
        return getFileOrDefaultDirectory((File) (filePath == null ? null : new File(filePath)));
    }
    
    private static File getFileOrDefaultDirectory(File file) {
        return isInvalidFile(file) ? Parameters.getDefaultDirectory() : file;
    }
    
    private static boolean isInvalidFile(File file){
        return file == null || !file.exists();
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
     * Return the standard file extensions for this picker as a pair with a description and the 
     * associated extensions. Each extension SHOULD be of the form {@code *.<extension>}.
     * @return a mapping between descriptions and a list of associated file extensions
     */
    public final List<Pair<String, List<String>>> getStandardExtensions(){
        return this.fileExtensions;
    }
}

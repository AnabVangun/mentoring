package mentoring.viewmodel.base;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mentoring.viewmodel.base.function.FileParser;


/**
 * ViewModel made to pick and parse files containing a Java object (that may be 
 * a collection) among a predefined list of files.
 * @param<T> type of the Java object to get from the selected file
 */
public class ClosedChoiceFilePickerViewModel<T> extends FilePickerViewModel<T> {
    private final Set<File> files = new HashSet<>();

    /**
     * Build a new instance.
     * @param files that may be parsed by this ViewModel. MUST NOT be null and
     * MUST NOT be empty
     * @param fileParser parser used to parse the files
     */
    public ClosedChoiceFilePickerViewModel(List<File> files, 
            FileParser<T> fileParser) {
        super(fileParser);
        this.files.addAll(files);
        setCurrentFile(files.get(0));
    }

    @Override
    protected File verifyOrCureFile(File file) {
        if (files.contains(file)){
            return file;
        } else {
            throw new IllegalArgumentException(
                    "could not find file %s in known files %s".formatted(file, files));
        }
    }
}

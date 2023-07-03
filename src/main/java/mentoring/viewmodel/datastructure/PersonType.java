package mentoring.viewmodel.datastructure;

import java.util.function.Function;
import mentoring.viewmodel.RunConfiguration;

public enum PersonType {
    MENTEE(RunConfiguration::getMenteeFilePath),
    MENTOR(RunConfiguration::getMentorFilePath);
    
    private final Function<RunConfiguration, String> filePathExtracter;

    private PersonType(Function<RunConfiguration, String> filePathSupplier) {
        this.filePathExtracter = filePathSupplier;
    }
    
    public String getFilePathFromConfiguration(RunConfiguration data){
        return filePathExtracter.apply(data);
    }
}

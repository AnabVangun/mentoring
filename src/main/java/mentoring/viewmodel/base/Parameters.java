package mentoring.viewmodel.base;

import java.io.File;

public class Parameters {
    private Parameters(){/*no-op*/}
    
    public static File getDefaultDirectory(){
        return new File(System.getProperty("user.home"));
    }
    
    public static final String MANUAL_MATCH_PSEUDOCLASS = "manual-match";
    public static final String COMPUTED_MATCH_PSEUDOCLASS = "computed-match";
}

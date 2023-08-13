package mentoring.viewmodel.base;

import java.io.File;

class Parameters {
    private Parameters(){/*no-op*/}
    
    public static File getDefaultDirectory(){
        return new File(System.getProperty("user.home"));
    }
}

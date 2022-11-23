package test.tools;

/**
 * Provider of a simple override of {@code toString} for the parameter of {@link TestFramework}.
 * 
 * <p>It is not required that the class used as a parameter for a TestFramework implementation 
 * inherits from this one: a record can be used just as well, in which case it should override
 * its {@code toString()} method.
 */
public abstract class TestArgs {
    private final String testCase;
    
    public TestArgs(String testCase){
        this.testCase = testCase;
    }
    
    @Override
    public String toString(){
        return this.testCase;
    }
}

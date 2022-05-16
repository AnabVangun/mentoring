package test.tools;

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

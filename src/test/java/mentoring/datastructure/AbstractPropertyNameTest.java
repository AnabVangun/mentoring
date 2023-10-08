package mentoring.datastructure;

import java.util.stream.Stream;
import mentoring.datastructure.AbstractPropertyNameTest.AbstractPropertyNameArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

abstract class AbstractPropertyNameTest<Property extends PropertyName<?>, 
        Args extends AbstractPropertyNameArgs<?, Property>> implements TestFramework<Args>{
    
    /**
     * Return an argument with different values than those from {@link #argumentsSupplier()}.
     * @return a specially crafted argument.
     */
    protected abstract Args getDifferentArgs();
    
    @TestFactory
    Stream<DynamicNode> getName(){
        return test("getName()", args -> Assertions.assertEquals(args.getExpectedName(),
                args.convert().getName()));
    }
    
    @TestFactory
    Stream<DynamicNode> getHeaderName(){
        return test("getHeaderName()", args -> Assertions.assertEquals(args.getExpectedHeaderName(),
                args.convert().getHeaderName()));
    }
    
    @TestFactory
    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    Stream<DynamicNode> getType(){
        return test("getType()", args -> Assertions.assertEquals(args.expectedType,
                args.convert().getType()));
    }
    
    /**
     * Verify that {@link PropertyName#getStringRepresentation(mentoring.datastructure.Person)} 
     * returns the expected value.
     * @return tests to verify the behaviour.
     */
    @TestFactory
    abstract Stream<DynamicNode> getStringRepresentation_expectedValue();
    
    @TestFactory
    Stream<DynamicNode> getStringRepresentation_NPE(){
        return test(Stream.of(getDifferentArgs()), //getDifferentArgs used to provide a single args
                "getStringRepresentation() throws an NPE on null input", args -> {
                    PropertyName<?> property = args.convert();
                    Assertions.assertThrows(NullPointerException.class, 
                            () -> property.getStringRepresentation(null));
                });
    }
    
    abstract static class AbstractPropertyNameArgs<K, T extends PropertyName<? extends K>> 
            extends TestArgs {
        final String name;
        final String headerName;
        final PropertyType<? extends K> expectedType;
        
        AbstractPropertyNameArgs(String testCase, String name, String headerName, 
                PropertyType<? extends K> expectedType){
            super(testCase);
            this.name = name;
            this.headerName = headerName;
            this.expectedType = expectedType;
        }
        
        abstract T convert();
        
        String getExpectedName(){
            return this.name;
        }

        String getExpectedHeaderName() {
            return this.headerName;
        }
    }
}

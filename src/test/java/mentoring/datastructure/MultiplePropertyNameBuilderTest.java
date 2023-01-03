package mentoring.datastructure;

import java.util.stream.Stream;
import mentoring.datastructure.MultiplePropertyNameBuilderTest.MultiplePropertyNameBuilderArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

class MultiplePropertyNameBuilderTest extends 
        AbstractPropertyNameBuilderTest<MultiplePropertyNameBuilderArgs, MultiplePropertyNameBuilder>{
    
    @Override
    public Stream<MultiplePropertyNameBuilderArgs> argumentsSupplier(){
        return Stream.of(
                new IndexedPropertyNameBuilderArgs("indexed builder", "foo", "bar", 
                        PropertyType.BOOLEAN),
                new SetPropertyNameBuilderArgs("set builder", "bar", "foo", PropertyType.INTEGER));
    }
    
    @TestFactory
    Stream<DynamicNode> getState_afterSetAggregation(){
        return test("getters return expected value after a call to setAggregation()", args -> {
            MultiplePropertyNameBuilder builder = args.prepared().setAggregation(args.aggregation);
            Assertions.assertAll(args.supplyAssertionsBuilderAsExpected(builder, false));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> build_failBeforeSetAggregation(){
        return test("build() fails before a call to setAggregation()", args ->
                Assertions.assertThrows(IllegalStateException.class, () -> args.prepared().build()));
    }
    
    @Override
    @TestFactory
    Stream<DynamicNode> reuseBuilder(){
        return test("build() produces the prepared property when reusing the builder",
                args -> {
                    MultiplePropertyNameBuilder builder = args.readyToBuild();
                    builder.build();
                    builder.prepare(args.name + "_1", PropertyType.YEAR)
                            .setAggregation(AggregationType.SET)
                            .withHeaderName(args.headerName + "_5");
                    MultiplePropertyName<?,?> property = builder.build();
                    //TODO use PropertyName.equals() here when implemented
                    Assertions.assertAll(
                            () -> Assertions.assertEquals(args.name + "_1", property.getName()),
                            () -> Assertions.assertEquals(PropertyType.YEAR, property.getType()),
                            () -> Assertions.assertEquals(args.headerName + "_5", 
                                    property.getHeaderName()),
                            () -> Assertions.assertTrue(property instanceof SetPropertyName));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> setAggregation_returnsSelf(){
        return test("setAggregation() returns the same instance", args -> {
            MultiplePropertyNameBuilder builder = args.prepared().setAggregation(args.aggregation);
            Assertions.assertSame(builder, builder.setAggregation(args.aggregation));
        });
    }
    
    abstract static class MultiplePropertyNameBuilderArgs extends 
            AbstractPropertyNameBuilderArgs<MultiplePropertyNameBuilder>{
        final AggregationType aggregation;

        public MultiplePropertyNameBuilderArgs(String testCase, String name, String headerName, 
                PropertyType<?> type, AggregationType aggregation) {
            super(testCase, name, headerName, type);
            this.aggregation = aggregation;
        }

        @Override
        protected MultiplePropertyNameBuilder convert() {
            return new MultiplePropertyNameBuilder();
        }
        
        @Override 
        protected MultiplePropertyNameBuilder readyToBuildWithoutOptionalParameters(){
            return super.readyToBuildWithoutOptionalParameters().setAggregation(aggregation);
        }
        
        @Override
        protected MultiplePropertyNameBuilder readyToBuild(){
            return super.readyToBuild().setAggregation(aggregation);
        }
    }
    
    static class IndexedPropertyNameBuilderArgs extends MultiplePropertyNameBuilderArgs{
        //TODO: this class becomes obsolete as soon as PropertyName.equals() has been implemented
        public IndexedPropertyNameBuilderArgs(String testCase, String name, String headerName, 
                PropertyType<?> type) {
            super(testCase, name, headerName, type, AggregationType.INDEXED);
        }
        
        @Override 
        protected Stream<Executable> supplyAssertionsPropertyAsExpected(PropertyName<?> actual,
                boolean withHeaderName){
            return Stream.concat(super.supplyAssertionsPropertyAsExpected(actual, withHeaderName),
                    Stream.of(() -> Assertions.assertTrue(actual instanceof IndexedPropertyName, 
                            "expected " + actual + " to be an instance of IndexedPropertyName")));
        }
    }
    
    static class SetPropertyNameBuilderArgs extends MultiplePropertyNameBuilderArgs{
        //TODO: this class becomes obsolete as soon as PropertyName.equals() has been implemented
        public SetPropertyNameBuilderArgs(String testCase, String name, String headerName, 
                PropertyType<?> type) {
            super(testCase, name, headerName, type, AggregationType.SET);
        }
        
        @Override 
        protected Stream<Executable> supplyAssertionsPropertyAsExpected(PropertyName<?> actual,
                boolean withHeaderName){
            return Stream.concat(super.supplyAssertionsPropertyAsExpected(actual, withHeaderName),
                    Stream.of(() -> Assertions.assertTrue(actual instanceof SetPropertyName, 
                            "expected " + actual + " to be an instance of SetPropertyName")));
        }
    }
}

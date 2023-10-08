package mentoring.datastructure;

import java.util.stream.Stream;
import mentoring.datastructure.MultiplePropertyNameBuilderTest.MultiplePropertyNameBuilderArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class MultiplePropertyNameBuilderTest extends 
        AbstractPropertyNameBuilderTest<MultiplePropertyNameBuilderArgs, MultiplePropertyNameBuilder>{
    //TODO refactor all logic between AbstractPropertyNameBuilderTest, SimplePropertyNameBuilderTest and Multiple
    @Override
    public Stream<MultiplePropertyNameBuilderArgs> argumentsSupplier(){
        return Stream.of(
                new MultiplePropertyNameBuilderArgs("indexed builder", "foo", "bar", 
                        PropertyType.BOOLEAN, AggregationType.INDEXED,
                        new IndexedPropertyName<>("foo", "foo", PropertyType.BOOLEAN),
                        new IndexedPropertyName<>("foo", "bar", PropertyType.BOOLEAN)),
                new MultiplePropertyNameBuilderArgs("set builder", "bar", "foo", 
                        PropertyType.INTEGER, AggregationType.SET,
                        new SetPropertyName<>("bar", "bar", PropertyType.INTEGER),
                        new SetPropertyName<>("bar", "foo", PropertyType.INTEGER)));
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
                    Assertions.assertEquals(new SetPropertyName<>(args.name + "_1", 
                            args.headerName + "_5", PropertyType.YEAR),
                            property);
                });
    }
    
    @TestFactory
    Stream<DynamicNode> setAggregation_returnsSelf(){
        return test("setAggregation() returns the same instance", args -> {
            MultiplePropertyNameBuilder builder = args.prepared().setAggregation(args.aggregation);
            Assertions.assertSame(builder, builder.setAggregation(args.aggregation));
        });
    }

    @Override
    protected PropertyName<?> provideNewProperty(String name, String headerName, PropertyType<?> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    static class MultiplePropertyNameBuilderArgs extends 
            AbstractPropertyNameBuilderArgs<MultiplePropertyNameBuilder>{
        final AggregationType aggregation;
        final MultiplePropertyName<?,?> expectedWithoutOptionalSetters;
        final MultiplePropertyName<?,?> expectedWithHeaderName;
        
        public MultiplePropertyNameBuilderArgs(String testCase, String name, String headerName, 
                PropertyType<?> type, AggregationType aggregation,
                MultiplePropertyName<?,?> expectedWithoutOptionalSetters,
                MultiplePropertyName<?,?> expectedWithHeaderName) {
            super(testCase, name, headerName, type);
            this.aggregation = aggregation;
            this.expectedWithoutOptionalSetters = expectedWithoutOptionalSetters;
            this.expectedWithHeaderName = expectedWithHeaderName;
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

        @Override
        protected PropertyName<?> supplyExpectedProperty(boolean withHeaderName) {
            return withHeaderName ? expectedWithHeaderName : expectedWithoutOptionalSetters; 
        }
    }
}

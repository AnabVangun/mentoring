package mentoring.datastructure;

import java.util.stream.Stream;
import mentoring.datastructure.MultiplePropertyDescriptionBuilderTest.MultiplePropertyDescriptionBuilderArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class MultiplePropertyDescriptionBuilderTest extends 
        PropertyDescriptionBuilderTest<MultiplePropertyDescriptionBuilderArgs, MultiplePropertyDescriptionBuilder>{
    @Override
    public Stream<MultiplePropertyDescriptionBuilderArgs> argumentsSupplier(){
        return Stream.of(new MultiplePropertyDescriptionBuilderArgs("indexed builder", "foo", "bar", 
                        PropertyType.BOOLEAN, AggregationType.INDEXED,
                        new IndexedPropertyDescription<>("foo", "foo", PropertyType.BOOLEAN),
                        new IndexedPropertyDescription<>("foo", "bar", PropertyType.BOOLEAN)),
                new MultiplePropertyDescriptionBuilderArgs("set builder", "bar", "foo", 
                        PropertyType.INTEGER, AggregationType.SET,
                        new SetPropertyDescription<>("bar", "bar", PropertyType.INTEGER),
                        new SetPropertyDescription<>("bar", "foo", PropertyType.INTEGER)));
    }
    
    @TestFactory
    Stream<DynamicNode> getState_afterSetAggregation(){
        return test("getters return expected value after a call to setAggregation()", args -> {
            MultiplePropertyDescriptionBuilder builder = args.prepared().setAggregation(args.aggregation);
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
                    MultiplePropertyDescriptionBuilder builder = args.readyToBuild();
                    builder.build();
                    builder.prepare(args.name + "_1", PropertyType.YEAR)
                            .setAggregation(AggregationType.SET)
                            .withHeaderName(args.headerName + "_5");
                    MultiplePropertyDescription<?,?> property = builder.build();
                    Assertions.assertEquals(new SetPropertyDescription<>(args.name + "_1", 
                            args.headerName + "_5", PropertyType.YEAR),
                            property);
                });
    }
    
    @TestFactory
    Stream<DynamicNode> setAggregation_returnsSelf(){
        return test("setAggregation() returns the same instance", args -> {
            MultiplePropertyDescriptionBuilder builder = args.prepared().setAggregation(args.aggregation);
            Assertions.assertSame(builder, builder.setAggregation(args.aggregation));
        });
    }

    @Override
    protected PropertyDescription<?> provideNewProperty(String name, String headerName, PropertyType<?> type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    static class MultiplePropertyDescriptionBuilderArgs extends 
            PropertyDescriptionBuilderArgs<MultiplePropertyDescriptionBuilder>{
        final AggregationType aggregation;
        final MultiplePropertyDescription<?,?> expectedWithoutOptionalSetters;
        final MultiplePropertyDescription<?,?> expectedWithHeaderName;
        
        public MultiplePropertyDescriptionBuilderArgs(String testCase, String name, String headerName, 
                PropertyType<?> type, AggregationType aggregation,
                MultiplePropertyDescription<?,?> expectedWithoutOptionalSetters,
                MultiplePropertyDescription<?,?> expectedWithHeaderName) {
            super(testCase, name, headerName, type);
            this.aggregation = aggregation;
            this.expectedWithoutOptionalSetters = expectedWithoutOptionalSetters;
            this.expectedWithHeaderName = expectedWithHeaderName;
        }

        @Override
        protected MultiplePropertyDescriptionBuilder convert() {
            return new MultiplePropertyDescriptionBuilder();
        }
        
        @Override 
        protected MultiplePropertyDescriptionBuilder readyToBuildWithoutOptionalParameters(){
            return super.readyToBuildWithoutOptionalParameters().setAggregation(aggregation);
        }
        
        @Override
        protected MultiplePropertyDescriptionBuilder readyToBuild(){
            return super.readyToBuild().setAggregation(aggregation);
        }

        @Override
        protected PropertyDescription<?> supplyExpectedProperty(boolean withHeaderName) {
            return withHeaderName ? expectedWithHeaderName : expectedWithoutOptionalSetters; 
        }
    }
}

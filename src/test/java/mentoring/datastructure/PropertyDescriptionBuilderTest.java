package mentoring.datastructure;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import test.tools.TestFramework;

abstract class PropertyDescriptionBuilderTest
        <K extends PropertyDescriptionBuilderArgs<V>, V extends PropertyDescriptionBuilder<V>> 
        implements TestFramework<K> {
    
    @TestFactory
    Stream<DynamicNode> getState_afterPrepare(){
        return test("getters return expected value after a call to prepare()", args -> {
            V builder = args.prepared();
            Assertions.assertAll(args.supplyAssertionsBuilderAsExpected(builder, false));
        });
    }

    @SuppressWarnings("unchecked")
    @TestFactory
    Stream<DynamicNode> getState_afterWithHeaderName(){
        return test("getters return expected value after a call to withHeaderName()", args -> {
            V builder = (V) args.prepared().withHeaderName(args.headerName);
            Assertions.assertAll(args.supplyAssertionsBuilderAsExpected(builder, true));
        });
    }

    @TestFactory
    Stream<DynamicNode> getState_failBeforePrepare(){
        return test("getters raise expected exception before prepare() has been called", args -> 
                        Assertions.assertAll(gettersThrowIllegalState(args.convert())));
    }
    
    Stream<Executable> gettersThrowIllegalState(PropertyDescriptionBuilder builder){
        Class<IllegalStateException> exception = IllegalStateException.class;
        return Stream.of(() -> Assertions.assertThrows(exception, () -> builder.getName()),
                            () -> Assertions.assertThrows(exception, () -> builder.getHeaderName()),
                            () -> Assertions.assertThrows(exception, () -> builder.getType()));
    }

    @TestFactory
    Stream<DynamicNode> getState_failAfterBuild(){
        return test("getters raise expected exception after build() has been called", args ->
                        Assertions.assertAll(gettersThrowIllegalState(args.built())));
    }
    
    @TestFactory
    Stream<DynamicNode> build_failBeforePrepare(){
        return test("build() fails before a call to prepare()", args -> 
                Assertions.assertThrows(IllegalStateException.class, () -> args.convert().build()));
    }

    @TestFactory
    Stream<DynamicNode> build_afterWithHeaderName(){
        return test("build() produces the expected PropertyDescription after calling withHeaderName()",
                args -> {
                   PropertyDescription<?> property = args.readyToBuild().build();
                   args.assertPropertyAsExpected(property, true);
                });
    }

    @TestFactory
    Stream<DynamicNode> build_withoutOptionalSetters(){
        return test("build() produces the expected PropertyDescription without calling optional setters",
                args -> {
                   PropertyDescription<?> property = args.readyToBuildWithoutOptionalParameters().build();
                   args.assertPropertyAsExpected(property, false);
                });
    }

    @TestFactory
    Stream<DynamicNode> reuseBuilder(){
        return test("build() produces the prepared property when reusing the builder",
                args -> {
                    V builder = args.readyToBuild();
                    builder.build();
                    builder.prepare(args.name + "_1", PropertyType.YEAR)
                            .withHeaderName(args.headerName + "_5");
                    PropertyDescription<?> property = builder.build();
                    Assertions.assertEquals(provideNewProperty(args.name + "_1", 
                            args.headerName + "_5", PropertyType.YEAR), property);
                });
    }
    
    protected abstract PropertyDescription<?> provideNewProperty(String name, String headerName, 
            PropertyType<?> type);
    
    @TestFactory
    Stream<DynamicNode> prepare_returnsSelf(){
        return test("prepare() returns the same instance",
                args -> {
                    V builder = args.convert();
                    Assertions.assertSame(builder, builder.prepare(args.name, args.type));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> withHeaderName_returnsSelf(){
        return test("withHeaderName() returns the same instance",
                args -> {
                    V builder = args.prepared();
                    Assertions.assertSame(builder, builder.withHeaderName(args.headerName));
                });
    }
}

package mentoring.datastructure;

import java.util.stream.Stream;
import static mentoring.datastructure.PropertyType.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

class PropertyTypeTest implements TestFramework<PropertyTypeTest.PropertyTypeArgs>{

    @Override
    public Stream<PropertyTypeArgs> argumentsSupplier() {
            return Stream.of(new PropertyTypeArgs("true boolean", BOOLEAN, "oui", true),
                    new PropertyTypeArgs("false boolean", BOOLEAN, "foo", false),
                    new PropertyTypeArgs("positive integer", INTEGER, "8765", 8765),
                    new PropertyTypeArgs("zero", INTEGER, "000", 0),
                    new PropertyTypeArgs("negative integer", INTEGER, "-1", -1),
                    new PropertyTypeArgs("string", STRING, "string", "string"));
        }
    
    @TestFactory
    Stream<DynamicNode> parse(){
        return test("parse() returns correct value", args -> 
                Assertions.assertEquals(args.output, args.type.parse(args.input)));
    }
    
    @TestFactory
    Stream<DynamicNode> parse_NumberFormatException(){
        return test(Stream.of(new PropertyTypeArgs("decimal number", INTEGER, "3.15", null),
                new PropertyTypeArgs("alphabetic string", INTEGER, "12a", null),
                new PropertyTypeArgs("empty string", INTEGER, "", null)), 
                "parse() fails on invalid input", args -> 
                Assertions.assertThrows(NumberFormatException.class, () -> args.type.parse(args.input)));
    }
    
    @TestFactory
    Stream<DynamicNode> getType_correctType(){
        return test("getType() returns the correct type", args -> 
                args.type.getType().isInstance(args.type.parse(args.input)));
    }
    
    static class PropertyTypeArgs extends TestArgs{
        final String input;
        final Object output;
        final PropertyType type;
        
        public PropertyTypeArgs(String testCase, PropertyType<?> type, String input, Object output) {
            super(String.format("type %s for %s", type, testCase));
            this.type = type;
            this.input = input;
            this.output = output;
        }
        
    }
}

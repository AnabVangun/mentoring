package mentoring.configuration;

import java.util.Arrays;
import java.util.stream.Stream;
import mentoring.configuration.PojoPersonConfigurationTest.PojoPersonConfigurationArgs;

class PojoPersonConfigurationTest implements SimplePersonConfigurationTest<PojoPersonConfigurationArgs>{

    @Override
    public Stream<PojoPersonConfigurationArgs> argumentsSupplier() {
        return Arrays.stream(PojoPersonConfiguration.values()).map(PojoPersonConfigurationArgs::new);
    }
    
    static record PojoPersonConfigurationArgs(PojoPersonConfiguration configuration) 
            implements SimplePersonConfigurationArgs {
        
        @Override
        public String toString(){
            return configuration.toString();
        }

        @Override
        public PersonConfiguration convert() {
            return configuration.getConfiguration();
        }
        
    }
}

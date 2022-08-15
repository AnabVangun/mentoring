package mentoring.configuration;

import java.util.Arrays;
import java.util.stream.Stream;
import mentoring.configuration.PojoPersonConfigurationTest.PojoPersonConfigurationArgs;

class PojoPersonConfigurationTest implements PersonConfigurationTest<PojoPersonConfigurationArgs>{

    @Override
    public Stream<PojoPersonConfigurationArgs> argumentsSupplier() {
        return Arrays.stream(PojoPersonConfiguration.values()).map(PojoPersonConfigurationArgs::new);
    }
    
    static class PojoPersonConfigurationArgs extends PersonConfigurationArgs{
        final PojoPersonConfiguration configuration;

        public PojoPersonConfigurationArgs(PojoPersonConfiguration configuration) {
            super(configuration.toString());
            this.configuration = configuration;
        }

        @Override
        PersonConfiguration convert() {
            return configuration;
        }
        
    }
}

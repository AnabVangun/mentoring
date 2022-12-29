package mentoring.io;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.IndexedPropertyName;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyName;

enum DummyPersonConfiguration{
        SIMPLE_PROPERTIES(new PersonConfiguration("Simple properties",
                Set.of(new PropertyName<>("first", "first", PropertyType.STRING),
                        new PropertyName<>("second", "second", PropertyType.INTEGER)),
                Set.of(), "\\|", "", List.of())),
        MULTIPLE_PROPERTIES(new PersonConfiguration("Multiple properties", Set.of(), 
                Set.of(new IndexedPropertyName<>("third", "third", PropertyType.BOOLEAN),
                        new SetPropertyName<>("fourth", "fourth", PropertyType.INTEGER)), 
                "\\|", "", List.of())),
        NAME_PROPERTIES(new PersonConfiguration("Name properties", Set.of(), Set.of(), "\\|", "%s", 
                List.of("fifth"))),
        ALL_PROPERTIES(new PersonConfiguration("all properties",
                Set.of(new PropertyName<>("pFirst", "first", PropertyType.STRING),
                        new PropertyName<>("pSecond", "second", PropertyType.INTEGER)),
                Set.of(new IndexedPropertyName<>("pThird", "third", PropertyType.BOOLEAN),
                        new IndexedPropertyName<>("pFourth", "fourth", PropertyType.INTEGER)), 
                "\\|", "%s %s %s", List.of("fifth", "sixth", "seventh")));
        
        public final PersonConfiguration configuration;
        
        private DummyPersonConfiguration(PersonConfiguration configuration){
            this.configuration = configuration;
        }
        
        public static Stream<PersonConfiguration> getConfigurations(){
            return Arrays.stream(values()).map(value -> value.configuration);
        }
    }
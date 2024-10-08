package mentoring.io;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.IndexedPropertyDescription;
import mentoring.datastructure.PropertyType;
import mentoring.datastructure.SetPropertyDescription;
import mentoring.datastructure.SimplePropertyDescription;

enum DummyPersonConfiguration{
        SIMPLE_PROPERTIES(new PersonConfiguration("Simple properties",
                Set.of(new SimplePropertyDescription<>("first", "first", PropertyType.STRING),
                        new SimplePropertyDescription<>("second", "second", PropertyType.INTEGER)),
                Set.of(), "\\|", "", List.of())),
        MULTIPLE_PROPERTIES(new PersonConfiguration("Multiple properties", Set.of(), 
                Set.of(new IndexedPropertyDescription<>("third", "third", PropertyType.BOOLEAN),
                        new SetPropertyDescription<>("fourth", "fourth", PropertyType.INTEGER)), 
                "\\|", "", List.of())),
        NAME_PROPERTIES(new PersonConfiguration("Name properties", Set.of(), Set.of(), "\\|", "%s", 
                List.of("fifth"))),
        ALL_PROPERTIES(new PersonConfiguration("all properties",
                Set.of(new SimplePropertyDescription<>("pFirst", "first", PropertyType.STRING),
                        new SimplePropertyDescription<>("pSecond", "second", PropertyType.INTEGER)),
                Set.of(new IndexedPropertyDescription<>("pThird", "third", PropertyType.BOOLEAN),
                        new IndexedPropertyDescription<>("pFourth", "fourth", PropertyType.INTEGER)), 
                "\\|", "%s %s %s", List.of("fifth", "sixth", "seventh")));
        
        public final PersonConfiguration configuration;
        
        private DummyPersonConfiguration(PersonConfiguration configuration){
            this.configuration = configuration;
        }
        
        public static Stream<PersonConfiguration> getConfigurations(){
            return Arrays.stream(values()).map(value -> value.configuration);
        }
    }
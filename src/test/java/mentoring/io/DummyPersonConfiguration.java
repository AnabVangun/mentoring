package mentoring.io;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import mentoring.configuration.PersonConfiguration;
import mentoring.datastructure.PropertyName;
import mentoring.datastructure.PropertyType;

enum DummyPersonConfiguration implements PersonConfiguration{
        SIMPLE_PROPERTIES(Set.of(new PropertyName("first", PropertyType.STRING), 
                new PropertyName("second", PropertyType.INTEGER)), 
            Set.of(), "", List.of(), Set.of("first", "second")),
        MULTIPLE_PROPERTIES(Set.of(), Set.of(new PropertyName("third", PropertyType.BOOLEAN), 
                new PropertyName("fourth", PropertyType.INTEGER)), 
            "", List.of(), Set.of("third", "fourth")),
        NAME_PROPERTIES(Set.of(), Set.of(), "%s", List.of("fifth"), Set.of("fifth")),
        ALL_PROPERTIES(Set.of(new PropertyName("pFirst", "first", PropertyType.STRING),
                new PropertyName("pSecond", "second", PropertyType.INTEGER)),
            Set.of(new PropertyName("pThird", "third", PropertyType.BOOLEAN), 
                    new PropertyName("pFourth", "fourth", PropertyType.INTEGER)), "%s %s %s",
            List.of("fifth", "sixth", "seventh"), Set.of("first", "second", "third", "fourth",
                    "fifth", "sixth", "seventh"));
        
        private final Set<PropertyName> properties;
        private final Set<PropertyName> multipleProperties;
        private final String nameFormat;
        private final List<String> nameProperties;
        private final Collection<String> allProperties;
        
        private DummyPersonConfiguration(Set<PropertyName> properties,
                Set<PropertyName> multipleProperties, String nameFormat,
                List<String> nameProperties, Collection<String> allProperties){
            this.properties = properties;
            this.multipleProperties = multipleProperties;
            this.nameFormat = nameFormat;
            this.nameProperties = nameProperties;
            this.allProperties = allProperties;
        }

        @Override
        public Set<PropertyName> getPropertiesNames() {
            return this.properties;
        }

        @Override
        public Set<PropertyName> getMultiplePropertiesNames() {
            return this.multipleProperties;
        }

        @Override
        public String getSeparator() {
            return "\\|";
        }

        @Override
        public String getNameFormat() {
            return this.nameFormat;
        }

        @Override
        public List<String> getNamePropertiesHeaderNames() {
            return this.nameProperties;
        }

        @Override
        public Collection<String> getAllPropertiesHeaderNames() {
            return this.allProperties;
        }
    }
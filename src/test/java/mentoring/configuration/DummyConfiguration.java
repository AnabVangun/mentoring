package mentoring.configuration;

import java.util.List;

public class DummyConfiguration extends Configuration<DummyConfiguration> {
    
    public DummyConfiguration(String name) {
        super(name);
    }
    public static final List<String> expectedKnownContent = List.of("first", "second");
    public static DummyConfiguration first = new DummyConfiguration("first");
    public static DummyConfiguration second = new DummyConfiguration("second");

    @Override
    public List<DummyConfiguration> values() {
        return List.of(first, second);
    }
    
}

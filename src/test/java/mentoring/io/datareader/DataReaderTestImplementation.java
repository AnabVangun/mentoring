package mentoring.io.datareader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import mentoring.io.datareader.DataReaderTestImplementation.DummyDataReader;
import mentoring.io.datareader.DataReaderTestImplementation.DummyDataReaderArgs;

class DataReaderTestImplementation implements DataReaderTest<DummyDataReader, DummyDataReaderArgs>{

    @Override
    public Stream<DummyDataReaderArgs> argumentsSupplier() {
        return Stream.of(new DummyDataReaderArgs("foo case", false, "foo"),
                new DummyDataReaderArgs("bar case", false, "bar"));
    }
    
    @Override
    public Stream<DummyDataReaderArgs> invalidArgumentsSupplier() {
        return Stream.of(new DummyDataReaderArgs("invalid case", true, ""));
    }

    @Override
    public DummyDataReader prepareReader() {
        return new DummyDataReader(false, List.of("foo", "bar"));
    }
    
    static class DummyDataReader extends DataReader{
        private final boolean fail;
        private final Iterator<String> keys;
        
        DummyDataReader(boolean fail, Iterable<String> keys){
            this.fail = fail;
            this.keys = keys.iterator();
        }
        
        @Override
        protected Map<String, Object> readWithException(Reader reader) throws Exception{
            if (fail){
                throw new IOException("DataReader made to fail");
            } else {
                return Map.of(keys.next(), 0);
            }
        }
    }
    
    static class DummyDataReaderArgs extends DataReaderTest.DataReaderArgs<DummyDataReader>{
        private final boolean fail;
        private final String key;
        
        public DummyDataReaderArgs(String testCase, boolean fail, String key) {
            super(testCase, Map.of(key, 0));
            this.fail = fail;
            this.key = key;
        }

        @Override
        protected DummyDataReader getReaderUnderTest() {
            return new DummyDataReader(fail, List.of(key));
        }
        
        @Override
        Reader getDataSource() throws IOException{
            return new StringReader(key);
        }
        
    }
}

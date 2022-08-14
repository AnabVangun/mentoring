package mentoring.io;

import mentoring.datastructure.Person;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import mentoring.configuration.PersonConfiguration;

public class PersonFileParser {
    private final PersonConfiguration configuration;
    private CSVReader reader;
    private PersonParser parser;
    /*
    TODO: test this class. Start by testing PersonParser.
    */
    
    public PersonFileParser(PersonConfiguration configuration) throws IOException{
        this.configuration = configuration;
    }
    
    public List<Person> parse(Reader fileReader) throws IOException{
        initialiseReaderAndParser(fileReader);
        List<Person> result = new ArrayList<>();
        for (String[] line: reader){
            result.add(parser.parseLine(line));
        }
        return result;
    }
    
    private void initialiseReaderAndParser(Reader fileReader) throws IOException{
        reader = new CSVReader(fileReader);
        parser = new PersonParser(configuration, readOneLine());
    }
    
    private String[] readOneLine() throws IOException{
        try {
            return reader.readNext();
        } catch (CsvValidationException ex) {
            throw new IOException("Something went wrong in the CSV Parser", ex);
        }
    }
}

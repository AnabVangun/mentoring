package mentoring.io;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import mentoring.configuration.PersonConfiguration;

public class PersonFileParser {
    private final CSVReader reader;
    private final PersonParser parser;
    
    public PersonFileParser(Reader fileReader, PersonConfiguration configuration) throws IOException{
        reader = new CSVReader(fileReader);
        try {
            parser = new PersonParser(configuration, reader.readNext());
        } catch (CsvValidationException ex) {
            throw new IOException("Something went wrong in the CSV Parser", ex);
        }
    }
    
    public List<Person> parse() throws IOException{
        List<Person> result = new ArrayList<>();
        for (String[] line: reader){
            result.add(parser.parseLine(line));
        }
        return result;
    }
}

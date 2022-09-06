package mentoring.io;

import mentoring.datastructure.Person;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mentoring.configuration.PersonConfiguration;

/**
 * A parser for CSV files based on a schema defined at runtime.
 * 
 * <p>This class is not thread-safe but is safe for reuse: if two files use the same configuration, 
 * the same instance can parse them both sequentially.
 */
public final class PersonFileParser {
    private final PersonConfiguration configuration;
    private CSVReader reader;
    private PersonParser parser;
    
    /**
     * Initialises a parser.
     * @param configuration of the files to parse.
     */
    public PersonFileParser(PersonConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration);
    }
    
    /**
     * Parses a file corresponding to this parser's configuration.
     * @param fileReader providing the data to parse
     * @return the persons contained in the file. There is no guarantee that the person will be in 
     *      the same order as in the file.
     * @throws IOException if the file cannot be read or does not correspond to its configuration.
     */
    public List<Person> parse(Reader fileReader) throws IOException{
        initialiseParserAndReader(fileReader);
        List<Person> result = new ArrayList<>();
        for (String[] line: reader){
            result.add(parser.parseLine(line));
        }
        return result;
    }
    
    private void initialiseParserAndReader(Reader fileReader) throws IOException{
        reader = new CSVReader(fileReader);
        try {
            parser = new PersonParser(configuration, reader.readNext());
        } catch (CsvValidationException e){
            throw new IOException("Something went wrong in the CSV Parser", e);
        }
    }
}

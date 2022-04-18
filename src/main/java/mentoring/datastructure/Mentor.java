package mentoring.datastructure;

import com.opencsv.bean.CsvBindByName;

/**
 * This bean represents the answers of a mentor to the registration form.
 * It is highly coupled to the format of input CSV files.
 */
@Deprecated
public class Mentor extends Person{
    
    @CsvBindByName(column="Promotion", required=true)
    private int year;
    
    public int getYear(){
        return this.year;
    }
    @Override
    public String toString(){
        return "Mentor " + super.toString() + "(" + year + ")";
    }
}

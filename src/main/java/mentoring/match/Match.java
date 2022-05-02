package mentoring.match;
//TODO test for untold hypotheses: null values, negative and zero cost
public class Match<Mentee, Mentor> {
    private final int cost;
    private final Mentee mentee;
    private final Mentor mentor;
    
    Match(Mentee mentee, Mentor mentor, int cost){
        this.mentee = mentee;
        this.mentor = mentor;
        this.cost = cost;
    }
    
    public int getCost(){
        return this.cost;
    }
    
    public Mentor getMentor(){
        return this.mentor;
    }
    
    public Mentee getMentee(){
        return this.mentee;
    }
    
    @Override
    public String toString(){
        return String.format("Mentee %s with mentor %s at cost %s", 
                this.mentee, this.mentor, this.cost);
    }
}

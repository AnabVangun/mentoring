package mentoring.match;

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
}

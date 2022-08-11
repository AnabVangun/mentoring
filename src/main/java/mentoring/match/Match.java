package mentoring.match;

public final class Match<Mentee, Mentor> {
    private final int cost;
    private final Mentee mentee;
    private final Mentor mentor;
    
    Match(Mentee mentee, Mentor mentor, int cost){
        this.mentee = mentee;
        this.mentor = mentor;
        this.cost = cost;
    }
    
    public Mentee getMentee(){
        return this.mentee;
    }
    
    public Mentor getMentor(){
        return this.mentor;
    }
    
    public int getCost(){
        return this.cost;
    }
    
    @Override
    public String toString(){
        return String.format("Mentee %s with mentor %s at cost %s", 
                this.mentee, this.mentor, this.cost);
    }
    
    @Override
    public boolean equals(Object o){
        if (! (o instanceof Match)){
            return false;
        }
        Match cast = (Match) o;
        return mentee.equals(cast.mentee) && mentor.equals(cast.mentor) && cost == cast.cost;
    }
    
    @Override
    public int hashCode(){
        return Integer.hashCode(cost)+31*(mentor.hashCode() + 31*mentee.hashCode());
    }
}

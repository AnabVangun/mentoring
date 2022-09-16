package mentoring.match;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import test.tools.TestArgs;
import test.tools.TestFramework;

public final class MatchesTest implements TestFramework<MatchesTest.MatchesArgs>{

    @Override
    public Stream<MatchesArgs> argumentsSupplier() {
        String identicalMentor = "Identical mentor";
        String identicalMentee = "Identical mentee";
        Match<String, String> identicalMatch = new Match<>(identicalMentee, identicalMentor, 6432);
        return Stream.of(
                new MatchesArgs<>("3 standard matches", List.of(
                        new Match<>("Mentee1","Mentor1",1),
                        new Match<>("Mentee2","Mentor2",2),
                        new Match<>(2,"Mentor3",3))),
                new MatchesArgs<>("3 matches with 2 identical mentors", List.of(
                        new Match<>("Mentee1",identicalMentor,10),
                        new Match<>("Mentee2",identicalMentor,Integer.MAX_VALUE),
                        new Match<>("Mentee3","Mentor3",0))),
                new MatchesArgs<>("4 matches with 2 identical mentees", List.of(
                        new Match<>(identicalMentee,"Mentor1",6234),
                        new Match<>("Mentee2","Mentor2",72),
                        new Match<>("Mentee3","Mentor3",87654),
                        new Match<>(identicalMentee,"Mentor4",12))),
                new MatchesArgs<>("0 match", List.of()),
                new MatchesArgs<>("2 identical matches", List.of(identicalMatch, identicalMatch))
        );
    }
    
    @TestFactory
    @SuppressWarnings("unchecked")
    public Stream<DynamicNode> iteratorTest(){
        return test("iterator()", args -> {
            Matches matches = args.convert();
            Iterator expectedIterator = args.matchesCopy.iterator();
            Iterator actualIterator = matches.iterator();
            Assertions.assertAll(args.matchesCopy.stream().map(ignored -> {
                    return (Executable) () -> 
                            Assertions.assertEquals(expectedIterator.next(), actualIterator.next());
            }));
        });
    }
    
    public static class MatchesArgs<K, V> extends TestArgs{
        final List<Match<K, V>> matches;
        final List<Match<K, V>> matchesCopy = new ArrayList<>();

        public MatchesArgs(String name, List<Match<K, V>> matches){
            super(name);
            this.matches = matches;
            matches.forEach(matchesCopy::add);
        }
        
        public MatchesArgs(List<Pair<? extends K, ? extends V>> matches){
            this("", matches.stream().map(pair -> new Match<>(pair.getLeft(), pair.getRight(), 0))
                    .collect(Collectors.toList()));
        }
        
        public Matches<K, V> convert(){
            return new Matches<>(matches);
        }
    }
}
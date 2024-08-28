package mentoring.match;

import java.time.Duration;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import mentoring.configuration.CriteriaConfiguration;
import mentoring.match.MatchesBuilderHandlerTest.MatchesBuilderHandlerArgs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import test.tools.TestArgs;
import test.tools.TestFramework;

final class MatchesBuilderHandlerTest implements TestFramework<MatchesBuilderHandlerArgs>{
    private static ExecutorService executor;
    private static final int PROHIBITIVE_COST = 2000;
    private static final int STANDARD_COST = 5;
    private static final int UNASSIGNED_SCORE = Integer.MAX_VALUE;//FIXME the default value must be stored somewhere, reference it here
    private static final List<ProgressiveCriterion<Integer, Integer>> defaultProgressiveCriterion =
            List.of((mentee, mentor) -> mentee == mentor ? PROHIBITIVE_COST : STANDARD_COST);
    
    @Override
    public Stream<MatchesBuilderHandlerArgs> argumentsSupplier() {
        return singleArgumentSupplier();
    }
    
    private Stream<MatchesBuilderHandlerArgs> singleArgumentSupplier(){
        Matches<Integer,Integer> expectedMatches = new Matches<>(List.of(
                new Match<>(0,1,STANDARD_COST), new Match<>(1,0,STANDARD_COST)));
        return Stream.of(
                new MatchesBuilderHandlerArgs("simple test case", expectedMatches, 
                        new DummyFuture<>(List.of(0,1)), new DummyFuture<>(List.of(0,1)), 
                        new DummyFuture<>(new DummyConfiguration(defaultProgressiveCriterion, 
                                List.of()))));
    }
    
    @BeforeAll
    static void initialise_executor(){
        executor = Executors.newCachedThreadPool();
    }
    
    @TestFactory
    Stream<DynamicNode> getWorks(){
        return test("get() returns the expected builder when called after the necessary setters",
                args -> {
                    MatchesBuilderHandler<Integer, Integer> handler = args.convert();
                    assertMatchesBuilderAsExpected(args.expectedMatches, handler);
                });
    }
    
    @TestFactory
    Stream<DynamicNode> getFailsBeforeNecessarySetters(){
        return test(Stream.of("ad hoc test"), 
                "get() throws an exception when the necessary setters have not been called",
                args -> {
                    MatchesBuilderHandler<String, Number> builder = new MatchesBuilderHandler<>();
                    Assertions.assertThrows(IllegalStateException.class, () -> builder.get());
                    builder.setMenteesSupplier(new DummyFuture<>(List.of("mentee")));
                    Assertions.assertThrows(IllegalStateException.class, () -> builder.get());
                    builder.setMentorsSupplier(new DummyFuture<>(List.of(2)));
                    Assertions.assertThrows(IllegalStateException.class, () -> builder.get());
                    MatchesBuilderHandler<Integer, Integer> otherBuilder = new MatchesBuilderHandler<>();
                    otherBuilder.setCriteriaSupplier(new DummyFuture<>(DummyConfiguration.dummyConfiguration()));
                    Assertions.assertThrows(IllegalStateException.class, () -> otherBuilder.get());
                    otherBuilder.setMenteesSupplier(new DummyFuture<>(List.of(2)));
                    Assertions.assertThrows(IllegalStateException.class, () -> otherBuilder.get());
                });
    }
    
    @TestFactory
    Stream<DynamicNode> setMenteesSupplierSuccessiveCalls(){
        Future<List<Integer>> secondMenteesSupplier = new DummyFuture<>(List.of(12, 63));
        Future<List<Integer>> thirdMenteesSupplier = new DummyFuture<>(List.of(0,3));
        Matches<Integer,Integer> expectedMatches = new Matches<>(List.of(
                new Match<>(0,1,STANDARD_COST), new Match<>(3,0,STANDARD_COST)));
        return test(singleArgumentSupplier(),
                "calling setMenteesSupplier() multiple times retains only the last call", args -> {
                    MatchesBuilderHandler<Integer, Integer> handler = args.convert();
                    handler.setMenteesSupplier(secondMenteesSupplier);
                    handler.setMenteesSupplier(thirdMenteesSupplier);
                    assertMatchesBuilderAsExpected(expectedMatches, handler);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> setMenteesSupplier_failFastOnNullSupplier(){
        return test(singleArgumentSupplier(), "calling setMenteesSupplier() with null fails", args -> {
            MatchesBuilderHandler<Integer, Integer> handler = args.convert();
            Assertions.assertThrows(NullPointerException.class, 
                    () -> handler.setMenteesSupplier(null));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> setMentorsSupplierSuccessiveCalls(){
        Future<List<Integer>> secondMentorsSupplier = new DummyFuture<>(List.of(12, 63));
        Future<List<Integer>> thirdMentorsSupplier = new DummyFuture<>(List.of(0,3));
        Matches<Integer,Integer> expectedMatches = new Matches<>(List.of(
                new Match<>(0,3,STANDARD_COST), new Match<>(1,0,STANDARD_COST)));
        return test(singleArgumentSupplier(),
                "calling setMentorsSupplier() multiple times retains only the last call", args -> {
                    MatchesBuilderHandler<Integer, Integer> handler = args.convert();
                    handler.setMentorsSupplier(secondMentorsSupplier);
                    handler.setMentorsSupplier(thirdMentorsSupplier);
                    assertMatchesBuilderAsExpected(expectedMatches, handler);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> setMentorsSupplier_failFastOnNullSupplier(){
        return test(singleArgumentSupplier(), "calling setMentorsSupplier() with null fails", args -> {
            MatchesBuilderHandler<Integer, Integer> handler = args.convert();
            Assertions.assertThrows(NullPointerException.class,
                    () -> handler.setMentorsSupplier(null));
        });
    }
    
    @TestFactory
    Stream<DynamicNode> setCriteriaSupplierSuccessiveCalls(){
        Future<CriteriaConfiguration<Integer, Integer>> secondCriteriaSupplier = 
                new DummyFuture<>(new DummyConfiguration(
                        List.of((mentee, mentor) -> PROHIBITIVE_COST), List.of()));
        Future<CriteriaConfiguration<Integer, Integer>> thirdCriteriaSupplier = 
                new DummyFuture<>(new DummyConfiguration(
                        List.of((mentee, mentor) -> 
                                mentee == mentor ? STANDARD_COST + 1: STANDARD_COST),
                        List.of((mentee, mentor) -> mentee == mentor)));
        Matches<Integer,Integer> expectedMatches = new Matches<>(List.of(
                new Match<>(0,0,STANDARD_COST + 1), new Match<>(1,1,STANDARD_COST + 1)));
        return test(singleArgumentSupplier(),
                "calling setCriteriaSupplier() multiple times retains only the last call", args -> {
                    MatchesBuilderHandler<Integer, Integer> handler = args.convert();
                    handler.setCriteriaSupplier(secondCriteriaSupplier);
                    handler.setCriteriaSupplier(thirdCriteriaSupplier);
                    assertMatchesBuilderAsExpected(expectedMatches, handler);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> setCriteriaSupplier_failFastOnNullSupplier(){
        return test(singleArgumentSupplier(),
                "calling setCriteriaSupplier() with null fails", args -> {
                    MatchesBuilderHandler<Integer, Integer> handler = args.convert();
                    Assertions.assertThrows(NullPointerException.class,
                            () -> handler.setCriteriaSupplier(null));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> setPlaceholderPersonsSupplierSuccessiveCalls(){
        Future<CriteriaConfiguration<Integer, Integer>> criteriaSupplier = 
                new DummyFuture<>(new DummyConfiguration(defaultProgressiveCriterion, 
                        List.of((mentee, mentor) -> false)));
        Integer placeholderMentee = 6;
        Integer placeholderMentor = 8;
        Future<Integer> firstPlaceholderMentee = new DummyFuture<>(placeholderMentee+3);
        Future<Integer> firstPlaceholderMentor = new DummyFuture<>(placeholderMentor-12);
        Future<Integer> secondPlaceholderMentee = new DummyFuture<>(placeholderMentee);
        Future<Integer> secondPlaceholderMentor = new DummyFuture<>(placeholderMentor);
        Matches<Integer,Integer> expectedMatches = new Matches<>(List.of(
                new Match<>(0,placeholderMentor,UNASSIGNED_SCORE), 
                new Match<>(1,placeholderMentor,UNASSIGNED_SCORE),
                new Match<>(placeholderMentee,0,UNASSIGNED_SCORE),
                new Match<>(placeholderMentee,1,UNASSIGNED_SCORE)));
        return test(singleArgumentSupplier(),
                "calling setPlaceholderPersonsSupplier() multiple times retains only the last call", args -> {
                    MatchesBuilderHandler<Integer, Integer> handler = args.convert();
                    handler.setCriteriaSupplier(criteriaSupplier);
                    handler.setPlaceholderPersonsSupplier(firstPlaceholderMentee, firstPlaceholderMentor);
                    handler.setPlaceholderPersonsSupplier(secondPlaceholderMentee, secondPlaceholderMentor);
                    assertMatchesBuilderAsExpected(expectedMatches, handler);
        });
    }
    
    @TestFactory
    Stream<DynamicNode> setPlaceholderPersonsSupplier_resetOnBothNull(){
        Future<Integer> overridenPlaceholderMentee = new DummyFuture<>(6);
        Future<Integer> overridenPlaceholderMentor = new DummyFuture<>(8);
        return test(singleArgumentSupplier(), 
                "calling setPlaceholderPersonsSupplier() with null resets the placeholders", args -> {
                    MatchesBuilderHandler<Integer, Integer> handler = args.convert();
                    handler.setPlaceholderPersonsSupplier(overridenPlaceholderMentee, 
                            overridenPlaceholderMentor);
                    handler.setPlaceholderPersonsSupplier(null, null);
                    assertMatchesBuilderAsExpected(args.expectedMatches, handler);
                });
    }
    
    @TestFactory
    Stream<DynamicNode> setPlaceholderPersonsSupplier_failOnNullMentee(){
        Future<Integer> placeholderMentor = new DummyFuture<>(8);
        return test(singleArgumentSupplier(),
                "calling setPlaceholderPersonsSupplier() with null mentee and non-null mentor fails", args -> {
                    MatchesBuilderHandler<Integer, Integer> handler = args.convert();
                    Assertions.assertThrows(IllegalArgumentException.class,
                            () -> handler.setPlaceholderPersonsSupplier(null, placeholderMentor));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> setPlaceholderPersonsSupplier_failOnNullMentor(){
        Future<Integer> placeholderMentee = new DummyFuture<>(6);
        return test(singleArgumentSupplier(),
                "calling setPlaceholderPersonsSupplier() with non-null mentee and null mentor fails", args -> {
                    MatchesBuilderHandler<Integer, Integer> handler = args.convert();
                    Assertions.assertThrows(IllegalArgumentException.class,
                            () -> handler.setPlaceholderPersonsSupplier(placeholderMentee, null));
                });
    }
    
    @TestFactory
    Stream<DynamicNode> getIsNotChangedByConcurrentSetters(){
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch stopSignal = new CountDownLatch(2);
        FutureTask<List<Integer>> secondMenteesSupplier = new FutureTask<>(() -> {
            //startSignal used to block handler in get()
            startSignal.await();
            return List.of(0,3);
        });
        executor.execute(secondMenteesSupplier);
        Future<List<Integer>> secondMentorsSupplier = new DummyFuture<>(List.of(12, 63));
        Future<CriteriaConfiguration<Integer, Integer>> criteriaSupplier =
                new DummyFuture<>(new DummyConfiguration(defaultProgressiveCriterion,
                        List.of((mentee, mentor) -> mentee == mentor)));
        Matches<Integer,Integer> expectedMatches = new Matches<>(List.of(
                new Match<>(0,1,STANDARD_COST), new Match<>(3,0,STANDARD_COST)));
        return test(singleArgumentSupplier(),
                "get() does not take into account setters called during its run", args -> {
                    MatchesBuilderHandler<Integer, Integer> handler = args.convert();
                    handler.setMenteesSupplier(secondMenteesSupplier);
                    Future<?> result = executor.submit(() -> {
                        try{
                            assertMatchesBuilderAsExpected(expectedMatches, handler);
                        } finally {
                        stopSignal.countDown();
                        }
                    });
                    Thread otherSetter = new Thread(() -> {
                        handler.setMentorsSupplier(secondMentorsSupplier);
                        handler.setCriteriaSupplier(criteriaSupplier);
                        stopSignal.countDown();
                            });
                    otherSetter.start();//ran as a thread to pool its state
                    EnumSet<Thread.State> blockedOrFinishedState = EnumSet.of(Thread.State.BLOCKED,
                            Thread.State.TERMINATED, Thread.State.TIMED_WAITING, Thread.State.WAITING);
                    Assertions.assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
                        while (! blockedOrFinishedState.contains(otherSetter.getState())){
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ex) {
                                Assertions.fail("something went wrong in test", ex);
                            }
                    }});
                    startSignal.countDown();
                    Assertions.assertTimeoutPreemptively(Duration.ofMillis(500), () -> stopSignal.await());
                    try {
                        Assertions.assertNull(result.get());
                    } catch (InterruptedException | ExecutionException ex) {
                        Assertions.fail(ex.getMessage());
                    }
        });
    }
    
    @TestFactory
    Stream<DynamicNode> nextGetIsChangedByConcurrentSetters(){
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch stopSignal = new CountDownLatch(2);
        FutureTask<List<Integer>> secondMenteesSupplier = new FutureTask<>(() -> {
            //startSignal used to block handler in get()
            startSignal.await();
            return List.of(0,3);
                });
        executor.execute(secondMenteesSupplier);
        Future<List<Integer>> secondMentorsSupplier = new DummyFuture<>(List.of(6, 3));
        Future<CriteriaConfiguration<Integer, Integer>> criteriaSupplier =
                new DummyFuture<>(new DummyConfiguration(defaultProgressiveCriterion,
                        List.of((mentee, mentor) -> mentee == mentor)));
        Integer defaultMentee = 15;
        Future<Integer> defaultMenteeSupplier = new DummyFuture<>(defaultMentee);
        Integer defaultMentor = 23;
        Future<Integer> defaultMentorSupplier = new DummyFuture<>(defaultMentor);
        Matches<Integer,Integer> expectedMatches = new Matches<>(List.of(
                new Match<>(3,3,PROHIBITIVE_COST), new Match<>(0,defaultMentor,UNASSIGNED_SCORE),
                new Match<>(defaultMentee,6,UNASSIGNED_SCORE)));
        return test(singleArgumentSupplier(),
                "the next call to get() takes into account setters called during a previous get", args -> {
                    MatchesBuilderHandler<Integer, Integer> handler = args.convert();
                    handler.setMenteesSupplier(secondMenteesSupplier);
                    Future<?> result = executor.submit(() -> {
                        try{
                            Assertions.assertDoesNotThrow(() -> handler.get());
                        } finally {
                        stopSignal.countDown();
                            }});
                    Thread otherSetter = new Thread(() -> {
                        handler.setMentorsSupplier(secondMentorsSupplier);
                        handler.setCriteriaSupplier(criteriaSupplier);
                        handler.setPlaceholderPersonsSupplier(defaultMenteeSupplier, defaultMentorSupplier);
                        stopSignal.countDown();
                            });
                    otherSetter.start();
                    EnumSet<Thread.State> blockedOrFinishedState = EnumSet.of(Thread.State.BLOCKED,
                            Thread.State.TERMINATED, Thread.State.TIMED_WAITING, Thread.State.WAITING);
                    Assertions.assertTimeoutPreemptively(Duration.ofMillis(5000), () -> {
                        while (! blockedOrFinishedState.contains(otherSetter.getState())){
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ex) {
                                Assertions.fail("something went wrong in test", ex);
                            }
                    }});
                    startSignal.countDown();
                    Assertions.assertTimeoutPreemptively(Duration.ofMillis(500), () -> stopSignal.await());
                    try {
                        Assertions.assertNull(result.get());
                    } catch (InterruptedException | ExecutionException ex) {
                        Assertions.fail(ex.getMessage());
                    }
                    assertMatchesBuilderAsExpected(expectedMatches, handler);
                });
    }
    
    static void assertMatchesBuilderAsExpected(Matches<Integer, Integer> expectedMatches,
            MatchesBuilderHandler<Integer, Integer> actual){
        try{
            MatchesBuilder<Integer, Integer> builder = actual.get();
            Matches<Integer, Integer> actualMatches = builder.build();
            MatchesBuilderTest.assertMatchesEquals(expectedMatches, actualMatches);
        } catch (InterruptedException | ExecutionException e){
            Assertions.fail(e);
        }
    }
    
    static class MatchesBuilderHandlerArgs extends TestArgs {
        final Matches<Integer,Integer> expectedMatches;
        final Future<List<Integer>> menteesSupplier;
        final Future<List<Integer>> mentorsSupplier;
        final Future<CriteriaConfiguration<Integer, Integer>> criteriaSupplier;
        
        MatchesBuilderHandlerArgs(String string, Matches<Integer, Integer> expectedMatches, 
                Future<List<Integer>> menteesSupplier, Future<List<Integer>> mentorsSupplier,
                Future<CriteriaConfiguration<Integer, Integer>> criteriaSupplier) {
            super(string);
            this.expectedMatches = expectedMatches;
            this.menteesSupplier = menteesSupplier;
            this.mentorsSupplier = mentorsSupplier;
            this.criteriaSupplier = criteriaSupplier;
        }
        
        MatchesBuilderHandler<Integer, Integer> convert(){
            MatchesBuilderHandler<Integer, Integer> result = new MatchesBuilderHandler<>();
            result.setMenteesSupplier(menteesSupplier);
            result.setMentorsSupplier(mentorsSupplier);
            result.setCriteriaSupplier(criteriaSupplier);
            return result;
        }
    }
    
    static class DummyFuture<V> implements Future<V> {
        private final V value;
        
        DummyFuture(V value){
            this.value = value;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            return value;
        }

        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return get();
        }
    }
    
    static class DummyConfiguration extends CriteriaConfiguration<Integer, Integer> {
        
        private final List<ProgressiveCriterion<Integer, Integer>> progressiveCriteria;
        private final List<NecessaryCriterion<Integer, Integer>> necessaryCriteria;
        
        DummyConfiguration(List<ProgressiveCriterion<Integer, Integer>> progressiveCriteria,
                List<NecessaryCriterion<Integer, Integer>> necessaryCriteria){
            super("ad-hoc configuration");
            this.progressiveCriteria = progressiveCriteria;
            this.necessaryCriteria = necessaryCriteria;
        }

        @Override
        public Collection<ProgressiveCriterion<Integer, Integer>> getProgressiveCriteria() {
            return progressiveCriteria;
        }

        @Override
        public List<NecessaryCriterion<Integer, Integer>> getNecessaryCriteria() {
            return necessaryCriteria;
        }
        
        static DummyConfiguration dummyConfiguration(){
            return new DummyConfiguration(List.of(), List.of());
        }
    }
}

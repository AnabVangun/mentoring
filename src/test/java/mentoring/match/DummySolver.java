package mentoring.match;

import assignmentproblem.CostMatrix;
import assignmentproblem.Result;
import assignmentproblem.Solver;
import java.util.List;

public class DummySolver implements Solver{
    
    final Result expectedResult;

    DummySolver(){
        this.expectedResult = new DummyResult();
    }
    
    DummySolver(List<Integer> rows, List<Integer> columns){
        this.expectedResult = new DummyResult(rows, columns);
    }

    @Override
    public Result solve(CostMatrix t) {
        return this.expectedResult;
    }

    @Override
    public Result solve(int[][] ints) throws IllegalArgumentException {
        return this.expectedResult;
    }

    static class DummyResult extends Result{
        private final List<Integer> rows;
        private final List<Integer> columns;
        
        DummyResult(){
            super(null);
            rows = List.of();
            columns = List.of();
        }
        
        DummyResult(List<Integer> rows, List<Integer> columns){
            super(null);
            this.rows = rows;
            this.columns = columns;
        }
        
        @Override
        public List<Integer> getRowAssignments() {
            return rows;
        }

        @Override
        public List<Integer> getColumnAssignments() {
            return columns;
        }
    }
}
package math;

import java.util.List;
import java.util.function.Function;

public interface VariationalSolver {
    public static final double EPSILON = 0.001;
    public List<Interpolator> solve(Function<List<Double>, Double> function, PointDouble start, PointDouble end);
}

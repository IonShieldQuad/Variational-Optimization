package math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class DirectSolver implements VariationalSolver {
    public static final int STEPS = 100;
    
    @Override
    public List<Interpolator> solve(Function<List<Double>, Double> function, PointDouble start, PointDouble end) {
        SolverND solver = new ChainGradientSolver();
        List<Double> in = new ArrayList<>();
        double delta = (end.getX() - start.getX()) / (STEPS + 1.0);
        for (int i = 0; i < STEPS; i++) {
            double alpha = i / (STEPS + 2.0);
            //in.add(start.getY() * (1 - alpha) + end.getY() * alpha);
            in.add(1.0);
            //in.add(Math.random());
        }
        solver.setF(l -> functionalValue(function, l, start, end));
        List<Double> result = solver.solve(in);
        List<PointDouble> interpolationPoints = new ArrayList<>();
        interpolationPoints.add(start);
        for (int i = 0; i < result.size(); i++) {
            interpolationPoints.add(new PointDouble(start.getX() + delta * (i + 1), result.get(i)));
        }
        interpolationPoints.add(end);
        
        return new ArrayList<>(Collections.singletonList(new LinearInterpolator(interpolationPoints)));
    }
    
    private double functionalValue(Function<List<Double>, Double> function, List<Double> values, PointDouble start, PointDouble end) {
        double res = 0;
        
        double delta = (end.getX() - start.getX()) / (STEPS + 1.0);
    
        double currT;
        double prevT = start.getX();
        double currX;
        double prevX = start.getY();
        double currZ;
        double prevZ = 0;
        double prevPrevF = 0;
        double prevF;
        
        for (int i = 0; i <= STEPS; i++) {
            if (i == STEPS) {
                currX = end.getY();
            }
            else {
                currX = values.get(i);
            }
            currT = start.getX() + (i + 1) * delta;
            currZ = (currX - prevX) / delta;
            prevF = function.apply(Arrays.asList(prevT, prevX, currZ));
            if (i != 0) {
                double area = ((prevPrevF + prevF) / 2.0) * delta;
                res += area;
            }
            
            prevT = currT;
            prevX = currX;
            prevZ = currZ;
            prevPrevF = prevF;
        }
        prevF = function.apply(Arrays.asList(prevT, prevX, prevZ));
        double area = ((prevPrevF + prevF) / 2.0) * delta;
        res += area;
        
        return res;
    }
}

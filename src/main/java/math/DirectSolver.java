package math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class DirectSolver implements VariationalSolver {
    private int steps = 20;
    
    public DirectSolver(int steps) {
        this.steps = steps;
    }
    
    @Override
    public List<Interpolator> solve(Function<List<Double>, Double> function, PointDouble start, PointDouble end) {
        SolverND solver = new GaussSeidelSolver();
        List<Double> in = new ArrayList<>();
        double delta = (end.getX() - start.getX()) / (steps + 1.0);
        for (int i = 0; i < steps; i++) {
            double alpha = i / (steps + 2.0);
            //in.add(start.getY() * (1 - alpha) + end.getY() * alpha);
            //in.add(1.0);
            in.add(Math.random());
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
        
        double delta = (end.getX() - start.getX()) / (steps + 1.0);
    
        double nextT;
        double currT = start.getX();
        double nextX;
        double currX = start.getY();
        double currZ = 0;
        double prevZ = 0;
        double currF = 0;
        double prevF = 0;
        
        for (int i = 0; i <= steps + 1; i++) {
            if (i == 0) {
                currX = start.getY();
            }
            else {
                if (i == steps + 1) {
                    currX = end.getY();
                } else {
                    currX = values.get(i - 1);
                }
            }
            if (i == steps + 1 || i == steps) {
                nextX = end.getY();
            }
            else {
                nextX = values.get(i);
            }
            currT = start.getX() + i * delta;
            nextT = currT + delta;
            
            if (i != steps + 1) {
                currZ = (nextX - currX) / delta;
            }
            
            if (i != steps + 1) {
                currF = function.apply(Arrays.asList(currT, currX, currZ));
            }
            else {
                currF = 0;
            }
            
            if (i != 0) {
                double area = ((currF + prevF) / 2.0) * delta;
                
                res += area;
            }
            prevF = currF;
        }
        /*prevF = function.apply(Arrays.asList(prevT, prevX, prevZ));
        double area = ((prevPrevF + prevF) / 2.0) * delta;
        res += area;*/
        
        return res;
    }
}

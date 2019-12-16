package math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class BallisticSolver implements VariationalSolver {
    public static final double INITIAL_Z = 1;
    public static final int STEPS = 100;
    @Override
    public List<Interpolator> solve(Function<List<Double>, Double> function, PointDouble start, PointDouble end) {
        List<Interpolator> outList = new ArrayList<>();
        
        SolverUtils.findMin(z -> Math.abs(findError(function, z, start, end, outList)),1, INITIAL_Z);
        return outList;
    }
    
    private double findError(Function<List<Double>, Double> function, double z0, PointDouble start, PointDouble end, List<Interpolator> outSequenceList) {
        double deltaT = (end.getX() - start.getX()) / (double)STEPS;
        double currT = start.getX();
        double currX = start.getY();
        double currZ = z0;
        double nextX;
        double nextZ;
        List<PointDouble> points = new ArrayList<>();
        
        for (int i = 0; i <= STEPS; i++) {
        points.add(new PointDouble(currT, currX));
        
        if (i == STEPS) {
            break;
        }
        
        nextX = currX + currZ * deltaT;
        nextZ = currZ + function.apply(Arrays.asList(currT, currX, currZ)) * deltaT;
        
        currT += deltaT;
        currX = nextX;
        currZ = nextZ;
        }
        outSequenceList.add(new LinearInterpolator(points));
        
        return end.getY() - currX;
    }
}

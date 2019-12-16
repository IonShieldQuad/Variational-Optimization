package math;

import java.util.ArrayList;
import java.util.List;

public class GaussSeidelSolver extends SolverND {
    private static final int I_MAX = 1024;
    
    @Override
    protected List<Double> solveInternal(List<Double> data) {
    
        double axisScale = 1;
        List<Double> startPoint = data;
        int size = startPoint.size();
    
        List<Double> prev = new ArrayList<>(startPoint);
        List<Double> curr = new ArrayList<>(startPoint);
        int i = 0;
        boolean cont = true;
        
        while(!(i > I_MAX || !cont)) {
            prev = curr;
            List<Double> res;
            
            //addToLog(i + ") Begin: " + new PointDouble(currX, currY).toString(PRECISION));
    
            for (int j = 0; j < size; j++) {
                List<Double> axis = new ArrayList<>();
                for (int k = 0; k < size; k++) {
                    axis.add(j == k ? axisScale : 0);
                }
                res = SolverUtils.findMinOnAxis(getF(), axis, curr);
                curr = new ArrayList<>(res);
            }
            
            i++;
            double val = 0;
            for (int j = 0; j < size; j++) {
                val += Math.pow(curr.get(j) - prev.get(j), 2);
            }
            val = Math.sqrt(val);
            cont = val > EPSILON;
        }
    
        return curr;
    }
    
    @Override
    protected int getLogBatchSize() {
        return 1;
    }
    
}

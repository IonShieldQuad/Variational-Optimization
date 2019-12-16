package math;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChainGradientSolver extends SolverND {
    private int I_MAX = 0x00000fff;
    private double DESC_RATE = 1;
    
    @Override
    protected List<Double> solveInternal(List<Double> data) {
        
        List<Double> curr = data;
        List<Double> prev = new ArrayList<>(curr);
        List<Double> grad = gradient(curr);
        List<Double> s = grad.stream().map(v -> -DESC_RATE * v).collect(Collectors.toList());
        List<Double> prevS;
        List<Double> prevGrad;
        int i = 0;
        double delta;
        double beta;
        do {
            prev = curr;
            prevGrad = grad;
            prevS = s;
            grad = gradient(prev);
            Optional<Double> b = grad.stream().map(v -> v * v).reduce(Double::sum);
            Optional<Double> pb = prevGrad.stream().map(v -> v * v).reduce(Double::sum);
            beta = b.orElse(1.0) / pb.orElse(1.0);
            s = new ArrayList<>();
            if (i > 0) {
                for (int j = 0; j < grad.size(); j++) {
                    s.add(-grad.get(j) * DESC_RATE + prevS.get(j) * beta);
                }
            }
            else {
                s = grad.stream().map(v -> -v * DESC_RATE).collect(Collectors.toList());
            }
            curr = SolverUtils.findMinOnAxis(getF(), s, prev);
            
            addPoint(prev);
            addPoint(curr);
            addToLog(i + ") Start = " + prev.toString() + "; End = " + curr.toString() + "; Gradient = " + grad.toString() + "; Beta = " + beta);
            delta = 0;
            for (int j = 0; j < curr.size(); j++) {
                delta += Math.pow(curr.get(j) - prev.get(j), 2);
            }
            delta = Math.sqrt(delta);
            
            i++;
        } while (i < I_MAX && delta > EPSILON);
        addPoint(prev);
        addPoint(curr);
        addToLog(i + ") Start = " + prev.toString() + "; End = " + curr.toString() + "; Gradient = " + grad.toString() + "; Beta = " + beta);
        return curr;
    }
    
    @Override
    protected int getLogBatchSize() {
        return 1;
    }
}

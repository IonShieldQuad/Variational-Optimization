package math;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class SolverND {
    public static final Double EPSILON = 0.001;
    public static int PRECISION = 3;
    private Function<List<Double>, Double> f;
    private List<String> log = new ArrayList<>();
    private List<List<Double>> points = new ArrayList<>();
    
    public SolverND(){}
    public SolverND(Function<List<Double>, Double> f) {
        this.f = f;
    }

    public Function<List<Double>, Double> getF() {
        return this.f;
    }

    public void setF(Function<List<Double>, Double> f) {
        this.f = f;
    }
    
    public List<Double> solve(List<Double> data) {
        points.clear();
        log.clear();
        return solveInternal(data);
    }
    
    protected abstract List<Double> solveInternal(List<Double> data);
    protected abstract int getLogBatchSize();

    protected void addToLog(String value) {
        log.add(value);
    }
    protected void addPoint(List<Double> point) {
        points.add(point);
    }

    /**@return Returns log as a list of string, each representing a solution step*/
    public List<String> getSolutionLog() {
        List<String> strings = new ArrayList<>();
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < this.log.size(); ++i) {
            if (i % getLogBatchSize() == 0 && string.length() != 0) {
                strings.add(string.toString());
                string = new StringBuilder();
            }
            string.append(this.log.get(i)).append(" ");
        }
        if (string.length() != 0) {
            strings.add(string.toString());
        }
        return strings;
    }
    
    public List<List<Double>> getPoints() {
        return points;
    }
    
    public List<Double> gradient(List<Double> point) {
        List<Double> grad = new ArrayList<>();
        for (int i = 0; i < point.size(); i++) {
            grad.add(dfdk(point, i));
        }
        return grad;
    }
    
    public double dfdk(List<Double> point, int index, int order) {
        if (order < 0) {
            throw new IllegalArgumentException("Derivative order has to be non-negative");
        }
        if (index >= point.size()) {
            throw new IllegalArgumentException("Point doesn't contain dimension " + index);
        }
        if (order == 0) {
            return f.apply(point);
        }
        List<Double> shifted = new ArrayList<>();
        for (int i = 0; i < point.size(); i++) {
            if (i == index) {
                shifted.add(point.get(i) + EPSILON);
            }
            shifted.add(point.get(i));
        }
        return (dfdk(shifted, index, order - 1) - dfdk(point, index, order - 1)) / EPSILON;
    }
    
    public double dfdk(List<Double> point, int index) {
        return dfdk(point, index, 1);
    }
    
}

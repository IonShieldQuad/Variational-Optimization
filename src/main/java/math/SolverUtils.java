package math;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class SolverUtils {
    public static final Double EPSILON = 0.001;
    
    public static PointDouble gradient(BiFunction<Double, Double, Double> f, PointDouble point) {
        return new PointDouble(fdx(f, point, 1), fdy(f, point, 1));
    }
    
    public static double fdx(BiFunction<Double, Double, Double> f, PointDouble point, int order) {
        if (order < 0) {
            throw new IllegalArgumentException("Derivative order has to be non-negative");
        }
        if (order == 0) {
            return f.apply(point.getX(), point.getY());
        }
        return (fdx(f, point.add(EPSILON, 0), order - 1) - fdx(f, point, order - 1)) / EPSILON;
    }
    
    public static double fdx(BiFunction<Double, Double, Double> f, PointDouble point) {
        return fdx(f, point, 1);
    }
    
    public static double fdy(BiFunction<Double, Double, Double> f, PointDouble point, int order) {
        if (order < 0) {
            throw new IllegalArgumentException("Derivative order has to be non-negative");
        }
        if (order == 0) {
            return f.apply(point.getX(), point.getY());
        }
        return (fdy(f, point.add(0, EPSILON), order - 1) - fdy(f, point, order - 1)) / EPSILON;
    }
    
    public static double fdy(BiFunction<Double, Double, Double> f, PointDouble point) {
        return fdy(f, point, 1);
    }
    
    public static double findMin(Function<Double, Double> f, double scale, double startValue) {
    
        int i = 0;
        double prevX = startValue;
        double currX = startValue;
        double nextX = currX + scale * Math.pow(2, i);
        i++;
        do {
            nextX = currX + scale * Math.pow(2, i);
            if (f.apply(currX) >= f.apply(nextX)) {
                //Positive direction
                while (f.apply(currX) > f.apply(nextX)) {
                    prevX = currX;
                    currX = nextX;
                    nextX = currX + scale * Math.pow(2, i);
                    i++;
                }
                double minX = prevX;
                double maxX = nextX;
                Solver1D solver = new GoldenRatioSolver();
                solver.setF(f);
                PointDouble res = solver.solve(Math.min(minX, maxX), Math.max(minX, maxX));
                return res.getX();
            }
            nextX = currX - scale * Math.pow(2, i - 1);
            if (f.apply(currX) > f.apply(nextX)) {
                //Negative direction
                while (f.apply(currX) >= f.apply(nextX)) {
                    prevX = currX;
                    currX = nextX;
                    nextX = currX - scale * Math.pow(2, i);
                    i++;
                }
                double minX = nextX;
                double maxX = prevX;
                Solver1D solver = new GoldenRatioSolver();
                solver.setF(f);
                PointDouble res = solver.solve(Math.min(minX, maxX), Math.max(minX, maxX));
                return res.getX();
            }
            scale /= 2;
        } while (scale > EPSILON);
        return startValue;
    }
    
    public static PointDouble findMinOnAxis(BiFunction<Double, Double, Double> f, PointDouble axis, PointDouble startPoint) {
        
        int i = 0;
        double prevX = startPoint.getX();
        double prevY = startPoint.getY();
        double currX = startPoint.getX();
        double currY = startPoint.getY();
        double nextX = currX + axis.getX() * Math.pow(2, i);
        double nextY = currY + axis.getY() * Math.pow(2, i);
        i++;
        
        do {
            nextX = currX + axis.getX() * Math.pow(2, i);
            nextY = currY + axis.getY() * Math.pow(2, i);
            if (f.apply(currX, currY) >= f.apply(nextX, nextY)) {
                //Positive direction
                while (f.apply(currX, currY) > f.apply(nextX, nextY)) {
                    prevX = currX;
                    prevY = currY;
                    currX = nextX;
                    currY = nextY;
                    nextX = currX + axis.getX() * Math.pow(2, i);
                    nextY = currY + axis.getY() * Math.pow(2, i);
                    i++;
                }
                double minX = prevX;
                double maxX = nextX;
                double minY = prevY;
                double maxY = nextY;
                Solver1D solver = new GoldenRatioSolver();
                solver.setF(a -> f.apply(minX * (1 - a) + maxX * a, minY * (1 - a) + maxY * a));
                PointDouble res = solver.solve(0, 1);
                PointDouble point = new PointDouble(maxX * res.getX() + minX * (1 - res.getX()), maxY * res.getX() + minY * (1 - res.getX()));
                return point;
            }
            nextX = currX - axis.getX() * Math.pow(2, i - 1);
            nextY = currY - axis.getY() * Math.pow(2, i - 1);
            if (f.apply(currX, currY) > f.apply(nextX, nextY)) {
                //Negative direction
                while (f.apply(currX, currY) >= f.apply(nextX, nextY)) {
                    prevX = currX;
                    prevY = currY;
                    currX = nextX;
                    currY = nextY;
                    nextX = currX - axis.getX() * Math.pow(2, i);
                    nextY = currY - axis.getY() * Math.pow(2, i);
                    i++;
                }
                double minX = prevX;
                double maxX = nextX;
                double minY = prevY;
                double maxY = nextY;
                Solver1D solver = new GoldenRatioSolver();
                solver.setF(a -> f.apply(minX * (1 - a) + maxX * a, minY * (1 - a) + maxY * a));
                PointDouble res = solver.solve(0, 1);
                PointDouble point = new PointDouble(maxX * res.getX() + minX * (1 - res.getX()), maxY * res.getX() + minY * (1 - res.getX()));
                return point;
            }
            
            axis = axis.scale(0.5);
        } while (axis.length() > EPSILON);
        return startPoint;
    }
    
    public static List<Double> findMinOnAxis(Function<List<Double>, Double> f, List<Double> axis, List<Double> startPoint) {
        if (axis.size() != startPoint.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int size = axis.size();
        int i = 0;
        List<Double> prev = new ArrayList<>(startPoint);
        List<Double> curr = new ArrayList<>(startPoint);
        List<Double> next = new ArrayList<>(startPoint);
    
        for (int j = 0; j < size; j++) {
            next.set(j, curr.get(j) + axis.get(j) * Math.pow(2, i));
        }
    
        double length = 0;
        for (int j = 0; j < size; j++) {
            length += Math.pow(axis.get(j), 2);
        }
        length = Math.sqrt(length);
        
        i++;
        
        do {
            for (int j = 0; j < size; j++) {
                next.set(j, curr.get(j) + axis.get(j) * Math.pow(2, i));
            }
            if (f.apply(curr) >= f.apply(next)) {
                //Positive direction
                while (f.apply(curr) > f.apply(next)) {
                    for (int j = 0; j < size; j++) {
                        prev.set(j, curr.get(j));
                    }
                    for (int j = 0; j < size; j++) {
                        curr.set(j, next.get(j));
                    }
                    for (int j = 0; j < size; j++) {
                        next.set(j, curr.get(j) + axis.get(j) * Math.pow(2, i));
                    }
                    i++;
                }
                List<Double> min = prev;
                List<Double> max = next;

                Solver1D solver = new GoldenRatioSolver();
                solver.setF(a -> {
                    List<Double> l = new ArrayList<>();
                    for (int j = 0; j < size; j++) {
                        l.add(min.get(j) * (1 - a) + max.get(j) * a);
                    }
                    return f.apply(l);
                });
                double res = solver.solve(0, 1).getX();
                List<Double> point = new ArrayList<>();
                for (int j = 0; j < size; j++) {
                    point.add(max.get(j) * res + min.get(j) * (1 - res));
                }
                return point;
            }
            for (int j = 0; j < size; j++) {
                next.set(j, curr.get(j) - axis.get(j) * Math.pow(2, i));
            }
            if (f.apply(curr) > f.apply(next)) {
                //Negative direction
                while (f.apply(curr) >= f.apply(next)) {
                    for (int j = 0; j < size; j++) {
                        prev.set(j, curr.get(j));
                    }
                    for (int j = 0; j < size; j++) {
                        curr.set(j, next.get(j));
                    }
                    for (int j = 0; j < size; j++) {
                        next.set(j, curr.get(j) - axis.get(j) * Math.pow(2, i));
                    }
                    i++;
                }
                List<Double> min = prev;
                List<Double> max = next;
                
                Solver1D solver = new GoldenRatioSolver();
                solver.setF(a -> {
                    List<Double> l = new ArrayList<>();
                    for (int j = 0; j < size; j++) {
                        l.add(min.get(j) * (1 - a) + max.get(j) * a);
                    }
                    return f.apply(l);
                });
                double res = solver.solve(0, 1).getX();
                List<Double> point = new ArrayList<>();
                for (int j = 0; j < size; j++) {
                    point.add(max.get(j) * res + min.get(j) * (1 - res));
                }
                return point;
            }
    
            for (int j = 0; j < size; j++) {
                axis.set(j, axis.get(j) / 2.0);
            }
            length = 0;
            for (int j = 0; j < size; j++) {
                length += Math.pow(axis.get(j), 2);
            }
            length = Math.sqrt(length);
        } while (length > EPSILON);
        return startPoint;
    }
}

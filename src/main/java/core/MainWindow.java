package core;

import graphics.GraphDisplay;
import math.*;
import org.mariuszgromada.math.mxparser.Function;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public class MainWindow {
    private JPanel rootPanel;
    private JTextArea log;
    private JTextField lowerX;
    private JTextField upperX;
    private JButton calculateButton;
    private GraphDisplay graph;
    private JTextField functionField;
    private JTextField lowerY;
    private JTextField upperY;
    private JComboBox modeSel;
    private JTextField minTField;
    private JTextField maxTField;
    private JTextField minXField;
    private JTextField maxXField;
    private JTextField stepsField;
    
    public static final String TITLE = "Variational optimization";
    private Function function;
    
    private MainWindow() {
        initComponents();
    }
    
    private void initComponents() {
        calculateButton.addActionListener(e -> calculate());
    }
    
    
    
    private void calculate() {
        try {
            log.setText("");
            
            double lX = Double.parseDouble(lowerX.getText());
            double uX = Double.parseDouble(upperX.getText());
            double lY = Double.parseDouble(lowerY.getText());
            double uY = Double.parseDouble(upperY.getText());
            
            int steps = Integer.parseInt(stepsField.getText());
            
            function = new Function(functionField.getText());
            
            if (!function.checkSyntax()) {
                log.append("\nInvalid function syntax!");
                return;
            }
            java.util.function.Function<List<Double>, Double> f = l -> function.calculate(l.get(0), l.get(1), l.get(2));
            VariationalSolver variationalSolver;
            List<Interpolator> results;
            Interpolator best;
            double dt;
            switch (modeSel.getSelectedIndex()) {
                case 0:
                    variationalSolver = new BallisticSolver();
                    results = variationalSolver.solve(f, new PointDouble(lX, lY), new PointDouble(uX, uY));
                    dt = (uX - lX) / (double) BallisticSolver.STEPS;
                    best = results.get(results.size() - 1);
                    log.append("\nGenerated");
                        double z0 = (best.evaluate(best.lower() + dt) - best.evaluate(best.lower())) / dt;
                    z0 = BigDecimal.valueOf(z0).setScale(3, RoundingMode.HALF_UP).doubleValue();
                    log.append("\nResult: z0 = " + z0);
                    for (int i = 0; i <= BallisticSolver.STEPS; i++) {
                        double x = best.lower() + i * dt;
                        log.append("\n" + i + ": " + new PointDouble(x, best.evaluate(x)).toString(3));
                    }
                    break;
                    
                default:
                    variationalSolver = new DirectSolver(steps);
                    results = variationalSolver.solve(f, new PointDouble(lX, lY), new PointDouble(uX, uY));
                    best = results.get(results.size() - 1);
                    dt = (uX - lX) / (double)(steps + 1.0);
                    log.append("\nGenerated");
                    for (int i = 0; i <= steps + 1; i++) {
                        double x = best.lower() + i * dt;
                        log.append("\n" + i + ": " + new PointDouble(x, best.evaluate(x)).toString(3));
                    }
            }
            updateGraph(results);
        }
        catch (NumberFormatException e) {
            log.append("\nInvalid input format");
        }
        catch (InterpolationException e) {
            e.printStackTrace();
        }
    }
    
    private void updateGraph(List<Interpolator> results) {
        try {
            graph.setMinX(Double.parseDouble(minTField.getText()));
            graph.setMaxX(Double.parseDouble(maxTField.getText()));
            graph.setMinY(Double.parseDouble(minXField.getText()));
            graph.setMaxY(Double.parseDouble(maxXField.getText()));
            
            graph.setInterpolators(results);
            graph.setInterpolatorsHighligthed(Collections.singletonList(results.get(results.size() - 1)));
    
            graph.repaint();
        }
        catch (NumberFormatException e) {
            log.append("\nInvalid input format");
        }
    }
    
    
    public static void main(String[] args) {
        JFrame frame = new JFrame(TITLE);
        MainWindow gui = new MainWindow();
        frame.setContentPane(gui.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

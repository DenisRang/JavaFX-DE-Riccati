package sample;

//DEModel class used to provide all mathematical computations as algorithms container

import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class DEModel {
    // Initial values
    private double x0;
    private double y0;
    private double X; // Final value of x
    private int N;  // Number of steps

    public DEModel(double x0, double y0, double X, int N) {
        this.x0 = x0;
        this.y0 = y0;
        this.X = X;
        this.N = N;
    }

    // Return the list of all required solution Series
    public List<XYChart.Series> getSolutions(boolean exact, boolean euler, boolean ie, boolean rk) {
        List<XYChart.Series> result = new ArrayList<>(4);
        double h = (X - x0) / N;
        double x[] = new double[N + 1];
        x[0] = x0;
        for (int i = 1; i <= N; i++) {
            x[i] = x[i - 1] + h;
        }
        double y[] = new double[N + 1];
        y[0] = y0;
        // Depend on chosen CheckBoxes result will contains solution series or null for relevant case
        if (exact) {
            result.add(exact(x, y, N));
        } else
            result.add(null);//TODO: try to avoid null
        if (euler) {
            result.add(euler(x, y, h, N));
        } else
            result.add(null);
        if (ie) {
            result.add(ie(x, y, h, N));
        } else
            result.add(null);
        if (rk) {
            result.add(rk(x, y, h, N));
        } else
            result.add(null);
        return result;
    }

    private XYChart.Series<Number, Number> exact(double x[], double y[], int N) {
        for (int i = 1; i <= N; i++) {
            y[i] = exp(x[i]) - 1 / (x[i] + 1 / (exp(x[i]) * exp(x[i]) + 5) - 2);
        }
        return doublesToSeries(x, y, "Exact");
    }

    private XYChart.Series<Number, Number> euler(double x[], double y[], double h, int N) {
        for (int i = 1; i <= N; i++) {
            y[i] = y[i - 1] + h * (f(x[i - 1], y[i - 1]));
        }
        return doublesToSeries(x, y, "Euler");
    }

    private XYChart.Series<Number, Number> ie(double x[], double y[], double h, int N) {
        for (int i = 1; i <= N; i++) {
            y[i] = y[i - 1] + h * (f(x[i - 1] + h / 2, y[i - 1] + h * f(x[i - 1], y[i - 1]) / 2));
        }
        return doublesToSeries(x, y, "Improved Euler");
    }

    private XYChart.Series rk(double x[], double y[], double h, int N) {
        for (int i = 1; i <= N; i++) {
            double k1 = f(x[i - 1], y[i - 1]);
            double k2 = f(x[i - 1] + h / 2, y[i - 1] + h * k1 / 2);
            double k3 = f(x[i - 1] + h / 2, y[i - 1] + h * k2 / 2);
            double k4 = f(x[i - 1] + h, y[i - 1] + h * k3);
            y[i] = y[i - 1] + h * (k1 + 2 * k2 + 2 * k3 + k4) / 6;
        }
        return doublesToSeries(x, y, "Runge-Kutta");
    }

    private double f(double x, double y) {
        return (1 - 2 * y) * exp(x) + y * y + exp(x) * exp(x);
    }

    // Create XYChart.Series from 2 arrays of double and set name to plot
    private XYChart.Series<Number, Number> doublesToSeries(double x[], double y[], String name) {
        XYChart.Series result;
        result = new XYChart.Series();
        result.setName(name);
        for (int i = 0; i < x.length; i++) {
            result.getData().add(new XYChart.Data(x[i], y[i]));
        }
        return result;
    }

    //when it start it call initializeGetTruncRez to get List of plots (at the start plots are empty
    // or does not exist(if they will not be showed))
    //for each required method for each N value it call getTrunc function
    //required methods also set by CheckBoxes
    //computation of truncation error made in private getTrunc funct
    //return the list of all required plots of truncation error

    public List<XYChart.Series> getTrunc(boolean euler, boolean ie, boolean rk, int n0, int ni) {
        List<XYChart.Series> result = initializeGetTruncRez(euler, ie, rk);
        for (int n = n0; n <= ni; n++) {
            N = n;
            for (int method = 1; method < 4; method++) {
                List<XYChart.Series> seriesList = getSolutions(true, euler, ie, rk);
                if (seriesList.get(method) != null) {
                    result.get(method - 1).getData().add(new XYChart.Data(n, getTrunc(seriesList.get(0), seriesList.get(method))));
                }
            }
        }
        return result;
    }
    //initialize list of Series and Series elements. Elements which are not chosen are null

    private List<XYChart.Series> initializeGetTruncRez(boolean euler, boolean ie, boolean rk) {
        List<XYChart.Series> result = new ArrayList<XYChart.Series>(3);
        if (euler) {
            result.add(new XYChart.Series());
            result.get(0).setName("euler");
        } else
            result.add(null);
        if (ie) {
            result.add(new XYChart.Series());
            result.get(1).setName("ie");
        } else
            result.add(null);
        if (rk) {
            result.add(new XYChart.Series());
            result.get(2).setName("rk");
        } else
            result.add(null);
        return result;
    }
    //method to calculate truncation error based on some series (result of numerical method) and exact series

    //return maximum value of difference between exact solution and numerical
    private double getTrunc(XYChart.Series exact, XYChart.Series series) {
        XYChart.Series result;
        result = new XYChart.Series();
        double max_trunc = 0;
        for (int i = 0; i <= N; i++) {
            XYChart.Data<Number, Number> current_exact = (XYChart.Data) exact.getData().get(i);
            XYChart.Data<Number, Number> current_series = (XYChart.Data) series.getData().get(i);
            double trunc = abs((double) current_exact.getYValue() - (double) current_series.getYValue());
            if (trunc > max_trunc)
                max_trunc = trunc;
        }
        return max_trunc;
    }
}


package edu.neu.coe.info6205.util;

import edu.neu.coe.info6205.sort.SortException;

import java.util.function.Function;

public class Statistics {

    /**
     * Constructor for Statistics.
     *
     * @param property   the particular property we're keeping track of.
     * @param normalizer the normalizer for the results (for example 1/n lg n).
     * @param nRuns      the number of runs.
     * @param size       the size of the problem.
     */
    public Statistics(String property, Function<Double, Double> normalizer, int nRuns, int size) {
        this.property = property;
        this.normalizer = normalizer;
        doubles = new double[nRuns];
        this.size = size;
    }

    public void add(double x) {
        if (doubles.length == 0)
            throw new SortException("Statistics: doubles is empty");
        if (count >= doubles.length) resize(2 * doubles.length);
        doubles[count] = x;
        count = count + 1;
        stale();
    }

    public int getCount() {
        return count;
    }

    public double total() {
        if (total == null) {
            double sum = 0;
            for (int i = 0; i < count; i++) sum += doubles[i];
            total = sum;
        }
        return total;
    }

    public double mean() {
        return total() / count;
    }

    public double stdDev() {
        if (stdDev == null) {
            double mean = mean();
            double variance = 0;
            for (int i = 0; i < count; i++) variance += (doubles[i] - mean) * (doubles[i] - mean);
            stdDev = Math.sqrt(variance / count);
        }
        return stdDev;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder().append(property).append(": ");
        if (updated) {
            final boolean stats = stdDev() > 0.0;
            final String s = "mean=";
            sb.append(s).append(Utilities.asInt(mean()));
            if (stats)
                sb.append("; stdDev=").append(Utilities.asInt(stdDev()));
            sb.append("; normalized=").append(Utilities.formatDecimal3Places(normalizedMean()));
        } else
            sb.append("<unset>");
        return sb.toString();
    }

    public static final Function<Double, Double> NORMALIZER_LINEARITHMIC_NATURAL = x -> Math.log(x) * x;

    public double normalizedMean() {
        return mean() / normalizer.apply((double) size);
    }

    private void resize(int n) {
        double[] result = new double[n];
        System.arraycopy(doubles, 0, result, 0, doubles.length);
        doubles = result;
    }

    private void stale() {
        total = null;
        stdDev = null;
        updated = true;
    }

    private Double total;
    private Double stdDev;

    private int count = 0;
    private final Function<Double, Double> normalizer;
    private double[] doubles;
    private final int size;
    private final String property;
    private boolean updated = false;

}
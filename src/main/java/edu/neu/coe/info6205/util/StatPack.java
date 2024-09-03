package edu.neu.coe.info6205.util;

import java.util.HashMap;
import java.util.function.Function;

/**
 * This class manages all of the "statistics" for an instrumented set of runs.
 * <p>
 * TODO add key "classification" and maybe also "heap access."
 */
public class StatPack {

    /**
     * Constructor of a StatPack.
     *
     * @param normalizer the normalizers.
     * @param nRuns      the number of runs.
     * @param keys       the set of keys for properties to be tracked.
     */
    public StatPack(Function<Double, Double> normalizer, int nRuns, int size, String... keys) {
        n = nRuns;
        map = new HashMap<>();
        for (String key : keys) map.put(key, new Statistics(key, normalizer, nRuns, size));
    }

    public void add(String key, double x) {
        getStatistics(key).add(x);
    }

    public Statistics getStatistics(String key) {
        final Statistics statistics = map.get(key);
        if (statistics == null) throw new RuntimeException("StatPack.getStatistics(" + key + "): key not valid");
        return statistics;
    }

    public int getCount(String key) {
        return getStatistics(key).getCount();
    }

    public double total(String key) {
        return getStatistics(key).total();
    }

    public double mean(String key) {
        return getStatistics(key).mean();
    }

    public double stdDev(String key) {
        return getStatistics(key).stdDev();
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder("StatPack {runs: " + n + " ");
        if (map.isEmpty()) stringBuilder.append("<empty>}");
        for (String key : map.keySet()) {
            final Statistics statistics = map.get(key);
            String string = statistics.toString();
            stringBuilder.append(string).append("; ");
        }
        return stringBuilder.toString().replaceAll("; $", "}");
    }

    private final HashMap<String, Statistics> map;
    private final int n;

    public boolean isInvalid() {
        return n <= 0;
    }
}
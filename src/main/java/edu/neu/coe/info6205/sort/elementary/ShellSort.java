/*
  (c) Copyright 2018, 2019 Phasmid Software
 */
package edu.neu.coe.info6205.sort.elementary;

import edu.neu.coe.info6205.sort.*;
import edu.neu.coe.info6205.util.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Class to implement Shell Sort.
 *
 * @param <X> the type of element on which we will be sorting (must implement Comparable).
 */
public class ShellSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    /**
     * Primary constructor for ShellSort with configuration and size.
     *
     * @param m      the mode, that is to say the "gap" (h) sequence to follow:
     *               1: ordinary insertion sort;
     *               2: use powers of two less one;
     *               3: use the sequence based on 3 (the one in the book): 1, 4, 13, etc.
     *               4: Sedgewick's sequence (not implemented).
     *               5: Pratt Sequence 2^i*3^j with i, j >= 0.
     * @param N      the number elements we expect to sort.
     * @param nRuns  the number of runs to be expected.
     * @param config the configuration.
     */
    public ShellSort(int m, int N, int nRuns, Config config) {
        super(DESCRIPTION + m, N, nRuns, config);
        this.m = m;
        // TODO get trackInversions from the Config file
        trackInversions = false;
    }

    /**
     * Secondary constructor for ShellSort using the Pratt sequence.
     */
    public ShellSort() throws IOException {
        this(5);
    }

    /**
     * Secondary constructor for ShellSort using the Pratt sequence and the standard configuration-based helper.
     *
     * @param m the mode, which is to say the "gap" (h) sequence to follow (see other constructors)
     */
    public ShellSort(int m) throws IOException {
        this(m, new InstrumentedComparableHelper<>(DESCRIPTION + m, Config.load(ShellSort.class)));
    }

    /**
     * Primary constructor for ShellSort with explicit mode and helper.
     *
     * @param m      the "gap" (h) sequence to follow:
     *               1: ordinary insertion sort;
     *               2: use powers of two less one;
     *               3: use the sequence based on 3 (the one in the book): 1, 4, 13, etc.
     *               4: Sedgewick's sequence (not implemented).
     *               5: Pratt Sequence 2^i*3^j with i, j >= 0.
     * @param helper an explicit instance of Helper to be used.
     */
    public ShellSort(int m, Helper<X> helper) {
        super(helper);
        this.m = m;
        trackInversions = false;
    }

    /**
     * Method to sort a sub-array of an array of Xs.
     * <p>
     * XXX check that the treatment of from and to is correct. It seems to be according to the unit tests.
     *
     * @param xs an array of Xs to be sorted in place.
     */
    public void sort(X[] xs, int from, int to) {
        int N = to - from;
        H hh = new H(N);
        int h = hh.first();
        while (h > 0) {
            hSort(h, xs, from, to);
            if (shellFunction != null)
                shellFunction.accept(getHelper());
            h = hh.next();
        }
    }

    /**
     * Set the "shell" function which is invoked on the helper after each shell (i.e., each value of h).
     * Yes, I do realize that shell was the name of the inventor, Donald Shell.
     * But it's also a convenient name of a (set of) h-sorts which one particular h-value.
     *
     * @param shellFunction a consumer of Helper of X.
     */
    public void setShellFunction(Consumer<Helper<X>> shellFunction) {
        this.shellFunction = shellFunction;
    }

    public static final String DESCRIPTION = "Shell sort in mode ";

    /**
     * Private method to h-sort an array.
     *
     * @param h    the stride (gap) of the h-sort.
     * @param xs   the array to be sorted.
     * @param from the first index to be considered in array xs.
     * @param to   one plus the last index to be considered in array xs.
     */
    private void hSort(int h, X[] xs, int from, int to) {
        final Helper<X> helper = getHelper();
        long inversionsStart = 0;
        if (trackInversions && helper.instrumented()) {
            inversionsStart = helper.inversions(xs);
            logger.debug("hSort (begin) with h=" + h + ", current inversionsStart=" + inversionsStart);
        }
        // TODO in the following operation, we over-count hits (see InsertionSort for how to do it correctly)
        for (int i = h + from; i < to; i++) {
            int j = i;
            while (j >= h + from && helper.swapConditional(xs, j - h, j)) j -= h;
        }
        if (trackInversions && helper.instrumented()) {
            long inversionsEnd = helper.inversions(xs);
            int proportionFixed = (int) (100.0 * (inversionsStart - inversionsEnd) / inversionsStart);
            logger.debug("hSort (end) with h=" + h + ", inversions fixed=" + proportionFixed + "%");
        }
    }

    private final int m;
    private final boolean trackInversions;

    private Consumer<Helper<X>> shellFunction = null;

    /**
     * Private inner class to provide h (gap) values.
     */
    class H {
        @SuppressWarnings("CanBeFinal")
        private int h = 1;
        private int i;
        private boolean started = false;
        final List<Integer> data = new ArrayList<>();

        H(int N) {

            switch (m) {
                case 1:
                    break;
                case 2:
                    while (h <= N) h = 2 * (h + 1) - 1;
                    break;
                case 3:
                    while (h <= N / 3) h = h * 3 + 1;
                    break;
                case 4:
                    i = 0;
                    while (sedgewick(i) < N) i++;
                    i--;
                    h = (int) sedgewick(i); // Note there will be loss of precision for large i
                    break;
                case 5:
                    //2^i*3^j with i, j >= 0
                    int i;
                    int j = 1;
                    while (j <= N) {
                        i = j;
                        while (i <= N) {
                            data.add(i);
                            i = i * 2;
                        }
                        j = j * 3;
                    }
                    // TODO this doesn't calculate hits, etc. correctly.
                    Collections.sort(data);
                    this.i = data.size() - 1;
                    h = data.get(this.i);
                    break;

                default:
                    throw new RuntimeException("invalid m value: " + m);
            }
        }

        /**
         * Method to yield the first h value.
         * NOTE: this may only be called once.
         *
         * @return the first (largest) value of h, given the size of the problem (N)
         */
        int first() {
            if (started) throw new RuntimeException("cannot call first more than once");
            started = true;
            return h;
        }

        /**
         * Method to yield the next h value in the "gap" series.
         * NOTE: first must be called before next.
         *
         * @return the next value of h in the gap series.
         */
        int next() {
            if (started) {
                switch (m) {
                    case 1 -> {
                        return 0;
                    }
                    case 2 -> {
                        h = (h + 1) / 2 - 1;
                        return h;
                    }
                    case 3 -> {
                        h = h / 3;
                        return h;
                    }
                    case 4 -> {
                        i--;
                        return (int) sedgewick(i);
                    }
                    case 5 -> {
                        i--;
                        if (i < 0) return 0;
                        return data.get(i);
                    }
                    default -> throw new RuntimeException("invalid m value: " + m);
                }
            } else {
                started = true;
                return h;
            }
        }

        long sedgewick(int k) {
            if (k < 0) return 0;
            if (k % 2 == 0) return 9L * (powerOf2(k) - powerOf2(k / 2)) + 1;
            else return 8L * powerOf2(k) - 6 * powerOf2((k + 1) / 2) + 1;
        }

        private long powerOf2(int k) {
            long value = 1;
            for (int i = 0; i < k; i++) value *= 2;
            return value;
        }
    }

    static <T extends Comparable<T>> boolean doShellSort(int m, NonComparableHelper<T> helper, final T[] xs) {
        ShellSort<T> shellSort = new ShellSort<>(m, helper);
        shellSort.mutatingSort(xs);
//        return helper.findInversion(xs) == -1;
        return true;
    }

    /**
     * Method to perform an (instrumented) shell sort on random data.
     *
     * @param m      the mode (gap sequence).
     * @param n      the size of the array to be sorted.
     * @param r      the number of repetitions.
     * @param config the configuration.
     * @return true if everything went according to plan.
     */
    static boolean doRandomDoubleShellSort(int m, int n, int r, final Config config) {
        boolean instrumented = config.getBoolean(Config.HELPER, "instrument");
        NonComparableHelper<Double> helper = instrumented ? new InstrumentedComparableHelper<>("ShellSort mode: " + m + " with instrumentation", n, r, config) : new NonInstrumentingComparableHelper<>("ShellSort mode: " + m, n, config);
        boolean result = true;
        for (int i = 0; i < r; i++) {
            Double[] xs = helper.random(Double.class, Random::nextDouble);
            result = result && doShellSort(m, helper, xs);
            helper.postProcess(xs);
        }
        if (helper.instrumented()) logger.info(helper.showStats());
        return result;
    }

    private static void showRandomDoubleShellSortResult(int m, int n, final Config config) {
        try (Stopwatch stopwatch = new Stopwatch()) {
            int repetitions = 100;
            boolean sorted = doRandomDoubleShellSort(m, n, repetitions, config);
            long millis = stopwatch.lap();
            if (sorted)
                logger.info("Shell Sorted with mode " + m + " and n = " + n + " (millisecs elapsed: " + millis * 1.0 / repetitions + ")");
            else throw new SortException("not sorted");
        }
    }

    final private static LazyLogger logger = new LazyLogger(ShellSort.class);

    public static void main(String[] args) {
        int N = 64000;

        while (N <= 100000) {
            int nRuns = 20;
            InstrumentedComparableHelper<Integer> instrumentedHelper = new InstrumentedComparableHelper<>("ShellSort", N, nRuns, Config.setupConfig("true", "false", "0", "0", "", ""));
            ShellSort<Integer> s = new ShellSort<>(5, instrumentedHelper);
            int j = N;
            s.init(j);
            Integer[] xs = instrumentedHelper.random(Integer.class, r -> r.nextInt(j));
            Benchmark<Boolean> benchmark = new Benchmark_Timer<>("Sorting", b -> s.sort(xs, 0, j));
            double nTime = benchmark.run(true, nRuns);
            long nCompares = instrumentedHelper.getCompares();
            long nSwaps = instrumentedHelper.getSwaps();
            long nHits = instrumentedHelper.getHits();

            System.out.println("When array size is: " + j);
            System.out.println("Compares: " + nCompares);
            System.out.println("Swaps: " + nSwaps);
            System.out.println("Hits: " + nHits);
            System.out.println("Time: " + nTime);

            N = N * 2;
        }
    }
}
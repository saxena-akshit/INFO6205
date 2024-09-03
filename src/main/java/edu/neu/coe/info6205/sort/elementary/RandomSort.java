/*
  (c) Copyright 2018, 2019 Phasmid Software
 */
package edu.neu.coe.info6205.sort.elementary;

import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.NonInstrumentingComparableHelper;
import edu.neu.coe.info6205.sort.SortWithComparableHelper;
import edu.neu.coe.info6205.util.Config;
import edu.neu.coe.info6205.util.QuickRandom;
import edu.neu.coe.info6205.util.Utilities;

import java.io.IOException;

/**
 * Class to implement Random Sort.
 * Random Sort, like Shell Sort, performs pre-processing of the array with (conditional) long-distance swaps in an attempt
 * to get the array almost ordered.
 * Like the decision-tree model of merge sort, Random sort performs N log N such conditional swaps before resorting to insertion sort.
 * It appears to take almost 10 times as long as quick sort for 5,000 elements.
 * I'm not sure why it should be so bad.
 *
 * @param <X> the type of element on which we will be sorting (must implement Comparable).
 */
public class RandomSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    /**
     * Constructor for RandomSort
     *
     * @param N      the number elements we expect to sort.
     * @param config the configuration.
     */
    public RandomSort(int N, Config config) {
        super(DESCRIPTION, N, 1, config);
    }

    public RandomSort() throws IOException {
        this(new NonInstrumentingComparableHelper<>(DESCRIPTION, Config.load(RandomSort.class)));
    }


    /**
     * Constructor for RandomSort
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public RandomSort(Helper<X> helper) {
        super(helper);
    }

    /**
     * Method to sort a sub-array of an array of Xs.
     * <p>
     *
     * @param xs an array of Xs to be sorted in place.
     */
    public void sort(X[] xs, int from, int to) {
        int N = to - from;
        final Helper<X> helper = getHelper();
        boolean instrumented = helper.instrumented();
        QuickRandom r = new QuickRandom(N);
        long inversions = instrumented ? helper.inversions(xs) : 0;
        if (N > CUTOFF) {
            int m = (int) (FACTOR * Utilities.lg(N) * N);
            for (int i = m; i > 0; i--) {
                int j = r.get() + from;
//                System.out.println("Random Sort: N="+N+", i="+i+", j="+j+", m="+m);
                helper.swapConditional(xs, j, r.get());
            }
            if (instrumented) {
                final long currentInversions = helper.inversions(xs);
                final long fixes = inversions - currentInversions;
                inversions = currentInversions;
                System.out.println("pre-processor: inversions=" + currentInversions + ", fixes=" + fixes + ", comparisons=" + m);
            }
        }
        new InsertionSort<>(helper).sort(xs, from, to);
        if (instrumented) {
            String s = helper.showStats();
            System.out.println("after insertion sort: " + s);
            final long currentInversions = helper.inversions(xs);
            final long fixes = inversions - currentInversions;
            System.out.println("insertion sort: inversions=" + currentInversions + ", fixes=" + fixes);
        }
    }

    public static final String DESCRIPTION = "Random sort";

    public static <T extends Comparable<T>> void sort(T[] ts) throws IOException {
        try (RandomSort<T> sort = new RandomSort<>()) {
            sort.mutatingSort(ts);
        }
    }

    /**
     * The number to be multiplied by N lg N in order to get the best efficiency from the pre-process.
     * Given that the probability of an inversion is 50%, we usually choose 2.0 here.
     * But, slightly higher values may work better.
     */
    public static final double FACTOR = 2.5;

    private static final int CUTOFF = 16;
}
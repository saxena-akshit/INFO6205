/*
  (c) Copyright 2018, 2019 Phasmid Software
 */
package edu.neu.coe.info6205.sort.elementary;

import edu.neu.coe.info6205.sort.*;
import edu.neu.coe.info6205.util.Config;
import edu.neu.coe.info6205.util.LazyLogger;
import edu.neu.coe.info6205.util.Stopwatch;

import java.io.IOException;
import java.util.Random;

/**
 * Class to sort arrays of (comparable) Xs which extends SortWithComparableHelper and Sort.
 *
 * @param <X> the underlying type of elements to be sorted (must support Comparable).
 */
public class BubbleSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    /**
     * Constructor for any subclasses to use.
     *
     * @param N      the number of elements expected.
     * @param nRuns  the expected number of runs.
     * @param config the configuration.
     */
    public BubbleSort(int N, int nRuns, Config config) {
        super(DESCRIPTION, N, nRuns, config);
    }

    /**
     * Constructor for BubbleSort
     *
     * @param N      the number elements we expect to sort.
     * @param config the configuration.
     */
    public BubbleSort(int N, Config config) {
        super(DESCRIPTION, N, 1, config);
    }

    public BubbleSort(Config config) {
        this(new NonInstrumentingComparableHelper<>(DESCRIPTION, config));
    }

    /**
     * Constructor for BubbleSort
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public BubbleSort(Helper<X> helper) {
        super(helper);
    }

    /**
     * Sort the subarray xs:from:to using bubble sort.
     *
     * @param xs   sort the array xs from "from" to "to".
     * @param from the index of the first element to sort.
     * @param to   the index of the first element not to sort.
     */
    public void sort(X[] xs, int from, int to) {
        final Helper<X> helper = getHelper();
        for (int j = to; j > from; j--)
            if (optimizedInnerLoopSuccess(xs, helper, from, j))
                break;
    }

    /**
     * "Optimized" inner loop of bubble sort (see Wikipedia: <a href="https://en.wikipedia.org/wiki/Bubble_sort#Implementation">Bubble sort implementation</a>)
     * The optimization is that we only loop until we reach the (j-1)th element because the jth element and beyond
     * cannot possibly be out of order.
     *
     * @param xs     the complete array to be sorted.
     * @param helper the helper.
     * @param from   the index of the first element to sort.
     * @param j      the index of the first element not to sort.
     * @return true if we passed through the elements without swapping any.
     */
    private boolean optimizedInnerLoopSuccess(X[] xs, Helper<X> helper, int from, int j) {
        if (from >= j - 1) return false;
        boolean swapped = false;
        // NOTE this first line is the clearest way to show the logic.
//        for (int i = from + 1; i < j; i++) swapped |= helper.swapStableConditional(xs, i);
        // NOTE the following lines correctly track hits.
        int i = from;
        X v = helper.get(xs, i);
        X w = helper.get(xs, i + 1);
        while (true) {
            boolean b = helper.swapConditional(xs, v, i, i + 1, w);
            swapped |= b;
            i++;
            if (i == j - 1) break;
            if (!b) v = w;
            w = helper.get(xs, i + 1);
        }

        // XXX return true if there were no swaps
        return !swapped;
    }

    /**
     * This is used only by unit tests.
     *
     * @param ys  the array to be sorted.
     * @param <Y> the underlying element type.
     */
    public static <Y extends Comparable<Y>> void mutatingBubbleSort(Y[] ys) throws IOException {
        try (BubbleSort<Y> sort = new BubbleSort<>(Config.load(BubbleSort.class))) {
            sort.mutatingSort(ys);
        }
    }

    public static final String DESCRIPTION = "Bubble sort";

    public static void main(String[] args) throws IOException {
        bubbleSortMain(10000);
        insertionSortMain(10000);
    }

    private static void bubbleSortMain(int n) throws IOException {
        BubbleSort<Integer> bubbleSort = new BubbleSort<Integer>(HelperFactory.create("Bubble sort", n, Config.load(BubbleSort.class)));
        Helper<Integer> helper = bubbleSort.getHelper();
        logger.info("Begin BubbleSort");
        try (Stopwatch stopwatch = new Stopwatch()) {
            for (int i = 0; i < 10; i++)
                doSort(bubbleSort, helper, stopwatch);
        }
        logger.info("End BubbleSort");
    }

    private static void insertionSortMain(int n) throws IOException {
        SortWithHelper<Integer> sorter = new InsertionSort<Integer>(HelperFactory.create("Insertion sort", n, Config.load(BubbleSort.class)));
        Helper<Integer> helper = sorter.getHelper();
        logger.info("Begin InsertionSort");
        try (Stopwatch stopwatch = new Stopwatch()) {
            for (int i = 0; i < 10; i++)
                doSort(sorter, helper, stopwatch);
        }
        logger.info("End InsertionSort");
    }

    private static void doSort(Sort<Integer> sorter, Helper<Integer> helper, Stopwatch stopwatch) {
        Integer[] integers = helper.random(Integer.class, Random::nextInt);
        Integer[] sorted = sorter.sort(integers);
        if (!helper.isSorted(sorted)) System.err.println("Not sorted");
        System.out.println(helper.getDescription() + " " + integers.length + " integers: " + stopwatch.lap());
    }

    final static LazyLogger logger = new LazyLogger(BubbleSort.class);

}
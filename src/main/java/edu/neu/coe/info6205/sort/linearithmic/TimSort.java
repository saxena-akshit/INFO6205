/*
  (c) Copyright 2018, 2019 Phasmid Software
 */
package edu.neu.coe.info6205.sort.linearithmic;

import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.NonInstrumentingComparableHelper;
import edu.neu.coe.info6205.sort.SortWithComparableHelper;
import edu.neu.coe.info6205.sort.SortWithHelper;
import edu.neu.coe.info6205.sort.classic.ClassicHelper;
import edu.neu.coe.info6205.util.Config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Sorter which delegates to Timsort via Arrays.sort.
 *
 * @param <X>
 */
public class TimSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    /**
     * Constructor for TimSort
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public TimSort(Helper<X> helper) {
        super(helper);
    }

    /**
     * Constructor for TimSort
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the number of runs to be expected (this is only significant when instrumenting).
     * @param config the configuration.
     */
    public TimSort(int N, int nRuns, Config config) {
        super(DESCRIPTION, N, nRuns, config);
    }

    public TimSort() throws IOException {
        this(new NonInstrumentingComparableHelper<>(DESCRIPTION, Config.load(TimSort.class)));
    }

    public void sort(X[] xs, int from, int to) {
        Arrays.sort(xs, from, to);
    }

    public static final String DESCRIPTION = "Timsort";

    public static SortWithHelper<String> CaseInsensitiveSort(int N, Config config) {
        return new SortWithHelper<>(new ClassicHelper<>(DESCRIPTION, String.CASE_INSENSITIVE_ORDER, N, new Random(), config)) {
            public void sort(String[] xs, int from, int to) {
                Arrays.sort(xs, from, to, String.CASE_INSENSITIVE_ORDER);
            }
        };
    }

    static class ComparatorSort<T> extends SortWithHelper<T> {
        public ComparatorSort(Helper<T> helper) {
            super(helper);
        }

        public void sort(T[] xs, int from, int to) {
            Arrays.sort(xs, from, to, getHelper().getComparator());
        }
    }
}

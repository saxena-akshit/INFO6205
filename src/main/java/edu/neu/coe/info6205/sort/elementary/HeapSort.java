package edu.neu.coe.info6205.sort.elementary;

import java.util.Arrays;

import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.SortException;
import edu.neu.coe.info6205.sort.SortWithComparableHelper;
import edu.neu.coe.info6205.util.Config;

public class HeapSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    public static final String DESCRIPTION = "HeapSort";

    /**
     * Constructor for HeapSort.
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public HeapSort(Helper<X> helper) {
        super(helper);
    }

    /**
     * Constructor for HeapSort with parameters similar to MergeSort.
     *
     * @param N the expected size of the array to be sorted.
     * @param nRuns the expected number of runs.
     * @param config the configuration settings.
     */
    public HeapSort(int N, int nRuns, Config config) {
        super(DESCRIPTION, N, nRuns, config);
    }

    @Override
    public X[] sort(X[] xs, boolean makeCopy) {
        getHelper().init(xs.length); // Initialize helper for tracking
        additionalMemory(xs.length); // Track memory usage
        X[] result = makeCopy ? Arrays.copyOf(xs, xs.length) : xs;
        sort(result, 0, result.length);
        additionalMemory(-xs.length); // Free tracked memory
        return result;
    }

    @Override
    public void sort(X[] xs, int from, int to) {
        if (xs == null || xs.length <= 1) {
            return;
        }

        Helper<X> helper = getHelper();
        helper.init(xs.length); // Initialize for logging

        // Build the max heap
        buildMaxHeap(xs);

        // Sort-down phase
        for (int i = to - 1; i > from; i--) {
            helper.swap(xs, from, i); // Log swap
            maxHeap(xs, i, from);     // Rebuild max heap for the reduced array
        }

        // Log the final statistics
        helper.postProcess(xs);
        // if (!helper.sorted(xs, from, to)) {
        //     throw new SortException("HeapSort failed to sort the array");
        // }
    }

    private void buildMaxHeap(X[] xs) {
        Helper<X> helper = getHelper();
        int n = xs.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            maxHeap(xs, n, i);
        }
    }

    private void maxHeap(X[] xs, int heapSize, int index) {
        Helper<X> helper = getHelper();
        int left = index * 2 + 1;
        int right = index * 2 + 2;
        int largest = index;

        if (left < heapSize && helper.compare(xs, largest, left) < 0) { // Log comparison
            largest = left;
        }
        if (right < heapSize && helper.compare(xs, largest, right) < 0) { // Log comparison
            largest = right;
        }

        if (largest != index) {
            helper.swap(xs, index, largest); // Log swap
            maxHeap(xs, heapSize, largest);
        }
    }

    /**
     * Get a configuration string based on the provided Config.
     *
     * @param config the configuration object.
     * @return a description string for the configuration.
     */
    // private static String getConfigString(Config config) {
    //     StringBuilder stringBuilder = new StringBuilder();
    //     int cutoff = config.getInt(HELPER, CUTOFF, CUTOFF_DEFAULT);
    //     if (cutoff != CUTOFF_DEFAULT) {
    //         stringBuilder.append(" with cutoff ").append(cutoff);
    //     }
    //     return stringBuilder.toString();
    // }
    private int arrayMemory = -1;
    private int additionalMemory;
    private int maxMemory;

    /**
     * Set the array memory for tracking.
     *
     * @param n the memory size to be set.
     */
    public void setArrayMemory(int n) {
        if (arrayMemory == -1) {
            arrayMemory = n;
            additionalMemory(n);
        }
    }

    /**
     * Track additional memory usage.
     *
     * @param n the additional memory used.
     */
    public void additionalMemory(int n) {
        additionalMemory += n;
        if (maxMemory < additionalMemory) {
            maxMemory = additionalMemory;
        }
    }

    /**
     * Get the memory usage factor.
     *
     * @return the memory factor.
     */
    public Double getMemoryFactor() {
        if (arrayMemory == -1) {
            throw new SortException("Array memory has not been set");
        }
        return 1.0 * maxMemory / arrayMemory;
    }
}

package edu.neu.coe.info6205.sort.linearithmic;

import java.util.Arrays;

import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.SortException;
import edu.neu.coe.info6205.sort.SortWithComparableHelper;
import edu.neu.coe.info6205.sort.elementary.InsertionSort;
import edu.neu.coe.info6205.util.Config;
import static edu.neu.coe.info6205.util.Config.CUTOFF;
import static edu.neu.coe.info6205.util.Config.CUTOFF_DEFAULT;
import static edu.neu.coe.info6205.util.Config.HELPER;

/**
 * Class MergeSort.
 *
 * @param <X> the underlying comparable type.
 */
public class MergeSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    public static final String DESCRIPTION = "MergeSort";

    /**
     * Constructor for MergeSort
     * <p>
     * NOTE this is used only by unit tests, using its own instrumented helper.
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public MergeSort(Helper<X> helper) {
        super(helper);
        insertionSort = setupInsertionSort(helper);
    }

    /**
     * Constructor for MergeSort
     *
     * @param N the number elements we expect to sort.
     * @param nRuns the expected number of runs.
     * @param config the configuration.
     */
    public MergeSort(int N, int nRuns, Config config) {
        super(DESCRIPTION + getConfigString(config), N, nRuns, config);
        insertionSort = setupInsertionSort(getHelper());
    }

    private InsertionSort<X> setupInsertionSort(final Helper<X> helper) {
        return new InsertionSort<>(helper.clone("MergeSort: insertion sort"));
    }

    public X[] sort(X[] xs, boolean makeCopy) {
        getHelper().init(xs.length);
        additionalMemory(xs.length);
        X[] result = makeCopy ? Arrays.copyOf(xs, xs.length) : xs;
        sort(result, 0, result.length);
        additionalMemory(-xs.length);
        return result;
    }

    public void sort(X[] a, int from, int to) {
        Config config = helper.getConfig();
        boolean noCopy = config.getBoolean(MERGESORT, NOCOPY);
        // CONSIDER don't copy but just allocate according to the xs/aux interchange optimization
        @SuppressWarnings("unchecked")
        X[] aux = noCopy ? helper.copyArray(a) : (X[]) new Comparable[a.length];
        sort(a, aux, from, to);
    }

    private void sort(X[] a, X[] aux, int from, int to) {
        Config config = helper.getConfig();
        boolean insurance = config.getBoolean(MERGESORT, INSURANCE);
        boolean noCopy = config.getBoolean(MERGESORT, NOCOPY);

        // Base case for recursive merge sort: use insertion sort for small arrays.
        if (to <= from + helper.cutoff()) {
            insertionSort.sort(a, from, to);
            return;
        }

        int mid = from + (to - from) / 2;

        // Recursively sort the left and right halves of the array
        sort(a, aux, from, mid);
        sort(a, aux, mid, to);

        // Merge the sorted halves
        merge(a, aux, from, mid, to);

        // If insurance is enabled, make sure the array is correctly sorted by checking conditions
        if (insurance && !isSorted(a, from, to)) {
            throw new SortException("Merge sort failed to sort correctly.");
        }

        // If noCopy is enabled, avoid unnecessary copying by using the input array itself as the result.
        if (noCopy) {
            System.arraycopy(aux, from, a, from, to - from);
        }
    }

    // Helper method to check if the array is sorted in the specified range.
    private boolean isSorted(X[] a, int from, int to) {
        for (int i = from + 1; i < to; i++) {
            if (helper.less(a[i], a[i - 1])) {
                return false;
            }
        }
        return true;
    }

    private void merge(X[] sorted, X[] result, int from, int mid, int to) {
        int i = from;
        int j = mid;
        X v = helper.get(sorted, i);
        X w = helper.get(sorted, j);
        for (int k = from; k < to; k++) {
            if (i >= mid) {
                helper.copy(w, result, k);
                if (++j < to) {
                    w = helper.get(sorted, j);
                }
            } else if (j >= to) {
                helper.copy(v, result, k);
                if (++i < mid) {
                    v = helper.get(sorted, i);
                }
            } else if (helper.less(w, v)) {
                helper.incrementFixes(mid - i);
                helper.copy(w, result, k);
                if (++j < to) {
                    w = helper.get(sorted, j);
                }
            } else {
                helper.copy(v, result, k);
                if (++i < mid) {
                    v = helper.get(sorted, i);
                }
            }
        }
    }

    public static final String MERGESORT = "mergesort";
    public static final String NOCOPY = "nocopy";
    public static final String INSURANCE = "insurance";

    private static String getConfigString(Config config) {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.getBoolean(MERGESORT, INSURANCE)) {
            stringBuilder.append(" with insurance comparison");
        }
        if (config.getBoolean(MERGESORT, NOCOPY)) {
            stringBuilder.append(" with no copy");
        }
        int cutoff = config.getInt(HELPER, CUTOFF, CUTOFF_DEFAULT);
        if (cutoff != CUTOFF_DEFAULT) {
            if (cutoff == 1) {
                stringBuilder.append(" with no cutoff"); 
            }else {
                stringBuilder.append(" with cutoff " + cutoff);
            }
        }
        return stringBuilder.toString();
    }

    private final InsertionSort<X> insertionSort;

    private int arrayMemory = -1;
    private int additionalMemory;
    private int maxMemory;

    public void setArrayMemory(int n) {
        if (arrayMemory == -1) {
            arrayMemory = n;
            additionalMemory(n);
        }
    }

    public void additionalMemory(int n) {
        additionalMemory += n;
        if (maxMemory < additionalMemory) {
            maxMemory = additionalMemory;
        }
    }

    public Double getMemoryFactor() {
        if (arrayMemory == -1) {
            throw new SortException("Array memory has not been set");
        }
        return 1.0 * maxMemory / arrayMemory;
    }

}

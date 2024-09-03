package edu.neu.coe.info6205.sort.counting;

import edu.neu.coe.info6205.sort.*;
import edu.neu.coe.info6205.sort.linearithmic.QuickSort_3way;
import edu.neu.coe.info6205.util.CodePointMapper;
import edu.neu.coe.info6205.util.Config;
import edu.neu.coe.info6205.util.SuffixComparator;

/**
 * Class to implement Most significant digit string sort (a radix sort).
 */
public class MSDStringSort extends SortWithHelperAndAdditionalMemory<String> {

    public static final String DESCRIPTION = "MSD string sort ";

    /**
     * Primary constructor.
     *
     * @param mapper the required CodePointMapper.
     * @param helper the appropriate Helper.
     */
    private MSDStringSort(CodePointMapper mapper, Helper<String> helper) {
        super(helper, (s, d) -> mapCodePoint(mapper, s, d));
        this.mapper = mapper; // CONSIDER remove this: all we actually need is the range.
    }

    public MSDStringSort(CodePointMapper mapper, String description, int N, Config config, int nRuns) {
        this(mapper, HelperFactory.createGeneric(description, mapper.comparator, N, nRuns, config));
        init(N);
        closeHelper = true;
    }

    public MSDStringSort(CodePointMapper mapper, int N, int nRuns, Config config) {
        this(mapper, DESCRIPTION + mapper + " with cutoff=" + config.getString(Config.HELPER, Config.MSDCUTOFF, InstrumentedComparableHelper.MSD_CUTOFF_DEFAULT + ""), N, config, nRuns);
    }

    /**
     * Generic, mutating sort method which operates on a sub-array.
     *
     * @param xs   sort the array xs from "from" until "to" (exclusive of to).
     * @param from the index of the first element to sort.
     * @param to   the index of the first element not to sort.
     */
    public void sort(String[] xs, int from, int to) {
        doSort(xs, from, to, 0);
    }

    /**
     * Sort from xs[from] to xs[to] (exclusive), ignoring the first d characters of each String.
     * This method is recursive.
     *
     * @param xs   the array to be sorted.
     * @param from the low index.
     * @param to   the high index (one above the highest actually processed).
     * @param d    the number of characters in each String to be skipped.
     */
    private void doSort(String[] xs, int from, int to, int d) {
        int n = to - from;
        if (n <= 1)
            return;
        // NOTE that we never cut over to Quicksort at the top-level.
        if (d > 0 && n <= helper.MSDCutoff()) cutToQuicksort(xs, from, to, d, n);
        else doMSDrecursive(xs, from, to, d);
    }

    private void cutToQuicksort(String[] xs, int from, int to, int d, int n) {
        SuffixComparator suffixComparator = new SuffixComparator(helper.getComparator(), d);
        Helper<String> cloned = helper.clone("MSD 3-way quicksort", suffixComparator, n);
        try (Sort<String> sorter = new QuickSort_3way<>(cloned)) {
            sorter.sort(xs, from, to);
        }
    }

    private void doMSDrecursive(String[] xs, int from, int to, int d) {
        int n = to - from;
        String[] aux = new String[n]; // CONSIDER optimizing the usage of aux.
        additionalMemory(n);
        int[] count = new int[mapper.range + 1];
        additionalMemory(mapper.range + 1);

        // Compute frequency counts.
        helper.incrementHits(n); // for the count.
        for (int i = from; i < to; i++) count[classify(xs, i, d) + 1]++;

        // Accumulate counts.
        int countR = count[0];
        for (int r = 1; r < mapper.range; r++) {
            helper.incrementHits(1); // for the count.
            count[r] += countR;
            countR = count[r];
        }

        // Distribute.
        helper.distributeBlock(xs, from, to, aux, x -> count[classify(x, d)]++);

        // Copy back.
        helper.copyBlock(aux, 0, xs, from, n);

        // Recursively sort on the next character position in each String.
        // TO BE IMPLEMENTED 
        // END SOLUTION
        additionalMemory(-(n + mapper.range + 1));
    }

    private static int mapCodePoint(CodePointMapper mapper, String x, int d) {
        return mapper.map(charAt(x, d));
    }

    private final CodePointMapper mapper;

    private static int charAt(String s, int d) {
        if (d < s.length()) return s.charAt(d);
        else return 0; // CONSIDER creating a value in CodePointMapper to specify this.
    }

    static class QuickSortThreeWayByFunction extends QuickSort_3way<String> {

        public QuickSortThreeWayByFunction(Helper<String> helper) {
            super(helper);
        }
    }

    @Override
    public void close() {
        if (closeHelper) helper.close();
        super.close();
    }
}
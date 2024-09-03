package edu.neu.coe.info6205.sort.classic;

import edu.neu.coe.info6205.sort.*;
import edu.neu.coe.info6205.sort.elementary.InsertionSort;
import edu.neu.coe.info6205.util.Config;
import edu.neu.coe.info6205.util.LazyLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Bucket Sort.
 * <p>
 * CONSIDER re-implementing by doing ClassicSort first (based on buckets) then do insertion sort.
 *
 * @param <X> the underlying type which must extend Comparable.
 */
public class BucketSort<X extends Comparable<X>> extends ClassificationSorter<X, Void> {

    public static final String DESCRIPTION = "Bucket sort";
    public static final String ALPHABET = " abcdefghijklmnopqrstuvwxyz";
    public static final int ALPHABET_SIZE = ALPHABET.length();

    public static String[] DIGRAPHS;
    public static final int DIGRAPHS_SIZE = ALPHABET_SIZE * ALPHABET_SIZE;
    private final Function<X, Integer> xClassifier;

    public static Integer classifyStringInitial(String s) {
        return ALPHABET.indexOf(s.toLowerCase().charAt(0));
    }

    public static Integer classifyStringDigraph(String s) {
        if (DIGRAPHS == null) {
            DIGRAPHS = new String[DIGRAPHS_SIZE];
            int i = 0;
            for (char c1 : ALPHABET.toCharArray())
                for (char c2 : ALPHABET.toCharArray())
                    DIGRAPHS[i++] = String.valueOf(c1) + c2;
        }
        String digraph = (s.toLowerCase() + " ").substring(0, 2);
        return Arrays.binarySearch(DIGRAPHS, digraph);
    }

    public void sort(X[] xs, int from, int to) {
        if (xClassifier == null) {
            if (Number.class.isAssignableFrom(xs[0].getClass())) {
                Function<X, Integer> numberClassifier = getNumberClassifier((Number[]) xs, 0, to, buckets.length);
                setClassifier((x, y) -> numberClassifier.apply(x));
            } else
                throw new SortException("BucketSort: classifier undefined AND the type being sorted is not a Number");
        }
        clearBuckets();
        assignToBuckets(xs, from, to);
        checkBuckets(xs);
        unloadBuckets(buckets, xs, helper);
        sort.sort(xs, from, to);
    }

    /**
     * Primary constructor.
     *
     * @param helper     the Helper to use.
     * @param classifier the classifier to yield an integer from an X (may be null on instantiation).
     * @param buckets    an array of Objects which will form the buckets for this BucketSort.
     */
    public BucketSort(Helper<X> helper, Function<X, Integer> classifier, Object[] buckets) {
        super(helper, (x, y) -> classifier.apply(x));
        this.xClassifier = classifier; // CONSIDER improving this mechanism.
        this.buckets = buckets;
        Helper<X> insertionSortHelper = helper.clone("insertion sort");
        this.sort = new InsertionSort<>(insertionSortHelper);
        for (int i = 0; i < buckets.length; i++) buckets[i] = new ArrayList<>();
        closeHelper = true;
        logger.info(DESCRIPTION + ": " + buckets.length + " buckets of mean size: " + 1.0 * helper.getN() / buckets.length);
    }

    /**
     * Secondary Constructor.
     * <p>
     * TESTME
     *
     * @param classifier the classifier to yield an integer from an X (may be null on instantiation).
     * @param nBuckets   the number of buckets to use.
     * @param N          the number of elements (only affects the InsertionSort sorter).
     * @param config     the configuration.
     */
    public BucketSort(Function<X, Integer> classifier, int nBuckets, int N, Config config) {
        this(HelperFactory.create(DESCRIPTION, N, config), classifier, new Object[nBuckets]);
        closeHelper = true;
    }

    /**
     * Secondary Constructor.
     *
     * @param classifier the classifier to yield an integer from an X (may be null on instantiation).
     * @param nBuckets   the number of buckets to use.
     * @param helper     the Helper to use.
     */
    public BucketSort(Function<X, Integer> classifier, int nBuckets, NonComparableHelper<X> helper) {
        this(helper, classifier, new Object[nBuckets]);
    }

    /**
     * Secondary constructor.
     * <p>
     * TESTME
     *
     * @param nBuckets the number of buckets to use.
     * @throws IOException a configuration problem.
     */
    BucketSort(int nBuckets) throws IOException {
        this(null, nBuckets, new NonInstrumentingComparableHelper<>(DESCRIPTION, Config.load(BucketSort.class)));
        closeHelper = true;
    }

    public static BucketSort<String> CaseIndependentBucketSort(Function<String, Integer> classifier, int nBuckets, int N, Config config) {
        return new BucketSort<>(HelperFactory.createGeneric(DESCRIPTION, String.CASE_INSENSITIVE_ORDER, N, 1, config), classifier, new Object[nBuckets]);

    }

    private void clearBuckets() {
        for (Object b : buckets) //noinspection unchecked
            ((List<X>) b).clear();
    }

    private void checkBuckets(X[] xs) {
        int count = 0;
        for (Object b : buckets) {
            @SuppressWarnings("unchecked") int size = ((List<X>) b).size();
            count += size;
        }
        if (count != xs.length) throw new RuntimeException("incorrect number of buckets: " + count + ", " + xs.length);
    }

    private void assignToBuckets(X[] xs, int from, int to) {
        helper.incrementCopies(to - from);  // this accounts for copying xs[i] to buckets[index]
        helper.incrementHits(to - from); // this accounts for adding the value to buckets[index]
        // Assign the elements to the buckets
        for (int i = from; i < to; i++) {
            X x = helper.get(xs, i);
            int index = classify(x, null);
            if (index < 0) index = 0;
            if (index >= buckets.length) index = buckets.length - 1;
            //noinspection unchecked
            ((List<X>) buckets[index]).add(x);
        }
    }

    private static <T> Function<T, Integer> getNumberClassifier(final Number[] xs, final int from, final int to, final int classes) {
        // Determine the min, max and gap.
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int i = from; i < to; i++) {
            if (xs[i].doubleValue() < min) min = xs[i].doubleValue();
            if (max < xs[i].doubleValue()) max = xs[i].doubleValue();
        }
        double gap = (max - min) / classes;
        logger.debug("creating numeric classifier with gap size: " + gap);
        return numberClassifier(min, gap, classes);
    }

    private static <T> Function<T, Integer> numberClassifier(final double min, final double gap, int nBuckets) {
        return x -> {
            int index = (int) Math.floor((((Number) x).doubleValue() - min) / gap);
            if (index < 0) index = 0;
            if (index >= nBuckets) index = nBuckets - 1;
            return index;
        };
    }

    private final static LazyLogger logger = new LazyLogger(BucketSort.class);

    /**
     * Method to unload and sort the buckets into the array xs.
     * <p>
     * CONSIDER using from and to as parameters (instead of xs.length).
     *
     * @param buckets an array of Bag of X elements.
     * @param xs      an array of X elements to be filled.
     * @param helper  a helper whose compare method we will use.
     * @param <X>     the underlying type of the array and the Helper.
     */
    @SuppressWarnings("unchecked")
    private static <X extends Comparable<X>> void unloadBuckets(Object[] buckets, X[] xs, final Helper<X> helper) {
        final Index index = new Index(xs.length);
        Arrays.stream(buckets).forEach(xes -> unloadBucket(xs, helper, index, (List<X>) xes));
    }

    @SuppressWarnings("unchecked")
    private static <X extends Comparable<X>> void unloadBucket(X[] xs, Helper<X> helper, Index index, List<X> xes) {
        final Object[] objects = xes.toArray();
        int size = xes.size();
        helper.incrementCopies(size);
        helper.incrementHits(2L * size);
        for (Object x : objects)
            try {
                int next = index.getNext();
                xs[next] = (X) x;
            } catch (IndexException e) {
                throw new RuntimeException("unloadBucket: index out of bounds: " + e.getLocalizedMessage());
            }
    }

    static class Index {
        public Index(int n) {
            this.n = n;
        }

        int index = 0;

        int getNext() throws IndexException {
            if (index >= 0 && index < n)
                return index++;
            else throw new IndexException(n); // TESTME
        }

        private final int n;
    }

    static class IndexException extends Exception {
        /**
         * Constructs a new exception with {@code null} as its detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         */
        public IndexException(int n) {
            super("Index negative or too large: " + n);
        }
    }

    private final Object[] buckets;
    private final SortWithHelper<X> sort;

}
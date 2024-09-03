package edu.neu.coe.info6205.select;

import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.NonInstrumentingComparableHelper;
import edu.neu.coe.info6205.sort.linearithmic.Partition;

import java.util.Random;

/**
 * Class QuickSelect
 *
 * @param <X> the underlying comparable type.
 * @author Suchita Dabir, 2024
 */
public class QuickSelect<X extends Comparable<X>> implements Select<X> {
    public final int k;

    /**
     * @param k represents how many smaller elements there are in the array.
     */
    public QuickSelect(int k) {
        this.k = k;
    }


    /**
     * Method to partition the given partition into smaller partitions.
     *
     * @param partition the partition to divide up.
     * @return an array of integers lt and gt.
     */
    public int[] partition(Partition<X> partition) {
        Helper<X> helper = NonInstrumentingComparableHelper.getHelper(QuickSelect.class);
        final X[] xs = partition.xs;
        final int p1 = partition.from;
        final int p2 = partition.to;
        helper.swapConditional(xs, p1, p2);
        int lt = p1 + 1;
        int gt = p2 - 1;
        int i = lt;
        X v1 = xs[p1];
        X v2 = xs[p2];
        // NOTE: we are trying to avoid checking on instrumented for every time in the inner loop for performance reasons (probably a silly idea).
        // NOTE: if we were using Scala, it would be easy to set up a comparer function and a swapper function. With java, it's possible but much messier.
        if (helper.instrumented()) {
            helper.incrementHits(2); // XXX these account for v1 and v2.
            while (i <= gt) {
                if (helper.compare(xs, i, v1) < 0) helper.swap(xs, lt++, i++);
                else if (helper.compare(xs, i, v2) > 0) helper.swap(xs, i, gt--);
                else i++;
            }
            helper.swap(xs, p1, --lt);
            helper.swap(xs, p2, ++gt);
        } else {
            while (i <= gt) {
                X x = xs[i];
                if (x.compareTo(v1) < 0) swap(xs, lt++, i++);
                else if (x.compareTo(v2) > 0) swap(xs, i, gt--);
                else i++;
            }
            swap(xs, p1, --lt);
            swap(xs, p2, ++gt);
        }

        return new int[]{lt, gt};
    }

    /***
     *
     * @param a the array
     * @param k the kth smallest index
     * @return kth smallest value
     */
    public X select(X[] a, int k) {
        shuffle(a);
        int lo = 0, hi = a.length - 1;
        while (hi > lo) {
            int[] j = partition(new Partition<>(a, lo, hi));
            int lt = j[0];
            int gt = j[1];

            if (k < lt) {
                hi = lt - 1;
            } else if (k > gt) {
                lo = gt + 1;
            } else {
                lo = lt + 1;
                hi = gt;
            }
        }
        return a[k];
    }

    /***
     * Knuth shuffle
     * In iteration i, pick integer r between 0 and i uniformly at random
     * Swap a[i] and a[r].
     *
     * @param a the array
     */
    public static void shuffle(Object[] a) {
        Random random = new Random();
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = random.nextInt(i + 1);
            swap(a, i, r);
        }
    }

    /**
     * exchange a[i] and a[r]
     *
     * @param a the array.
     * @param i one index.
     * @param r other index.
     */
    private static void swap(Object[] a, int i, int r) {
        Object temp = a[i];
        a[i] = a[r];
        a[r] = temp;
    }

}
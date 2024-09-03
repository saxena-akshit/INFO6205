/*
 * Copyright (c) 2017. Phasmid Software
 */

package edu.neu.coe.info6205.sort.linearithmic;

import edu.neu.coe.info6205.sort.*;
import edu.neu.coe.info6205.sort.elementary.InsertionSort;
import edu.neu.coe.info6205.util.Config;
import edu.neu.coe.info6205.util.LazyLogger;
import edu.neu.coe.info6205.util.PrivateMethodTester;
import edu.neu.coe.info6205.util.StatPack;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static edu.neu.coe.info6205.util.ConfigTest.INVERSIONS;
import static org.junit.Assert.*;

@SuppressWarnings("ALL")
public class MergeSortTest {

    @BeforeClass
    public static void beforeClass() throws IOException {
        config = Config.load(MergeSortTest.class);
    }

    @Test
    public void testSort1a() throws Exception {
        Integer[] xs = new Integer[4];
        xs[0] = 3;
        xs[1] = 4;
        xs[2] = 2;
        xs[3] = 1;
        // NOTE: first we ensure that there is no cutoff to insertion sort going on.
        final Config config = Config.setupConfig("true", "false", "", "0", "1", "");
        Sort<Integer> s = new MergeSort<>(xs.length, 1, config);
        Integer[] ys = s.sort(xs);
        assertEquals(Integer.valueOf(1), ys[0]);
        assertEquals(Integer.valueOf(2), ys[1]);
        assertEquals(Integer.valueOf(3), ys[2]);
        assertEquals(Integer.valueOf(4), ys[3]);
    }

    @Test
    public void testSort1b() throws Exception {
        Integer[] xs = new Integer[5];
        xs[0] = 3;
        xs[1] = 4;
        xs[2] = 2;
        xs[3] = 1;
        xs[4] = 0;
        // NOTE: first we ensure that there is no cutoff to insertion sort going on.
        final Config config = Config.setupConfig("true", "false", "", "0", "1", "");
        Sort<Integer> s = new MergeSort<>(xs.length, 1, config);
        Integer[] ys = s.sort(xs);
        assertEquals(Integer.valueOf(0), ys[0]);
        assertEquals(Integer.valueOf(1), ys[1]);
        assertEquals(Integer.valueOf(2), ys[2]);
        assertEquals(Integer.valueOf(3), ys[3]);
        assertEquals(Integer.valueOf(4), ys[4]);
    }

    /**
     * Test longer array with no insertion sort cutoff.
     *
     * @throws Exception
     */
    @Test
    public void testSort1c() throws Exception {
        Integer[] xs = new Integer[]{3, 17, 14, 5, 8, 6, 0, 20, 9, 5, 13, 2, 19, 7, 1, 23, 15};
        Integer[] expected = new Integer[]{0, 1, 2, 3, 5, 5, 6, 7, 8, 9, 13, 14, 15, 17, 19, 20, 23};
        // NOTE: first we ensure that there is no cutoff to insertion sort going on.
        final Config config = Config.setupConfig("true", "false", "", "0", "1", "");
        SortWithHelper<Integer> s = new MergeSort<>(xs.length, 1, config);
        Helper<Integer> helper = s.getHelper();
        assertArrayEquals(expected, s.sort(xs));
        assertEquals(0L, helper.getSwaps());
        assertEquals(51L, helper.getCompares());
        assertEquals(140L, helper.getCopies());
        assertEquals(297L, helper.getHits());
        assertEquals(102, helper.getLookups());
    }

    /**
     * Test longer array with insertion sort cutoff.
     *
     * @throws Exception
     */
    @Test
    public void testSort1d() throws Exception {
        Integer[] xs = new Integer[]{3, 17, 14, 5, 8, 6, 0, 20, 9, 5, 13, 2, 19, 7, 1, 23, 15};
        Integer[] expected = new Integer[]{0, 1, 2, 3, 5, 5, 6, 7, 8, 9, 13, 14, 15, 17, 19, 20, 23};
        // NOTE: first we ensure that there is no cutoff to insertion sort going on.
        final Config config = Config.setupConfig("true", "false", "", "0", "7", "");
        SortWithHelper<Integer> s = new MergeSort<>(xs.length, 1, config);
        Helper<Integer> helper = s.getHelper();
        assertArrayEquals(expected, s.sort(xs));
        assertEquals(15L, helper.getSwaps());
        assertEquals(51L, helper.getCompares());
        assertEquals(68L, helper.getCopies());
        assertEquals(162L, helper.getHits());
        assertEquals(102, helper.getLookups());
    }

    @Test
    public void testSort2() throws Exception {
        int k = 7; // Use 7 as the cutoff value
        int N = (int) Math.pow(2, k);
        // NOTE this depends on the cutoff value for merge sort.
        int levels = k - 2;
        final Config config = Config.setupConfig("true", "true", "0", "1", "" + k, "");
        final Helper<Integer> helper = HelperFactory.create("merge sort", N, config);
        System.out.println(helper);
        try (Sort<Integer> s = new MergeSort<>(helper)) {
            s.init(N);
            final Integer[] xs = helper.random(Integer.class, r -> r.nextInt(10000));
            assertEquals(Integer.valueOf(1360), xs[0]);
            helper.preProcess(xs);
            Integer[] ys = s.sort(xs);
            helper.postProcess(ys);
            final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
            final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
            System.out.println(statPack);
            final int compares = (int) statPack.getStatistics(InstrumentedComparableHelper.COMPARES).mean();
            final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
            final int fixes = (int) statPack.getStatistics(InstrumentedComparableHelper.FIXES).mean();
            final int swaps = (int) statPack.getStatistics(InstrumentedComparableHelper.SWAPS).mean();
            final int copies = (int) statPack.getStatistics(InstrumentedComparableHelper.COPIES).mean();
            final int hits = (int) statPack.getStatistics(InstrumentedComparableHelper.HITS).mean();
            final int worstCompares = N * k - N + 1;
            System.out.println("Compares" + compares);
            System.out.println("Worst Compares" + worstCompares);
            assertTrue(compares <= worstCompares);
            assertEquals(inversions, fixes);
            assertEquals(2 * levels * N, copies); // TODO check this
        }
    }

    @Test
    public void testSort3() throws Exception {
        int k = 7;
        int N = (int) Math.pow(2, k);
        final Helper<Integer> insertionHelper = HelperFactory.create("insertion sort", N, Config.setupConfig("true", "false", "0", "1", "", ""));
        System.out.println(insertionHelper);
        final Integer[] xs = insertionHelper.random(Integer.class, r -> r.nextInt(10000));
        assertEquals(Integer.valueOf(1360), xs[0]);
        new InsertionSort<Integer>(insertionHelper).mutatingSort(xs);
        insertionHelper.postProcess(xs);
        final Helper<Integer> mergeHelper = HelperFactory.create("merge sort", N, Config.setupConfig("true", "false", "", "0", "1", ""));
        System.out.println(mergeHelper);
        try (Sort<Integer> mergeSort = new MergeSort<>(mergeHelper)) {
            mergeSort.init(N);
            mergeHelper.preProcess(xs);
            // NOTE: we are sorting an already sorted array.
            Integer[] ys = mergeSort.sort(xs);
            mergeHelper.postProcess(ys);
            final PrivateMethodTester insertionPMT = new PrivateMethodTester(insertionHelper);
            final StatPack insertionStatPack = (StatPack) insertionPMT.invokePrivate("getStatPack");
            final int insertionInversions = (int) insertionStatPack.getStatistics(INVERSIONS).mean();
            final PrivateMethodTester mergePMT = new PrivateMethodTester(mergeHelper);
            final StatPack mergeStatPack = (StatPack) mergePMT.invokePrivate("getStatPack");
            System.out.println(mergeStatPack);
            final int compares = (int) mergeStatPack.getStatistics(InstrumentedComparableHelper.COMPARES).mean();
            final int fixes = (int) mergeStatPack.getStatistics(InstrumentedComparableHelper.FIXES).mean();
            final int swaps = (int) mergeStatPack.getStatistics(InstrumentedComparableHelper.SWAPS).mean();
            final int copies = (int) mergeStatPack.getStatistics(InstrumentedComparableHelper.COPIES).mean();
            final int expectedCompares = N * k / 2;
            assertEquals(expectedCompares, compares);
            assertEquals(insertionInversions, fixes);
            assertEquals(2 * k * N, copies); // XXX check this
        }
    }

    @Test
    public void testSort4() throws Exception {
        final int k = 7;
        ArrayList<Long> time = new ArrayList<Long>();
        final int N = (int) Math.pow(2, k);
        final Helper<Integer> helper1 = HelperFactory.create("insertion sort", N, Config.setupConfig2("true", "0", "1", "", "", "false", "false"));
        System.out.println(helper1);
        final Integer[] xs = helper1.random(Integer.class, r -> r.nextInt(10000));
        System.nanoTime();
        Sort<Integer> s = new MergeSort<>(xs.length, 1, config);
        for (int i = 0; i <= 1000; i++) {
            Long start = System.nanoTime();
            Integer[] ys = s.sort(xs);
            Long end = System.nanoTime();
            Long t = (end - start);
            time.add(t);
        }
        long sum = 0;
        for (Long t : time) {
            sum += t;
        }
        long avg = sum / 1000;
        System.out.println("average time random_CutOff: " + avg);

    }

    @Test
    public void testSort5() throws Exception {
        final int k = 7;
        ArrayList<Long> time = new ArrayList<Long>();
        final int N = (int) Math.pow(2, k);
        final Helper<Integer> helper1 = HelperFactory.create("insertion sort", N, Config.setupConfig2("true", "0", "1", "", "", "false", "true"));
        System.out.println(helper1);
        final Integer[] xs = helper1.random(Integer.class, r -> r.nextInt(10000));
        System.nanoTime();
        Sort<Integer> s = new MergeSort<>(xs.length, 1, config);
        for (int i = 0; i <= 1000; i++) {
            Long start = System.nanoTime();
            Integer[] ys = s.sort(xs);
            Long end = System.nanoTime();
            Long t = (end - start);
            time.add(t);
        }
        long sum = 0;
        for (Long t : time) {
            sum += t;
        }
        long avg = sum / 1000;
        System.out.println("average time random_Cutoff + NoCopy: " + avg);
    }

    @Test
    public void testSort6() throws Exception {
        final int k = 7;
        ArrayList<Long> time = new ArrayList<Long>();
        final int N = (int) Math.pow(2, k);
        final Helper<Integer> helper1 = HelperFactory.create("insertion sort", N, Config.setupConfig2("true", "0", "1", "", "", "true", "false"));
        System.out.println(helper1);
        final Integer[] xs = helper1.random(Integer.class, r -> r.nextInt(10000));
        System.nanoTime();
        Sort<Integer> s = new MergeSort<>(xs.length, 1, config);
        for (int i = 0; i <= 1000; i++) {
            Long start = System.nanoTime();
            Integer[] ys = s.sort(xs);
            Long end = System.nanoTime();
            Long t = (end - start);
            time.add(t);
        }
        long sum = 0;
        for (Long t : time) {
            sum += t;
        }
        long avg = sum / 1000;
        System.out.println("average time random_Cutoff + Insurance: " + avg);
    }

    @Test
    public void testSort7() throws Exception {
        final int k = 7;
        ArrayList<Long> time = new ArrayList<Long>();
        final int N = (int) Math.pow(2, k);
        final Helper<Integer> helper1 = HelperFactory.create("insertion sort", N, Config.setupConfig2("true", "0", "1", "", "", "true", "true"));
        System.out.println(helper1);
        final Integer[] xs = helper1.random(Integer.class, r -> r.nextInt(10000));
        System.nanoTime();
        Sort<Integer> s = new MergeSort<>(xs.length, 1, config);
        for (int i = 0; i <= 1000; i++) {
            Long start = System.nanoTime();
            Integer[] ys = s.sort(xs);
            Long end = System.nanoTime();
            Long t = (end - start);
            time.add(t);
        }
        long sum = 0;
        for (Long t : time) {
            sum += t;
        }
        long avg = sum / 1000;
        System.out.println("average time random_Cutoff + Insurance + NoCopy: " + avg);
    }

    @Test
    public void testSort8_partialsorted() throws Exception {
        final int k = 7;
        ArrayList<Long> time = new ArrayList<Long>();
        final int N = (int) Math.pow(2, k);
        final Helper<Integer> helper1 = HelperFactory.create("insertion sort", N, Config.setupConfig2("true", "0", "1", "", "", "false", "false"));
        System.out.println(helper1);
        Integer[] xs_sorted = helper1.random(Integer.class, r -> r.nextInt(10000));
        Arrays.sort(xs_sorted);
        Integer[] xs_unsorted = helper1.random(Integer.class, r -> r.nextInt(10000));
        ArrayList<Integer> xs_orignal = new ArrayList<Integer>(Arrays.asList(xs_sorted));
        xs_orignal.addAll(Arrays.asList(xs_unsorted));
        final Integer[] xs = xs_orignal.toArray(new Integer[xs_orignal.size()]);
        System.nanoTime();
        Sort<Integer> s = new MergeSort<>(xs.length, 1, config);
        for (int i = 0; i <= 1000; i++) {
            Long start = System.nanoTime();
            Integer[] ys = s.sort(xs);
            Long end = System.nanoTime();
            Long t = (end - start);
            time.add(t);
        }
        long sum = 0;
        for (Long t : time) {
            sum += t;
        }
        long avg = sum / 1000;
        System.out.println("partial sorted average time partialsorted_Cutoff: " + avg);

    }

    @Test
    public void testSort9_partialsorted() throws Exception {
        final int k = 7;
        ArrayList<Long> time = new ArrayList<Long>();
        final int N = (int) Math.pow(2, k);
        final Helper<Integer> helper1 = HelperFactory.create("insertion sort", N, Config.setupConfig2("true", "0", "1", "", "", "false", "true"));
        System.out.println(helper1);
        Integer[] xs_sorted = helper1.random(Integer.class, r -> r.nextInt(10000));
        Arrays.sort(xs_sorted);
        Integer[] xs_unsorted = helper1.random(Integer.class, r -> r.nextInt(10000));
        ArrayList<Integer> xs_orignal = new ArrayList<Integer>(Arrays.asList(xs_sorted));
        xs_orignal.addAll(Arrays.asList(xs_unsorted));
        final Integer[] xs = xs_orignal.toArray(new Integer[xs_orignal.size()]);

        System.nanoTime();
        Sort<Integer> s = new MergeSort<>(xs.length, 1, config);
        for (int i = 0; i <= 1000; i++) {
            Long start = System.nanoTime();
            Integer[] ys = s.sort(xs);
            Long end = System.nanoTime();
            Long t = (end - start);
            time.add(t);
        }
        long sum = 0;
        for (Long t : time) {
            sum += t;
        }
        long avg = sum / 1000;
        System.out.println("partial sorted average time partialsorted_Cutoff + NoCopy: " + avg);

    }

    @Test
    public void testSort10_partialsorted() throws Exception {
        final int k = 7;
        ArrayList<Long> time = new ArrayList<Long>();
        final int N = (int) Math.pow(2, k);
        final Helper<Integer> helper1 = HelperFactory.create("insertion sort", N, Config.setupConfig2("true", "0", "1", "", "", "true", "false"));
        System.out.println(helper1);
        Integer[] xs_sorted = helper1.random(Integer.class, r -> r.nextInt(10000));
        Arrays.sort(xs_sorted);
        Integer[] xs_unsorted = helper1.random(Integer.class, r -> r.nextInt(10000));
        ArrayList<Integer> xs_orignal = new ArrayList<Integer>(Arrays.asList(xs_sorted));
        xs_orignal.addAll(Arrays.asList(xs_unsorted));
        final Integer[] xs = xs_orignal.toArray(new Integer[xs_orignal.size()]);

        System.nanoTime();
        Sort<Integer> s = new MergeSort<>(xs.length, 1, config);
        for (int i = 0; i <= 1000; i++) {
            Long start = System.nanoTime();
            Integer[] ys = s.sort(xs);
            Long end = System.nanoTime();
            Long t = (end - start);
            time.add(t);
        }
        long sum = 0;
        for (Long t : time) {
            sum += t;
        }
        long avg = sum / 1000;
        System.out.println("partial sorted average time partialsorted_Cutoff + Insurance: " + avg);
    }

    @Test
    public void testSort11_partialsorted() throws Exception {
        final int k = 7;
        ArrayList<Long> time = new ArrayList<Long>();
        final int N = (int) Math.pow(2, k);
        final Helper<Integer> helper1 = HelperFactory.create("insertion sort", N, Config.setupConfig2("true", "0", "1", "", "", "true", "true"));
        System.out.println(helper1);
        Integer[] xs_sorted = helper1.random(Integer.class, r -> r.nextInt(10000));
        Arrays.sort(xs_sorted);
        Integer[] xs_unsorted = helper1.random(Integer.class, r -> r.nextInt(10000));
        ArrayList<Integer> xs_orignal = new ArrayList<Integer>(Arrays.asList(xs_sorted));
        xs_orignal.addAll(Arrays.asList(xs_unsorted));
        final Integer[] xs = xs_orignal.toArray(new Integer[xs_orignal.size()]);

        System.nanoTime();
        Sort<Integer> s = new MergeSort<>(xs.length, 1, config);
        for (int i = 0; i <= 1000; i++) {
            Long start = System.nanoTime();
            Integer[] ys = s.sort(xs);
            Long end = System.nanoTime();
            Long t = (end - start);
            time.add(t);
        }
        long sum = 0;
        for (Long t : time) {
            sum += t;
        }
        long avg = sum / 1000;
        System.out.println("partial sorted average time partialsorted_Cutoff + Insurance + NoCopy: " + avg);
    }

    @Test
    public void testSort12() {
        Config config1 = config.copy(MergeSort.MERGESORT, MergeSort.INSURANCE, "true");
        Config config2 = config1.copy(MergeSort.MERGESORT, MergeSort.NOCOPY, "false");
        SortWithHelper<Integer> sorter = new MergeSort<>(8, 1, config2);
        System.out.println("testing " + sorter);
        Helper<Integer> helper = sorter.getHelper();
        Integer[] ints = helper.random(Integer.class, r -> r.nextInt(1000));
        Integer[] sorted = sorter.sort(ints);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSort13() {
        SortWithHelper<Integer> sorter = new MergeSort<>(8, 1, config.copy(MergeSort.MERGESORT, MergeSort.INSURANCE, "false").copy(MergeSort.MERGESORT, MergeSort.NOCOPY, "true"));
        System.out.println("testing " + sorter);
        Helper<Integer> helper = sorter.getHelper();
        Integer[] ints = helper.random(Integer.class, r -> r.nextInt(1000));
        Integer[] sorted = sorter.sort(ints);
        assertTrue(helper.isSorted(sorted));
    }

    @Test
    public void testSort14() {
        SortWithHelper<Integer> sorter = new MergeSort<>(8, 1, config.copy(MergeSort.MERGESORT, MergeSort.INSURANCE, "true").copy(MergeSort.MERGESORT, MergeSort.NOCOPY, "true"));
        System.out.println("testing " + sorter);
        Helper<Integer> helper = sorter.getHelper();
        Integer[] ints = helper.random(Integer.class, r -> r.nextInt(1000));
        Integer[] sorted = sorter.sort(ints);
        assertTrue(helper.isSorted(sorted));
    }

    final static LazyLogger logger = new LazyLogger(MergeSort.class);

    private static Config config;
}
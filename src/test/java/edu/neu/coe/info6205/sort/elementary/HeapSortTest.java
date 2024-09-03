/*
 * Copyright (c) 2017. Phasmid Software
 */

package edu.neu.coe.info6205.sort.elementary;

import edu.neu.coe.info6205.sort.*;
import edu.neu.coe.info6205.util.Config;
import edu.neu.coe.info6205.util.LazyLogger;
import edu.neu.coe.info6205.util.PrivateMethodTester;
import edu.neu.coe.info6205.util.StatPack;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ALL")
public class HeapSortTest {

    @Test
    public void sort0() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        Integer[] xs = list.toArray(new Integer[0]);
        final Config config = Config.setupConfig("true", "false", "0", "1", "", "");
        NonComparableHelper<Integer> helper = HelperFactory.create("HeapSort", list.size(), config);
        helper.init(list.size());
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        SortWithHelper<Integer> sorter = new HeapSort<Integer>(helper);
        sorter.preProcess(xs);
//        System.out.println(Arrays.toString(xs));
        Integer[] ys = sorter.sort(xs);
//        System.out.println(Arrays.toString(ys));
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        assertEquals(7, (int) statPack.getStatistics(InstrumentedComparableHelper.COMPARES).mean());
        assertEquals(8, (int) statPack.getStatistics(InstrumentedComparableHelper.SWAPS).mean());
        assertEquals(30, (int) statPack.getStatistics(InstrumentedComparableHelper.HITS).mean());
    }

    @Test
    public void sort1() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        NonComparableHelper<Integer> helper = new NonInstrumentingComparableHelper<>("HeapSort", xs.length, Config.load(HeapSortTest.class));
        Sort<Integer> sorter = new HeapSort<Integer>(helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        System.out.println(sorter.toString());
    }

    @Test
    public void testMutatingHeapSort() throws IOException {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        NonComparableHelper<Integer> helper = new NonInstrumentingComparableHelper<>("HeapSort", xs.length, Config.load(HeapSortTest.class));
        Sort<Integer> sorter = new HeapSort<Integer>(helper);
        sorter.mutatingSort(xs);
        assertTrue(helper.isSorted(xs));
    }

    @Test
    public void sort2() throws Exception {
        final Config config = Config.setupConfig("true", "false", "0", "1", "", "");
        int n = 100;
        NonComparableHelper<Integer> helper = HelperFactory.create("HeapSort", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000));
        SortWithHelper<Integer> sorter = new HeapSort<Integer>(helper);
        sorter.preProcess(xs);
//        System.out.println(Arrays.toString(xs));
        Integer[] ys = sorter.sort(xs);
//        System.out.println(Arrays.toString(ys));
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final int compares = (int) statPack.getStatistics(InstrumentedComparableHelper.COMPARES).mean();
        // Since we set a specific seed, this should always succeed.
        assertEquals(1026, compares);
        final int swaps = (int) statPack.getStatistics(InstrumentedComparableHelper.SWAPS).mean();
        assertEquals(581, swaps);
        final int hits = (int) statPack.getStatistics(InstrumentedComparableHelper.HITS).mean();
        assertEquals(2 * compares + 2 * swaps, hits);
    }

    @Test
    public void sort3() throws Exception {
        final Config config = Config.setupConfig("true", "false", "0", "1", "", "");
        int n = 100;
        NonComparableHelper<Integer> helper = HelperFactory.create("HeapSort", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        Integer[] xs = new Integer[n];
        for (int i = 0; i < n; i++) xs[i] = n - i;
        SortWithHelper<Integer> sorter = new HeapSort<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        sorter.postProcess(ys);
        final int compares = (int) statPack.getStatistics(InstrumentedComparableHelper.COMPARES).mean();
        // Since we set a specific seed, this should always succeed.
        assertEquals(944, compares); // TODO check this.
    }

    final static LazyLogger logger = new LazyLogger(HeapSort.class);

}
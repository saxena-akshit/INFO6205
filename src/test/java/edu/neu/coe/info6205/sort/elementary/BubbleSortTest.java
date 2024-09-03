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

import static edu.neu.coe.info6205.util.ConfigTest.INVERSIONS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ALL")
public class BubbleSortTest {

    @Test
    public void sort0() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        Integer[] xs = list.toArray(new Integer[0]);
        final Config config = Config.setupConfig("true", "false", "0", "1", "", "");
        NonComparableHelper<Integer> helper = HelperFactory.create("BubbleSort", list.size(), config);
        helper.init(list.size());
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        SortWithHelper<Integer> sorter = new BubbleSort<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        sorter.postProcess(ys);
        assertTrue(helper.isSorted(ys));
        final int compares = (int) statPack.getStatistics(InstrumentedComparableHelper.COMPARES).mean();
        assertEquals(list.size() - 1, compares);
        final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
        assertEquals(0L, inversions);
        final int fixes = (int) statPack.getStatistics(InstrumentedComparableHelper.FIXES).mean();
        assertEquals(inversions, fixes);
    }

    @Test
    public void sort1() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        NonComparableHelper<Integer> helper = new NonInstrumentingComparableHelper<>("BubbleSort", xs.length, Config.load(BubbleSortTest.class));
        Sort<Integer> sorter = new BubbleSort<Integer>(helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        System.out.println(sorter.toString());
    }

    @Test
    public void testMutatingBubbleSort() throws IOException {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        NonComparableHelper<Integer> helper = new NonInstrumentingComparableHelper<>("BubbleSort", xs.length, Config.load(BubbleSortTest.class));
        Sort<Integer> sorter = new BubbleSort<Integer>(helper);
        sorter.mutatingSort(xs);
        assertTrue(helper.isSorted(xs));
    }

    @Test
    public void sort2() throws Exception {
        final Config config = Config.setupConfig("true", "false", "0", "1", "", "").copy(Instrument.INSTRUMENTING, Helper.FIXES, "true");
        int n = 100;
        NonComparableHelper<Integer> helper = HelperFactory.create("BubbleSort", n, config);
        helper.init(n);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        final StatPack statPack = (StatPack) privateMethodTester.invokePrivate("getStatPack");
        Integer[] xs = helper.random(Integer.class, r -> r.nextInt(1000));
        SortWithHelper<Integer> sorter = new BubbleSort<Integer>(helper);
        sorter.preProcess(xs);
        Integer[] ys = sorter.sort(xs);
        sorter.postProcess(ys);
        assertTrue(helper.isSorted(ys));
        final int compares = (int) statPack.getStatistics(InstrumentedComparableHelper.COMPARES).mean();
        // NOTE: these are suppoed to match within about 12%.
        // Since we set a specific seed, this should always succeed.
        // If we use true random seed and this test fails, just increase the delta a little.
        assertEquals(1.0, 2.0 * compares / n / (n - 1), 0.12);
        final int inversions = (int) statPack.getStatistics(INVERSIONS).mean();
        final int fixes = (int) statPack.getStatistics(InstrumentedComparableHelper.FIXES).mean();
        System.out.println(statPack);
        assertEquals(inversions, fixes);
    }

    final static LazyLogger logger = new LazyLogger(BubbleSort.class);

}
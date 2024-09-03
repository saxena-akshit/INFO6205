/*
 * Copyright (c) 2017. Phasmid Software
 */

package edu.neu.coe.info6205.sort.classic;

import com.google.common.collect.ImmutableList;
import edu.neu.coe.info6205.sort.InstrumentedComparableHelper;
import edu.neu.coe.info6205.sort.NonComparableHelper;
import edu.neu.coe.info6205.sort.NonInstrumentingComparableHelper;
import edu.neu.coe.info6205.sort.Sort;
import edu.neu.coe.info6205.util.Config;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ALL")
public class BucketSortTest {

    @Test
    public void testSort0() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravo", "Campion", "Able", "Aardvark", "Beetle");
        Sort<String> sorter = new BucketSort<String>(s -> classifyString(s), 3, 5, Config.load(BucketSortTest.class));
        String[] xs = list.toArray(new String[]{});
        sorter.mutatingSort(xs);
        System.out.println(Arrays.toString(xs));
        assertArrayEquals(new String[]{"Aardvark", "Able", "Beetle", "Bravo", "Campion"}, xs);
    }

    @Test
    public void testSort1() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravo", "Campion", "Able", "Aardvark", "Beetle");
        Sort<String> sorter = new BucketSort<String>(BucketSort::classifyStringInitial, BucketSort.ALPHABET_SIZE, 5, Config.load(BucketSortTest.class));
        String[] xs = list.toArray(new String[]{});
        sorter.mutatingSort(xs);
        System.out.println(Arrays.toString(xs));
        assertArrayEquals(new String[]{"Aardvark", "Able", "Beetle", "Bravo", "Campion"}, xs);
    }

    @Test
    public void testSort2() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravo", "Campion", "Able", "Aardvark", "Beetle", "C");
        Sort<String> sorter = new BucketSort<String>(BucketSort::classifyStringDigraph, BucketSort.DIGRAPHS_SIZE, 6, Config.load(BucketSortTest.class));
        String[] xs = list.toArray(new String[]{});
        sorter.mutatingSort(xs);
        System.out.println(Arrays.toString(xs));
        assertArrayEquals(new String[]{"Aardvark", "Able", "Beetle", "Bravo", "C", "Campion"}, xs);
    }

    @Test
    public void testSort3() throws IOException {
        ImmutableList<String> list = ImmutableList.of("bravo", "Campion", "able", "aArdvark", "beetle");
        BucketSort<String> sorter = BucketSort.CaseIndependentBucketSort(BucketSort::classifyStringInitial, BucketSort.ALPHABET_SIZE, 5, Config.load(BucketSortTest.class));
        String[] xs = list.toArray(new String[]{});
        sorter.mutatingSort(xs);
        System.out.println(Arrays.toString(xs));
        assertArrayEquals(new String[]{"aArdvark", "able", "beetle", "bravo", "Campion"}, xs);
    }

    @Test
    public void testSort4() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravo", "Campion", "Able", "Aardvark", "Beetle", "c");
        BucketSort<String> sorter = BucketSort.CaseIndependentBucketSort(BucketSort::classifyStringDigraph, BucketSort.DIGRAPHS_SIZE, 6, Config.load(BucketSortTest.class));
        String[] xs = list.toArray(new String[]{});
        sorter.mutatingSort(xs);
        System.out.println(Arrays.toString(xs));
        assertArrayEquals(new String[]{"Aardvark", "Able", "Beetle", "Bravo", "c", "Campion"}, xs);
    }

    @Test
    public void testSortFour() throws Exception {
        final List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        list.add(2);
        list.add(1);
        Integer[] xs = list.toArray(new Integer[0]);
        NonComparableHelper<Integer> helper = new NonInstrumentingComparableHelper<>("BucketSort", xs.length, Config.load(BucketSortTest.class));
        Sort<Integer> sorter = new BucketSort<>(null, 2, helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        System.out.println(sorter.toString());
    }

    @Test
    public void testSortN() throws Exception {
        int N = 10000;
        Integer[] xs = new Integer[N];
        Random random = new Random();
        for (int i = 0; i < N; i++) xs[i] = random.nextInt(10000);
        NonComparableHelper<Integer> helper = new NonInstrumentingComparableHelper<>("BucketSort", xs.length, Config.load(BucketSortTest.class));
        Sort<Integer> sorter = new BucketSort<>(null, 100, helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        System.out.println(sorter.toString());
    }

    @Test
    public void testSortInstrumented() throws Exception {
        int N = 10_000;
        final int bound = 20_000;
        int nBuckets = 100;
        final Config config = Config.setupConfig("true", "true", "0", "1", "", "");
        NonComparableHelper<Integer> helper = new InstrumentedComparableHelper<>("BucketSort", N, config);
        Integer[] xs = helper.random(N, Integer.class, r -> r.nextInt(bound));
        Sort<Integer> sorter = new BucketSort<>(null, nBuckets, helper);
        Integer[] ys = sorter.sort(xs);
        assertTrue(helper.isSorted(ys));
        System.out.println(sorter.toString());
        assertEquals(2L * N, helper.getCopies());
        assertEquals(261_328L, helper.getCompares());
        assertEquals(301_329L, helper.getHits());
        assertEquals(532_656L, helper.getLookups());
        long inversions = helper.getFixes();
        assertEquals((long) N * N / 4 / nBuckets, inversions, (long) N);
        assertEquals(inversions, helper.getFixes());
    }

    @Test
    public void init() {
    }

    @Test
    public void postProcess() {
    }

    @Test
    public void close() {
    }

    private static Integer classifyString(String s) {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(s.toUpperCase().charAt(0));
    }
}
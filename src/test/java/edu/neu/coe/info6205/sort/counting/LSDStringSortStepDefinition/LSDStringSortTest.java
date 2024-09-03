package edu.neu.coe.info6205.sort.counting.LSDStringSortStepDefinition;


import com.google.common.collect.ImmutableList;
import edu.neu.coe.info6205.sort.Sort;
import edu.neu.coe.info6205.sort.counting.LSDStringSort;
import edu.neu.coe.info6205.util.Config;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

public class LSDStringSortTest {

    // TODO add tests which test numbers of hits, lookups, etc.

    @Test
    public void testSort0() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravos", "Campion", "Ablexx", "Aardva", "Beetle");
        try (Sort<String> sorter = new LSDStringSort(list.size(), 0, LSDStringSort.comparatorASCII, 1, Config.load(LSDStringSort.class))) {
            String[] xs = list.toArray(new String[]{});
            sorter.mutatingSort(xs);
            System.out.println(Arrays.toString(xs));
            assertArrayEquals(new String[]{"Aardva", "Ablexx", "Beetle", "Bravos", "Campion"}, xs);
        }
    }

    @Test
    public void testSort1() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravos", "Campion", "Ablexx", "Aardva", "Beetle");
        try (Sort<String> sorter = new LSDStringSort(list.size(), 0, LSDStringSort.comparatorASCII, 1, Config.load(LSDStringSort.class))) {
            String[] xs = list.toArray(new String[]{});
            sorter.mutatingSort(xs);
            System.out.println(Arrays.toString(xs));
            assertArrayEquals(new String[]{"Aardva", "Ablexx", "Beetle", "Bravos", "Campion"}, xs);
        }
    }

    @Test
    public void testSort2() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravos", "Campion", "Ablexx", "Aardva", "Beetle", "C     ");
        try (Sort<String> sorter = new LSDStringSort(list.size(), 0, LSDStringSort.comparatorASCII, 1, Config.load(LSDStringSort.class))) {
            String[] xs = list.toArray(new String[]{});
            sorter.mutatingSort(xs);
            System.out.println(Arrays.toString(xs));
            assertArrayEquals(new String[]{"Aardva", "Ablexx", "Beetle", "Bravos", "C     ", "Campion"}, xs);
        }
    }

    //        @Test  // Need to do implement case-independent comparisons (as an option) in LSDStringSort,
    public void testSort4() throws IOException {
        ImmutableList<String> list = ImmutableList.of("Bravos", "Campion", "Ablexx", "Aardva", "Beetle", "c     ");
        try (Sort<String> sorter = new LSDStringSort(list.size(), 0, LSDStringSort.comparatorASCII, 1, Config.load(LSDStringSort.class))) {
            String[] xs = list.toArray(new String[]{});
            sorter.mutatingSort(xs);
            System.out.println(Arrays.toString(xs));
            assertArrayEquals(new String[]{"Aardva", "Ablexx", "Beetle", "Bravos", "c     ", "Campion"}, xs);
        }
    }
}
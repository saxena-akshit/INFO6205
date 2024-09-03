package edu.neu.coe.info6205.util;

import org.junit.Test;

import java.io.IOException;

import static edu.neu.coe.info6205.util.SortBenchmark.minComparisons;
import static junit.framework.TestCase.assertEquals;

public class SortBenchmarkTest {

    @Test
    public void testDoMain() throws IOException {
        Config config1 = Config.load(SortBenchmark.class).copy("helper", "instrument", "false");
        new SortBenchmark(config1).doMain(new String[]{"1000"});
        Config config2 = config1.copy("helper", "instrument", "true");
        new SortBenchmark(config2).doMain(new String[]{"1000"});
    }

    @Test
    public void testParseInt() {
        assertEquals(1L, SortBenchmark.parseInt("1"));
        assertEquals(1024L, SortBenchmark.parseInt("1k"));
        assertEquals(1024L, SortBenchmark.parseInt("1K"));
        assertEquals(1048576L, SortBenchmark.parseInt("1m"));
        assertEquals(1048576L, SortBenchmark.parseInt("1M"));
        assertEquals(1073741824L, SortBenchmark.parseInt("1g"));
        assertEquals(1073741824L, SortBenchmark.parseInt("1G"));
    }

    @Test
    public void sortLocalDateTimes() {
    }

    @Test
    public void benchmarkStringSorters() {
    }

    @Test
    public void benchmarkStringSortersInstrumented() {
    }

    @Test
    public void runStringSortBenchmark() {
    }

    @Test
    public void testRunStringSortBenchmark() {
    }

    @Test
    public void runIntegerSortBenchmark() {
    }

    @Test
    public void meanInversions() {
    }

    @Test
    public void getLeipzigWords() {
    }

    @Test
    public void testSortLocalDateTimes() {
    }

    @Test
    public void testBenchmarkStringSorters() {
    }

    @Test
    public void testRunStringSortBenchmark1() {
    }

    @Test
    public void testRunStringSortBenchmark2() {
    }

    @Test
    public void testRunIntegerSortBenchmark() {
    }

    @Test
    public void testMinComparisons() {
        assertEquals(8769, minComparisons(1024), 0.1);
        assertEquals(19.46E6, minComparisons(1024 * 1024), 10000);
        assertEquals(31E9, minComparisons(1024 * 1024 * 1024), 500000000);
    }

    @Test
    public void testMeanInversions() {
    }

    @Test
    public void testGetLeipzigWords() {
    }
}
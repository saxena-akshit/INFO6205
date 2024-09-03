package edu.neu.coe.info6205.sort;

import edu.neu.coe.info6205.util.Config;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HelperTest {
    final Config config = Config.setupConfig("true", "false", "0", "1", "", "");

    final InstrumentedComparatorHelper<String> helper = new InstrumentedComparatorHelper<>("test", String::compareToIgnoreCase, 20, config);

    @Test
    public void sortPair() {
        Instrument instrumenter = helper.instrumenter;
        String[] ab = new String[]{"a", "b"};
        String[] xs0 = new String[]{"a", "b"};
        helper.sortPair(xs0, 0, 2);
        assertArrayEquals(ab, xs0);
        assertEquals(1L, instrumenter.getCompares());
        assertEquals(0L, instrumenter.getSwaps());
        assertEquals(2L, instrumenter.getHits());
        assertEquals(2, instrumenter.getLookups());
        String[] xs1 = new String[]{"b", "a"};
        helper.sortPair(xs1, 0, 2);
        assertArrayEquals(ab, xs1);
        assertEquals(2L, instrumenter.getCompares());
        assertEquals(1, instrumenter.getSwaps());
        assertEquals(4, instrumenter.getHits());
        assertEquals(4, instrumenter.getLookups());
    }

    @Test
    public void sortTrio() {
        Instrument instrumenter = helper.instrumenter;
        String[] abc = new String[]{"a", "b", "c"};
        String[] xs0 = new String[]{"a", "b", "c"};
        helper.sortTrio(xs0, 0, 3);
        assertArrayEquals(abc, xs0);
        assertEquals(2L, instrumenter.getCompares());
        assertEquals(0L, instrumenter.getSwaps());
        assertEquals(3L, instrumenter.getHits());
        assertEquals(4, helper.getLookups());
        String[] xs1 = new String[]{"a", "c", "b"};
        helper.sortTrio(xs1, 0, 3);
        assertArrayEquals(abc, xs1);
        assertEquals(5L, instrumenter.getCompares());
        assertEquals(1L, instrumenter.getSwaps());
        assertEquals(7L, instrumenter.getHits());
        assertEquals(10, helper.getLookups());
        String[] xs2 = new String[]{"c", "a", "b"};
        helper.sortTrio(xs2, 0, 3);
        assertArrayEquals(abc, xs2);
        assertEquals(8L, instrumenter.getCompares());
        assertEquals(3L, instrumenter.getSwaps());
        assertEquals(11L, instrumenter.getHits());
        assertEquals(16, helper.getLookups());
        String[] xs3 = new String[]{"b", "a", "c"};
        helper.sortTrio(xs3, 0, 3);
        assertArrayEquals(abc, xs3);
        assertEquals(10L, instrumenter.getCompares());
        assertEquals(4L, instrumenter.getSwaps());
        assertEquals(14L, instrumenter.getHits());
        assertEquals(20, helper.getLookups());
        String[] xs4 = new String[]{"b", "c", "a"};
        helper.sortTrio(xs4, 0, 3);
        assertArrayEquals(abc, xs4);
        assertEquals(13L, instrumenter.getCompares());
        assertEquals(6L, instrumenter.getSwaps());
        assertEquals(18L, instrumenter.getHits());
        assertEquals(26, helper.getLookups());
        String[] xs5 = new String[]{"c", "b", "a"};
        helper.sortTrio(xs5, 0, 3);
        assertArrayEquals(abc, xs5);
        assertEquals(16L, instrumenter.getCompares());
        assertEquals(9L, instrumenter.getSwaps());
        assertEquals(22L, instrumenter.getHits());
        assertEquals(32, helper.getLookups());
    }
}
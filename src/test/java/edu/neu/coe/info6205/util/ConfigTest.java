package edu.neu.coe.info6205.util;

import edu.neu.coe.info6205.sort.InstrumentedComparableHelper;
import edu.neu.coe.info6205.sort.Instrumenter;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static edu.neu.coe.info6205.util.Config.SEED;
import static org.junit.Assert.*;

public class ConfigTest {

    @Test
    public void testConfig() throws IOException {
        final Config config = Config.load();
        String name = config.get("main", "version");
        System.out.println("ConfigTest: " + name);
    }

    @Test
    public void testConfigFixed() {
        final Config config = Config.setupConfig(TRUE, "false", "0", "10", "", "");
        assertTrue(config.isInstrumented());
        assertEquals(0L, config.getSeed());
        assertEquals(10, config.getInt(INSTRUMENTING, INVERSIONS, 0));
    }

    @Test
    public void testCopy1() {
        final Config config = Config.setupConfig(FALSE, "false", "0", "", "", "");
        long originalSeed = config.getSeed();
        Config config1 = config.copy(Config.HELPER, SEED, "1");
        assertEquals(originalSeed, config.getSeed());
        assertEquals(1, config1.getSeed());
    }

    @Test
    public void testCopy2() {
        final Config config = Config.setupConfig(FALSE, "false", "", "", "", "");
        String junk = "junk";
        assertEquals(-1, config.getInt(Config.HELPER, junk, -1));
        Config config1 = config.copy(Config.HELPER, junk, "1");
        assertEquals(1, config1.getInt(Config.HELPER, junk, -1));
    }

    // NOTE: we ignore this for now, because this would need to run before any other tests in order to work as originally designed.
    @Ignore
    public void testUnLogged() throws IOException {
        final Config config = Config.load();
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(config);
        assertTrue((Boolean) privateMethodTester.invokePrivate("unLogged", Config.HELPER + "." + SEED));
        assertFalse((Boolean) privateMethodTester.invokePrivate("unLogged", Config.HELPER + "." + SEED));
    }

    public static final String TRUE = "true";
    public static final String FALSE = "";
    public static final String INSTRUMENTING = InstrumentedComparableHelper.INSTRUMENTING;
    public static final String INVERSIONS = Instrumenter.INVERSIONS;
    public static final String SWAPS = InstrumentedComparableHelper.SWAPS;
    public static final String COMPARES = InstrumentedComparableHelper.COMPARES;
    public static final String COPIES = InstrumentedComparableHelper.COPIES;
    public static final String FIXES = InstrumentedComparableHelper.FIXES;
    public static final String INSURANCE = "";
    public static final String NOCOPY = "";
}
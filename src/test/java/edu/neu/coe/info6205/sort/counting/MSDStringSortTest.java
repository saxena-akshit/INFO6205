package edu.neu.coe.info6205.sort.counting;

import edu.neu.coe.info6205.sort.*;
import edu.neu.coe.info6205.sort.linearithmic.QuickSort_3way;
import edu.neu.coe.info6205.util.*;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MSDStringSortTest {

    final String[] input = "she sells seashells by the seashore the shells she sells are surely seashells".split(" ");
    final String[] expected = "are by seashells seashells seashore sells sells she she shells surely the the".split(" ");

    @Test
    public void sort0() {
        int n = input.length;
        Config config = Config.setupConfig("true", "false", "0", "1", "4", "").copy(Config.HELPER, "msdcutoff", "10");
        try (Sort<String> sorter = new MSDStringSort(CodePointMapper.ASCIIExt, n, 1, config)) {
            String[] sorted = sorter.sort(input);
            System.out.println(Arrays.toString(sorted));
            assertArrayEquals(expected, sorted);
        }
    }

    @Test
    public void sort1() throws IOException {
        int n = 1000;
        String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
        Config config = Config.load(MSDStringSortTest.class).copy(Config.HELPER, "seed", "1");
        try (MSDStringSort sorter = new MSDStringSort(CodePointMapper.ASCIIExt, n, 1, config)) {
            Helper<String> helper = sorter.getHelper();
            final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
            assertEquals(n, xs.length);
            String[] ys = sorter.sort(xs);
            System.out.println(Arrays.toString(ys));
            assertEquals("African-American", ys[0]);
            assertEquals("Palestinian", ys[16]);

        }
    }

    @Test
    public void sort2() throws IOException {
        int n = input.length;
        try (MSDStringSort sorter = new MSDStringSort(CodePointMapper.English, "test", n, Config.load(MSDStringSortTest.class), 1)) {
            String[] sorted = sorter.sort(input);
            System.out.println(Arrays.toString(sorted));
            assertArrayEquals(expected, sorted);
        }}

    @Test
    public void sort3() throws IOException {
        int n = 100;
        String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
        try (MSDStringSort sorter = new MSDStringSort(CodePointMapper.English, "test", n, Config.load(MSDStringSortTest.class), 1)) {
            Helper<String> helper = sorter.getHelper();
            final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
            assertEquals(n, xs.length);
            String[] ys = sorter.sort(xs);
            System.out.println(Arrays.toString(ys));
            assertTrue(helper.isSorted(ys));
        }}

    @Test
    public void sort4() throws IOException {
        int n = 1000;
        int seed = 948;
        String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
        Config config = Config.load(MSDStringSortTest.class).copy(Config.HELPER, "seed", String.valueOf(seed));
        try (MSDStringSort sorter = new MSDStringSort(CodePointMapper.English, "test", n, config, 1)) {
            Helper<String> helper = sorter.getHelper();
            final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
            assertEquals(n, xs.length);
            String[] ys = sorter.sort(xs);
            System.out.println(Arrays.toString(ys));
            assertTrue(helper.isSorted(ys));
        }}

    //    @Test
    public void sort5() throws IOException {
        int n = 1000;
        for (int seed = 0; seed < 1000; seed++) {
            System.out.println(seed);
            String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
            Config config = Config.load(MSDStringSortTest.class).copy(Config.HELPER, "seed", String.valueOf(seed));
            try (MSDStringSort sorter = new MSDStringSort(CodePointMapper.English, "test", n, config, 1)) {
                Helper<String> helper = sorter.getHelper();
                final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
                assertEquals(n, xs.length);
                String[] ys = sorter.sort(xs);
                assertTrue(helper.isSorted(ys));
            }
        }
    }

    @Test
    public void testX() {
        // CONSIDER this is the only reference to QuickSortThreeWayByFunction -- do we really need this test?
        Config config = Config.setupConfig("true", "false", "0", "1", "1", "");
        final NonComparableHelper<String> helper = HelperFactory.create("quick sort", input.length, config);
        MSDStringSort.QuickSortThreeWayByFunction sorter = new MSDStringSort.QuickSortThreeWayByFunction(helper);
        String[] sorted = sorter.sort(input);
        System.out.println(Arrays.toString(sorted));
        assertArrayEquals(expected, sorted);
    }

    @Test
    public void testY() {
        Config config = Config.setupConfig("true", "false", "0", "1", "1", "").copy(Config.HELPER, "msdcutoff", "1");
        MSDStringSort sorter = new MSDStringSort(CodePointMapper.ASCIIExt, "MSD", input.length, config, 1);
        Helper<String> helper = sorter.getHelper();
        String[] sorted = sorter.sort(input);
        sorter.close();
        System.out.println(Arrays.toString(sorted));
        assertArrayEquals(expected, sorted);
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        assertEquals(0L, privateMethodTester.invokePrivate("getSwaps"));
        assertEquals(0L, privateMethodTester.invokePrivate("getCompares"));
        assertEquals(124L, privateMethodTester.invokePrivate("getCopies"));
        // NOTE this was only 310 before. Please check.
        assertEquals(4962L, privateMethodTester.invokePrivate("getHits"));
    }

    @Test
    public void testZ0() {
        String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
        Config config = Config.setupConfig("true", "false", "0", "1", "16", "").copy(Config.HELPER, "msdcutoff", "1");
        MSDStringSort sorter = new MSDStringSort(CodePointMapper.ASCIIExt, "MSD", 1024, config, 1);
        Helper<String> helper = sorter.getHelper();
        final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
        String[] sorted = sorter.sort(xs);
        sorter.close();
        assertTrue(helper.isSorted(sorted));
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        // TODO check these values
        assertEquals(0L, privateMethodTester.invokePrivate("getSwaps"));
        assertEquals(0L, privateMethodTester.invokePrivate("getCompares"));
        assertEquals(10436L, privateMethodTester.invokePrivate("getCopies"));
        // NOTE this was previousl just 26090. Please check it.
        assertEquals(293703L, privateMethodTester.invokePrivate("getHits"));
    }

    @Test
    public void testZ1() {
        String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
        Config config = Config.setupConfig("true", "false", "0", "1", "1", "").copy(Config.HELPER, "msdcutoff", "128");
        MSDStringSort sorter = new MSDStringSort(CodePointMapper.ASCIIExt, "MSD", 1024, config, 1);
        Helper<String> helper = sorter.getHelper();
        final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
        String[] sorted = sorter.sort(xs);
        sorter.close();
        assertTrue(helper.isSorted(sorted));
        final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
        // TODO check these values
        assertEquals(4157L, privateMethodTester.invokePrivate("getSwaps"));
        assertEquals(4895L, privateMethodTester.invokePrivate("getCompares"));
        // NOTE this was 16979 before. Please check.
        assertEquals(2308L, privateMethodTester.invokePrivate("getCopies"));
        assertEquals(16979L, privateMethodTester.invokePrivate("getHits"));
    }

    @Test
    public void testQ256() {
        String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
        Config baseConfig = Config.setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(29_276_440L, runMSD(baseConfig.copy(Config.HELPER, "msdcutoff", "256"), 1_048_576));
    }

    @Test
    public void testQ512() {
        String[] words = getWords("3000-common-words.txt", MSDStringSortTest::lineAsList);
        Config baseConfig = Config.setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(28_353_067L, runMSD(baseConfig.copy(Config.HELPER, "msdcutoff", "512"), 1_048_576));
    }

    @Test
    public void testQ2048() {
        Config baseConfig = Config.setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(28_371_261L, runMSD(baseConfig.copy(Config.HELPER, "msdcutoff", "2048"), 1_048_576));
    }

    @Test
    public void testQ4096() {
        Config baseConfig = Config.setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(29_025_790L, runMSD(baseConfig.copy(Config.HELPER, "msdcutoff", "4096"), 1_048_576));
    }

    @Test
    public void testQ8192() {
        Config baseConfig = Config.setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(29_783_908L, runMSD(baseConfig.copy(Config.HELPER, "msdcutoff", "8192"), 1_048_576));
    }

    @Test
    public void testQ16384() {
        Config baseConfig = Config.setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(30_562_786L, runMSD(baseConfig.copy(Config.HELPER, "msdcutoff", "16384"), 1_048_576));
    }

//    @Test XXX this is slow
    public void testQ32M() {
        Config baseConfig = Config.setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(1_366_801_150L, runMSD(baseConfig.copy(Config.HELPER, "msdcutoff", "256"), 32_768_000));
    }

    @Test
    public void testQuick() {
        Config config = Config.setupConfig("true", "false", "0", "1", "16", "");
        assertEquals(41_118_955L, runQuick(config));
    }

    private static long runMSD(Config config, final int n) {
        String resource = "eng-uk_web_2002_" + "100K" + "-sentences.txt";
        try {
            String[] words = SortBenchmarkHelper.getWords(resource, SortBenchmark::getLeipzigWords);
            MSDStringSort sorter = new MSDStringSort(CodePointMapper.ASCIIExt, "MSD", n, config, 1);
            Helper<String> helper = sorter.getHelper();
            final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
            String[] sorted = sorter.sort(xs);
            helper.postProcess(sorted);
            sorter.close();
            final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
            return (Long) privateMethodTester.invokePrivate("getHits");
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            return -1L;
        }
    }

    private static long runQuick(Config config) {
        String resource = "eng-uk_web_2002_" + "100K" + "-sentences.txt";
        try {
            String[] words = SortBenchmarkHelper.getWords(resource, SortBenchmark::getLeipzigWords);
            SortWithHelper<String> sorter = new QuickSort_3way<>(1048576, config);
            Helper<String> helper = sorter.getHelper();
            final String[] xs = helper.random(String.class, r -> words[r.nextInt(words.length)]);
            System.out.println(Arrays.toString(Arrays.stream(xs, 0, 200).toArray()));
            String[] sorted = sorter.sort(xs);
            sorter.close();
            helper.isSorted(sorted);
            final PrivateMethodTester privateMethodTester = new PrivateMethodTester(helper);
            return (Long) privateMethodTester.invokePrivate("getHits");
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            return -1L;
        }
    }

    /**
     * Method to open a resource relative to this class and from the corresponding File, get an array of Strings.
     *
     * @param resource           the URL of the resource containing the Strings required.
     * @param stringListFunction a function which takes a String and splits into a List of Strings.
     * @return an array of Strings.
     */
    static String[] getWords(final String resource, final Function<String, List<String>> stringListFunction) {
        try {
            final File file = new File(getPathname(resource, MSDStringSortTest.class));
            final String[] result = getWordArray(file, stringListFunction, 2);
            System.out.println("getWords: testing with " + Utilities.formatWhole(result.length) + " unique words: from " + file);
            return result;
        } catch (final FileNotFoundException e) {
            System.out.println("Cannot find resource: " + resource);
            return new String[0];
        }
    }

    private static List<String> getWordList(final FileReader fr, final Function<String, List<String>> stringListFunction, final int minLength) {
        final List<String> words = new ArrayList<>();
        for (final Object line : new BufferedReader(fr).lines().toArray())
            words.addAll(stringListFunction.apply((String) line));
        return words.stream().distinct().filter(s -> s.length() >= minLength).collect(Collectors.toList());
    }


    /**
     * Method to read given file and return a String[] of its content.
     *
     * @param file               the file to read.
     * @param stringListFunction a function which takes a String and splits into a List of Strings.
     * @param minLength          the minimum acceptable length for a word.
     * @return an array of Strings.
     */
    static String[] getWordArray(final File file, final Function<String, List<String>> stringListFunction, final int minLength) {
        try (final FileReader fr = new FileReader(file)) {
            return getWordList(fr, stringListFunction, minLength).toArray(new String[0]);
        } catch (final IOException e) {
            System.out.println("Cannot open file: " + file);
            return new String[0];
        }
    }

    static List<String> lineAsList(final String line) {
        final List<String> words = new ArrayList<>();
        words.add(line);
        return words;
    }

    private static String getPathname(final String resource, @SuppressWarnings("SameParameterValue") final Class<?> clazz) throws FileNotFoundException {
        final URL url = clazz.getClassLoader().getResource(resource);
        if (url != null) return url.getPath();
        throw new FileNotFoundException(resource + " in " + clazz);
    }

}
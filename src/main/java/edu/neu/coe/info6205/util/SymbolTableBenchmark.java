/*
  (c) Copyright 2018, 2019 Phasmid Software
 */
package edu.neu.coe.info6205.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static edu.neu.coe.info6205.util.SortBenchmarkHelper.getWords;
import static edu.neu.coe.info6205.util.Utilities.formatWhole;

/**
 * Class to test the comparative efficiency of:
 * (1) build a hash table and get the values in key order;
 * (2) build a Red-black tree and the values in key order;
 */
public class SymbolTableBenchmark {

    public SymbolTableBenchmark(Config config) {
        this.config = config;
    }

    public static void main(String[] args) throws IOException {
        Config config = Config.load(SymbolTableBenchmark.class);
        logger.info("SortBenchmark.main: " + config.get("SortBenchmark", "version") + " with word counts: " + Arrays.toString(args));
        if (args.length == 0) logger.warn("No word counts specified on the command line");
        new SymbolTableBenchmark(config).doMain(args);
    }

    void doMain(String[] args) {
        doBenchmarks(getWordCounts(args));
    }

    /**
     * Method to run pure (non-instrumented) string sorter benchmarks.
     * <p>
     * NOTE: this is package-private because it is used by unit tests.
     *
     * @param words  the word source.
     * @param nWords the number of words to be sorted.
     * @param nRuns  the number of runs.
     */
    void benchmarkStringSorters(String[] words, int nWords, int nRuns) {
        logger.info("Testing pure sorts with " + formatWhole(nRuns) + " runs of sorting " + formatWhole(nWords) + " words");
        Random random = new Random();
        runHashTableBenchmark(words, nWords, nRuns, random);
        runRBTreeBenchmark(words, nWords, nRuns, random);
    }

    private static void runHashTableBenchmark(String[] words, int nWords, int nRuns, Random random) {
        Benchmark<String[]> benchmark = new Benchmark_Timer<>("hashTable", null, SymbolTableBenchmark::buildAndRenderHashTable, null);
        doPureBenchmark(words, nWords, nRuns, random, benchmark);
    }

    private static void runRBTreeBenchmark(String[] words, int nWords, int nRuns, Random random) {
        Benchmark<String[]> benchmark = new Benchmark_Timer<>("RBTree", null, SymbolTableBenchmark::buildRBTree, null);
        doPureBenchmark(words, nWords, nRuns, random, benchmark);
    }

    private static void buildAndRenderHashTable(String[] xs) {
        Map<String, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < xs.length; i++)
            hashMap.put(xs[i], i);
        List<String> keys = new ArrayList<>(((Map<String, Integer>) new TreeMap<>(hashMap)).keySet());
        Collections.sort(keys);
//        List<Integer> values = new ArrayList<>();
//        for (String key : keys) values.add(hashMap.get(key));
        // CONSIDER returning values.
    }

    private static void buildRBTree(String[] xs) {
        Map<String, Integer> treeMap = new TreeMap<>();
        for (int i = 0; i < xs.length; i++)
            treeMap.put(xs[i], i);
//        List<Integer> values = new ArrayList<>();
//        for (String key : treeMap.keySet()) values.add(treeMap.get(key));
        // CONSIDER returning values.
    }

    private void doBenchmarks(Stream<Integer> wordCounts) {
        logger.info("Beginning String sorts");
        wordCounts.forEach(this::doSymbolTableBenchmark);
    }

    private void doSymbolTableBenchmark(int x) {
        String resource = "eng-uk_web_2002_" + (x < 50000 ? "10K" : "100K") + "-sentences.txt";
        try {
            String[] words = getWords(resource, SymbolTableBenchmark::getLeipzigWords);
            benchmarkStringSorters(words, x, 1000);
        } catch (FileNotFoundException e) {
            logger.warn("Unable to find resource: " + resource, e);
        }
    }

    /**
     * For mergesort, the number of array accesses is actually four times the number of comparisons (six when noCopy is false).
     * That's because, in addition to each comparison, there will be approximately two copy operations.
     * Thus, in the case where comparisons are based on primitives,
     * the normalized time per run should approximate the time for one array access.
     */
    public final static TimeLogger[] timeLoggersLinearithmic = {
            new TimeLogger("Raw time per run (mSec): ", null),
            new TimeLogger("Normalized time per run (n log n): ", SymbolTableBenchmark::minComparisons)
    };

    final static LazyLogger logger = new LazyLogger(SymbolTableBenchmark.class);

    final static Pattern regexLeipzig = Pattern.compile("[~\\t]*\\t(([\\s\\p{Punct}\\uFF0C]*\\p{L}+)*)");

    /**
     * This is based on log2(n!)
     *
     * @param n the number of elements.
     * @return the minimum number of comparisons possible to sort n randomly ordered elements.
     */
    static double minComparisons(int n) {
        double lgN = Utilities.lg(n);
        return n * (lgN - LgE) + lgN / 2 + 1.33;
    }

    public static Collection<String> getLeipzigWords(String line) {
        return getWords(regexLeipzig, line);
    }

    private static void doPureBenchmark(String[] words, int nWords, int nRuns, Random random, Benchmark<String[]> benchmark) {
        // CONSIDER we should manage the space returned by fillRandomArray and deallocate it after use.
        final double time = benchmark.runFromSupplier(() -> Utilities.fillRandomArray(String.class, random, nWords, r -> words[r.nextInt(words.length)]), nRuns);
        for (TimeLogger timeLogger : timeLoggersLinearithmic) timeLogger.log("", time, nWords);
    }

    private static Stream<Integer> getWordCounts(String[] args) {
        return Arrays.stream(args).map(Integer::parseInt);
    }

    private static final double LgE = Utilities.lg(Math.E);

    /**
     * NOTE currently unused.
     *
     * @return the current configuration
     */
    private Config getConfig() {
        return config;
    }

    private final Config config;
}
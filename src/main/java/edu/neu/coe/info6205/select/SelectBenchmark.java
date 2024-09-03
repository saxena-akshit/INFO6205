package edu.neu.coe.info6205.select;

import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.NonInstrumentingComparableHelper;
import edu.neu.coe.info6205.util.Benchmark;
import edu.neu.coe.info6205.util.Benchmark_Timer;
import edu.neu.coe.info6205.util.Config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

/**
 * @author Suchita Dabir, 2024
 */
public class SelectBenchmark {

    public SelectBenchmark(int runs, int n, int m) {
        this.runs = runs;
        this.safetyFactor = 10;
        this.n = n;
    }

    public String runBenchmarks() throws IOException {
        StringBuilder myContent = new StringBuilder();
        int N = n * safetyFactor;
        System.out.println("SelectBenchmark: N=" + N);
        Config config = Config.load(SelectBenchmark.class);
        try (Helper<Integer> helper = new NonInstrumentingComparableHelper<>("SelectBenchmark", N, config)) {
            helper.init(N);
            Supplier<Integer[]> randomSupplier = () -> helper.random(Integer.class, Random::nextInt);
            Supplier<Integer[]> orderedSupplier = () -> helper.ordered(N, Integer.class, i -> i);
            Supplier<Integer[]> partialSupplier = () -> helper.partialOrdered(N, Integer.class, i -> i);
            Supplier<Integer[]> reverseSupplier = () -> helper.reverse(N, Integer.class, i -> i);
            int k = N / 2;
            QuickSelect<Integer> quickSelect = new QuickSelect<>(k);
            SlowSelect<Integer> slowSelect = new SlowSelect<>(k);
            String quickSelector = "QuickSelect";
            String slowSelector = "SlowSelect";
            String random = "random";
            String ordered = "ordered";
            String partiallyOrdered = "partially-ordered";
            String reverseOrdered = "reverse-ordered";

            String s0 = quickSelector + "," + random;
            double d0 = doBenchmark(quickSelector, quickSelect, k, randomSupplier, runs);
            myContent.append(resultMessage(s0, d0, N));

            double d1 = doBenchmark(quickSelector, quickSelect, k, orderedSupplier, runs);
            String s1 = quickSelector + "," + ordered;
            myContent.append(resultMessage(s1, d1, N));

            double d2 = doBenchmark(quickSelector, quickSelect, k, partialSupplier, runs);
            String s2 = quickSelector + "," + partiallyOrdered;
            myContent.append(resultMessage(s2, d2, N));

            double d3 = doBenchmark(quickSelector, quickSelect, k, reverseSupplier, runs);
            String s3 = quickSelector + "," + reverseOrdered;
            myContent.append(resultMessage(s3, d3, N));

            double d4 = doBenchmark(slowSelector, slowSelect, k, randomSupplier, runs);
            String s4 = slowSelector + "," + random;
            myContent.append(resultMessage(s4, d4, N));

            double d5 = doBenchmark(slowSelector, slowSelect, k, orderedSupplier, runs);
            String s5 = slowSelector + "," + ordered;
            myContent.append(resultMessage(s5, d5, N));

            double d6 = doBenchmark(slowSelector, slowSelect, k, partialSupplier, runs);
            String s6 = slowSelector + "," + partiallyOrdered;
            myContent.append(resultMessage(s6, d6, N));

            double d7 = doBenchmark(slowSelector, slowSelect, k, reverseSupplier, runs);
            String s7 = slowSelector + "," + reverseOrdered;
            myContent.append(resultMessage(s7, d7, N));

            return myContent.toString();
        }
    }

    private String resultMessage(String s, double d, int n) {
        // NOTE leave this as StringBuilder
        return new StringBuilder().append(s).append(",").append(runs).append(",").append(n).append(",").append(d).append("\n").toString();
    }

    private static double doBenchmark(String description, Select<Integer> select, int k, Supplier<Integer[]> supplier, final int runs) {
        final Benchmark<Integer[]> benchmark = new Benchmark_Timer<>(
                description,
                (xs) -> Arrays.copyOf(xs, xs.length),
                (xs) -> select.select(xs, k),
                null
        );
        return benchmark.runFromSupplier(supplier, runs);
    }

    public static void main(String[] args) throws IOException {
        StringBuilder fileContent = new StringBuilder();
        fileContent.append(new SelectBenchmark(100, 256, 256).runBenchmarks());
        fileContent.append(new SelectBenchmark(50, 512, 512).runBenchmarks());
//        fileContent.append(new SelectBenchmark(20, 1024, 1024).runBenchmarks());
//        fileContent.append(new SelectBenchmark(10, 2048, 2048).runBenchmarks());
//        fileContent.append(new SelectBenchmark(5, 4096, 4096).runBenchmarks());
//        fileContent.append(new SelectBenchmark(3, 8192, 8192).runBenchmarks());
//        fileContent.append(new SelectBenchmark(2, 16384, 16384).runBenchmarks());

        try {
            String currentDirPath = System.getProperty("user.dir");
            String OutputCsvFilename = "SelectBenchmark.csv";
            String path = Paths.get(currentDirPath, OutputCsvFilename).toString();
            System.out.println("Output CSV File Path :-> " + path);
            File file = new File(path);
            String header = "Method,Array-Ordering,Runs,N,Time\n";
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.append(header);
            bw.append(fileContent);

            bw.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private final int runs;
    private final int n;
    private final int safetyFactor;

}
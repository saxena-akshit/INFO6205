package edu.neu.coe.info6205.threesum;

import edu.neu.coe.info6205.util.Benchmark_Timer;
import edu.neu.coe.info6205.util.TimeLogger;
import edu.neu.coe.info6205.util.Utilities;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ThreeSumBenchmark {
    public ThreeSumBenchmark(int runs, int n, int m) {
        this.runs = runs;
        this.supplier = new Source(n, m).intsSupplier(10);
        this.n = n;
    }

    public void runBenchmarks() {
        System.out.println("ThreeSumBenchmark: N=" + n);
        benchmarkThreeSum("ThreeSumQuadratic", (xs) -> new ThreeSumQuadratic(xs).getTriples(), n, timeLoggersQuadratic);
        benchmarkThreeSum("ThreeSumQuadrithmic", (xs) -> new ThreeSumQuadrithmic(xs).getTriples(), n,
                timeLoggersQuadrithmic);
        benchmarkThreeSum("ThreeSumCubic", (xs) -> new ThreeSumCubic(xs).getTriples(), n, timeLoggersCubic);
    }

    public static void main(String[] args) {
        new ThreeSumBenchmark(100, 250, 250).runBenchmarks();
        new ThreeSumBenchmark(50, 500, 500).runBenchmarks();
        new ThreeSumBenchmark(20, 1000, 1000).runBenchmarks();
        new ThreeSumBenchmark(10, 2000, 2000).runBenchmarks();
        new ThreeSumBenchmark(5, 4000, 4000).runBenchmarks();
        new ThreeSumBenchmark(3, 8000, 8000).runBenchmarks();
        new ThreeSumBenchmark(3, 16000, 16000).runBenchmarks();
        new ThreeSumBenchmark(3, 32000, 32000).runBenchmarks();
        new ThreeSumBenchmark(3, 64000, 64000).runBenchmarks();
        new ThreeSumBenchmark(3, 128000, 128000).runBenchmarks();
        new ThreeSumBenchmark(3, 256000, 256000).runBenchmarks();
    }

    private void benchmarkThreeSum(final String description, final Consumer<int[]> function, int n,
            final TimeLogger[] timeLoggers) {
        if (description.equals("ThreeSumCubic") && n > 4000)
            return;

        System.out.println("Running " + description + " for n=" + n);

        // Create a Benchmark_Timer that times the execution of the ThreeSum function
        Benchmark_Timer<int[]> benchmarkTimer = new Benchmark_Timer<>(
                description,
                null,
                function,
                null);

        // Run the benchmark multiple times based on the "runs" field and record the
        // average time
        double averageTime = benchmarkTimer.runFromSupplier(supplier, runs);

        // Log the time results using the provided TimeLogger array
        for (TimeLogger timeLogger : timeLoggers) {
            timeLogger.log(description, averageTime, n);
        }
    }

    private final static TimeLogger[] timeLoggersCubic = {
            new TimeLogger("Raw time per run (mSec): ", null),
            new TimeLogger("Normalized time per run (n^3): ", n -> 1.0 / 6 * n * n * n)
    };
    private final static TimeLogger[] timeLoggersQuadrithmic = {
            new TimeLogger("Raw time per run (mSec): ", null),
            new TimeLogger("Normalized time per run (n^2 log n): ", n -> n * n * Utilities.lg(n))
    };
    private final static TimeLogger[] timeLoggersQuadratic = {
            new TimeLogger("Raw time per run (mSec): ", null),
            new TimeLogger("Normalized time per run (n^2): ", n -> 1.0 / 2 * n * n)
    };

    private final int runs;
    private final Supplier<int[]> supplier;
    private final int n;
}
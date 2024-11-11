package edu.neu.coe.info6205.sort.par;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) {
        // Array sizes to test
        int[] arraySizes = {1000000, 2000000, 4000000, 8000000};

        // Cutoff values to test
        int[] cutoffValues = {1000, 5000, 10000, 50000, 100000};

        // Number of runs for each configuration
        final int RUNS_PER_TEST = 5;

        try (FileWriter fw = new FileWriter("sorting_results.csv"); BufferedWriter bw = new BufferedWriter(fw)) {

            // Write CSV header
            bw.write("ArraySize,Cutoff,ThreadCount,RunNumber,ExecutionTime(ms)\n");

            // Test different array sizes
            for (int arraySize : arraySizes) {
                System.out.println("\nTesting array size: " + arraySize);

                // Test different cutoff values
                for (int cutoff : cutoffValues) {
                    ParSort.cutoff = cutoff;
                    System.out.println("\nCutoff value: " + cutoff);
                    System.out.println("Thread count: " + ForkJoinPool.getCommonPoolParallelism());

                    // Multiple runs for each configuration
                    for (int run = 1; run <= RUNS_PER_TEST; run++) {
                        // Generate random array
                        int[] array = new int[arraySize];
                        Random random = new Random();
                        for (int i = 0; i < array.length; i++) {
                            array[i] = random.nextInt(10000000);
                        }

                        // Time the sort
                        long startTime = System.currentTimeMillis();
                        ParSort.sort(array, 0, array.length);
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;

                        // Verify the sort worked correctly
                        if (!isSorted(array)) {
                            System.err.println("Array not sorted correctly!");
                            continue;
                        }

                        // Write results to CSV
                        String result = String.format("%d,%d,%d,%d,%d\n",
                                arraySize, cutoff, ForkJoinPool.getCommonPoolParallelism(),
                                run, duration);
                        bw.write(result);
                        bw.flush();

                        System.out.printf("Run %d: %dms%n", run, duration);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1] > array[i]) {
                return false;
            }
        }
        return true;
    }
}

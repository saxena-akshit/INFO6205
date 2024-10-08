package edu.neu.coe.info6205.sort.elementary;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

import edu.neu.coe.info6205.util.Benchmark_Timer;

public class InsertionSortBenchmark {

    // Array generators (same as before)
    public static Integer[] generateRandomArray(int size) {
        Random random = new Random();
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size; i++) {
            arr[i] = random.nextInt(size);
        }
        return arr;
    }

    public static Integer[] generateOrderedArray(int size) {
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i;
        }
        return arr;
    }

    public static Integer[] generatePartiallyOrderedArray(int size) {
        Integer[] arr = generateOrderedArray(size);
        Random random = new Random();
        for (int i = 0; i < size / 2; i++) {
            int index = random.nextInt(size);
            int temp = arr[i];
            arr[i] = arr[index];
            arr[index] = temp;
        }
        return arr;
    }

    public static Integer[] generateReverseOrderedArray(int size) {
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size; i++) {
            arr[i] = size - i;
        }
        return arr;
    }

    public static void main(String[] args) {
        // Create an instance of InsertionSortBasic
        InsertionSortBasic<Integer> insertionSort = InsertionSortBasic.create();

        // Define a Consumer that uses InsertionSortBasic's sort method
        Consumer<Integer[]> insertionSortConsumer = arr -> insertionSort.sort(arr);

        // Set up the benchmark timer using the InsertionSortBasic instance
        Benchmark_Timer<Integer[]> benchmark = new Benchmark_Timer<>(
                "Insertion Sort Benchmark",
                null,
                insertionSortConsumer,
                null
        );

        // number of runs
        int m = 50;

        int[] sizes = {500, 1000, 2000, 4000, 8000, 16000, 32000, 64000}; // Test for 5 values of n using the doubling method
        for (int size : sizes) {
            // Random array benchmark
            Integer[] randomArray = generateRandomArray(size);
            double randomTime = benchmark.runFromSupplier(() -> Arrays.copyOf(randomArray, randomArray.length), m);
            System.out.println("Size: " + size + " Random array sort time: " + randomTime + " ms");

            // Ordered array benchmark
            Integer[] orderedArray = generateOrderedArray(size);
            double orderedTime = benchmark.runFromSupplier(() -> Arrays.copyOf(orderedArray, orderedArray.length), m);
            System.out.println("Size: " + size + " Ordered array sort time: " + orderedTime + " ms");

            // Partially-ordered array benchmark
            Integer[] partiallyOrderedArray = generatePartiallyOrderedArray(size);
            double partiallyOrderedTime = benchmark.runFromSupplier(() -> Arrays.copyOf(partiallyOrderedArray, partiallyOrderedArray.length), m);
            System.out.println("Size: " + size + " Partially-ordered array sort time: " + partiallyOrderedTime + " ms");

            // Reverse-ordered array benchmark
            Integer[] reverseOrderedArray = generateReverseOrderedArray(size);
            double reverseOrderedTime = benchmark.runFromSupplier(() -> Arrays.copyOf(reverseOrderedArray, reverseOrderedArray.length), m);
            System.out.println("Size: " + size + " Reverse-ordered array sort time: " + reverseOrderedTime + " ms");

            System.out.println();
        }
    }
}

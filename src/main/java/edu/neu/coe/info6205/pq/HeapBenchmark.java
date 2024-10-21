package edu.neu.coe.info6205.pq;

import java.util.Random;

import edu.neu.coe.info6205.util.Benchmark_Timer;

public class HeapBenchmark {

    private static final Random random = new Random();
    private static final int RUNS = 10;
    private static final int MAX_HEAP_SIZE = 4095;
    private static final int TOTAL_INSERTIONS = 16000;
    private static final int TOTAL_REMOVALS = 4000;

    public static void main(String[] args) {
        String[] heapTypes = {
            "4-ary Heap",
            "Basic Binary Heap",
            "Basic Binary Heap (Floyd's trick)",
            "4-ary Heap Floyd's trick)"
        };

        for (String heapType : heapTypes) {
            benchmarkHeap(heapType);
        }
    }

    private static void benchmarkHeap(String description) {
        int arity = description.contains("4-ary") ? 4 : 2;
        boolean floyd = description.contains("Floyd's trick");

        double totalInsertTime = 0;
        double totalRemoveTime = 0;
        int highestSpilledPriority = Integer.MIN_VALUE;

        // Warm-up run
        runBenchmark(arity, floyd);

        // Actual runs
        for (int run = 0; run < RUNS; run++) {
            double[] results = runBenchmark(arity, floyd);
            totalInsertTime += results[0];
            totalRemoveTime += results[1];
            highestSpilledPriority = Math.max(highestSpilledPriority, (int) results[2]);
        }

        double avgInsertTime = totalInsertTime / RUNS;
        double avgRemoveTime = totalRemoveTime / RUNS;

        System.out.println("Heap Type: " + description);
        System.out.println("Average Insert Time: " + avgInsertTime + " seconds");
        System.out.println("Average Remove Time: " + avgRemoveTime + " seconds");
        System.out.println("Highest Spilled Priority: " + highestSpilledPriority);
        System.out.println();
    }

    private static double[] runBenchmark(int arity, boolean floyd) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(MAX_HEAP_SIZE, true, Integer::compare, floyd, arity);
        java.util.PriorityQueue<Integer> spilledElements = new java.util.PriorityQueue<>((a, b) -> b.compareTo(a));

        Benchmark_Timer<Integer> insertTimer = new Benchmark_Timer<>(
                "Insert",
                (ignore) -> {
                },
                (ignore) -> {
                    int value = random.nextInt();
                    if (pq.size() >= MAX_HEAP_SIZE) {
                        spilledElements.offer(value);
                    } else {
                        pq.give(value);
                    }
                }
        );

        Benchmark_Timer<Integer> removeTimer = new Benchmark_Timer<>(
                "Remove",
                (ignore) -> {
                },
                (ignore) -> {
                    if (!pq.isEmpty()) {
                        try {
                            pq.take();
                        } catch (PQException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        double insertTime = insertTimer.runFromSupplier(() -> TOTAL_INSERTIONS, TOTAL_INSERTIONS);
        double removeTime = removeTimer.runFromSupplier(() -> TOTAL_REMOVALS, TOTAL_REMOVALS);

        int highestSpilledPriority = spilledElements.isEmpty() ? Integer.MIN_VALUE : spilledElements.peek();

        return new double[]{insertTime, removeTime, highestSpilledPriority};
    }
}

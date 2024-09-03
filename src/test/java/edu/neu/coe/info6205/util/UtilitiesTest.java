package edu.neu.coe.info6205.util;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class UtilitiesTest {

    @Test
    public void fillRandomArray1() {
        int n = 100;
        int max = 10;
        Integer[] integers = Utilities.fillRandomArray(Integer.class, new Random(0), n, r -> r.nextInt(max));
        int[] count = new int[max];
        for (Integer x : integers) count[x]++;
        int sum = 0;
        for (int x : count) sum += x;
        assertEquals(8, count[0]);
        assertEquals(12, count[2]);
        assertEquals(17, count[7]);
        assertEquals(5, count[9]);
        assertEquals(n, sum);
    }

    @Test
    public void fillRandomArray2() {
        int n = 1000;
        int max = 100;
        int seed = 1;
        Integer[] integers = Utilities.fillRandomArray(Integer.class, new Random(seed), n, r -> r.nextInt(max));
        int[] count = new int[max];
        for (Integer x : integers) count[x]++;
        int sum = 0;
        for (int x : count) sum += x;
        assertEquals(n, sum);
    }
}
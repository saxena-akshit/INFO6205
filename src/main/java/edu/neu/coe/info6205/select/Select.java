package edu.neu.coe.info6205.select;

public interface Select<X extends Comparable<X>> {
    X select(X[] a, int k);
}
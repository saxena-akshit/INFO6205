package edu.neu.coe.info6205.sort;

import edu.neu.coe.info6205.util.Config;

/**
 * Base class for Sort with a Helper.
 * <p>
 * CONSIDER extending GenericSortWithHelper
 *
 * @param <X> underlying type which extends Comparable.
 */
public abstract class SortWithComparableHelper<X extends Comparable<X>> extends SortWithHelper<X> {

    public SortWithComparableHelper(Helper<X> helper) {
        super(helper);
    }

    public SortWithComparableHelper(String description, int N, int nRuns, Config config) {
        this(HelperFactory.create(description, N, config.getSeed(), nRuns, config));
        closeHelper = true;
    }
}
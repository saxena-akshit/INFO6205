package edu.neu.coe.info6205.sort.counting;

import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.HelperFactory;
import edu.neu.coe.info6205.sort.SortException;
import edu.neu.coe.info6205.sort.classic.ClassificationSorter;
import edu.neu.coe.info6205.util.CodePointMapper;
import edu.neu.coe.info6205.util.Config;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Class LSDStringSort.
 *
 * @author Darshan Ashwin Dedhia (original code)
 * @author rhillyard adapted to extend SortWithHelper.
 */
public class LSDStringSort extends ClassificationSorter<String, Integer> {

    public final static String DESCRIPTION = "LSD String Sort";

    /**
     * This Comparator is required to be consistent with the logic of LSDStringSort (and is used by the helper to check sorted, etc.)
     */
    public final static Comparator<String> comparatorASCII = CodePointMapper.ASCIIComparator;

    /**
     * Perform pre-processing step for this Sort.
     * Forces all strings to be in lower case.
     * <p>
     * CONSIDER moving this to a sub-class.
     *
     * @param xs the elements to be pre-processed.
     */
    @Override
    public String[] preProcess(String[] xs) {
        String[] strings = super.preProcess(xs);
        for (int i = 0; i < strings.length; i++) strings[i] = strings[i].toLowerCase();
        return strings;
    }

    /**
     * Primary constructor.
     *
     * @param helper a String helper.
     * @param w      the max length of each string (if 0, then we will calculate it).
     */
    public LSDStringSort(Helper<String> helper, int w) {
        super(helper, LSDStringSort::charAsciiVal);
        this.w = w;
    }

    /**
     * Secondary constructor.
     *
     * @param N          the expected number of elements.
     * @param w          the max length of each string (if 0, then we will calculate it).
     * @param comparator the comparator for Strings.
     * @param nRuns      the expected number of runs to be made.
     * @param config     the configuration.
     */
    public LSDStringSort(int N, int w, Comparator<String> comparator, int nRuns, Config config) {
        this(HelperFactory.createGeneric(DESCRIPTION + (w > 0 ? " " + w : ""), comparator, N, nRuns, config), w);
        if (w > 0) getLogger().info("LSD string sort with fixed length: " + w);
        closeHelper = true;
    }

    private final int w;

    /**
     * charAsciiVal method returns ASCII value of particular character in a String.
     *
     * @param str          String input for which ASCII Value need to be found
     * @param charPosition Character position (zero-based) of which ASCII value needs to be found. If character
     *                     doesn't exist then ASCII value of null i.e. 0 is returned
     * @return int Returns ASCII value
     */
    private static int charAsciiVal(String str, int charPosition) {
        if (charPosition < 0 || charPosition >= str.length()) return 0;
        char x = str.charAt(charPosition);
        return x & 0x7F; // CONSIDER using MSDStringSort mapper.
    }

    /**
     * charSort method is implementation of LSD sort algorithm at particular character.
     *
     * @param xs           It contains an array of String on which LSD char sort needs to be performed
     * @param charPosition This is the character position on which sort would be performed
     * @param from         This is the starting index from which sorting operation will begin
     * @param to           This is the first index to be ignored.
     */
    private void charSort(String[] xs, int charPosition, int from, int to) {
        int ASCII_RANGE = 128;
        int[] count = new int[ASCII_RANGE + 1];

        for (int i = from; i < to; i++) {
            String x = helper.get(xs, i);
            int c = classify(x, charPosition);
            helper.incrementHits(1); // for the count.
            if (c >= 0 && c <= count.length) count[c + 1]++;
            else throw new SortException(DESCRIPTION + ": count index " + c +
                    " is out of range: 0 thru " + count.length);
        }

        // transform counts to indices
        int countR = count[0];
        for (int r = 1; r < ASCII_RANGE + 1; r++) {
            helper.incrementHits(1); // for the count.
            count[r] += countR;
            countR = count[r];
        }

        // distribute: essentially a copy block with extra function
        // XXX It's rather surprising that this function works (since it involves updating count) but it does!
        String[] result = new String[xs.length];
        helper.distributeBlock(xs, from, to, result, x -> count[classify(x, charPosition)]++);

        // copy back
        helper.copyBlock(result, 0, xs, from, to - from);
    }

    /**
     * sort method is implementation of LSD String sort algorithm.
     *
     * @param xs   It contains an array of String on which LSD sort needs to be performed
     * @param from This is the starting index from which sorting operation will begin
     * @param to   This is the first index to be ignored.
     */
    public void sort(String[] xs, int from, int to) {
        // XXX first, we try to ensure that all elements of xs have only ASCII characters.
        // NOTE for now, at least, we do not increment hits for this operation.
        Stream<String> stream = Arrays.stream(xs, from, to).map(CodePointMapper.ASCII::map);
        String[] ys = stream.toArray(String[]::new);
        int n = ys.length;
        if (n != xs.length) System.err.println("LSD String Sort: lost strings");
        // CONSIDER using a CodeMapper here.
        int i = from;
        for (String y : ys) xs[i++] = y;
        for (; i < to; i++)
            xs[i] = "";

        // XXX now, we find the longest string
        // NOTE for now, at least, we do not increment hits for this operation.
        int maxLength = w > 0 ? w : findMaxLength(xs);

        // XXX finally, run charSort.
        for (int d = maxLength; d > 0; d--)
            charSort(xs, d - 1, 0, n);
    }

    /**
     * findMaxLength method returns the maximum length of all available strings in an array (not just from->to).
     *
     * @param xs It contains an array of String from which maximum length needs to be found
     * @return int Returns maximum length value
     */
    private int findMaxLength(String[] xs) {
        int maxLength = Integer.MIN_VALUE;
        helper.incrementHits(xs.length);
        for (String str : xs)
            maxLength = Math.max(maxLength, str.length());
        return maxLength;
    }
}
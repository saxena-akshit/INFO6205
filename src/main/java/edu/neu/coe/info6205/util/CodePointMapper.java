package edu.neu.coe.info6205.util;

import java.util.Comparator;
import java.util.function.UnaryOperator;

/**
 * Class to manage code point mapping.
 * This implementation deals with three components: the mapper, the range, and the comparator.
 * See the constructor for explanations.
 * <p>
 * What is a codepoint? It's an integer (could be 8, 16, or more bits) that maps to a (Unicode) character.
 * See <a href="https://en.wikipedia.org/wiki/Code_point">Wikipedia: Codepoint</a>.
 */
public class CodePointMapper implements UnaryOperator<Integer>, Comparator<String> {

    @Override
    public String toString() {
        return name;
    }

    static final UnaryOperator<Integer> EnglishMapper = x -> {
        if (x < 256 && Character.isLetter(x)) return x & 0x1F;
        else return 0;
    };

    static final Comparator<String> EnglishComparator = (o1, o2) -> {
        for (int i = 0; i <= o1.length() && i <= o2.length(); i++) {
            int char1 = (i < o1.length()) ? o1.charAt(i) : 0;
            int char2 = (i < o2.length()) ? o2.charAt(i) : 0;
            int cf = EnglishMapper.apply(char1) - EnglishMapper.apply(char2);
            if (cf != 0) return cf;
        }
        return 0;
    };

    /**
     * CodePointMapper to yield a value in the range 0 -> 31 which is good for English characters.
     */
    public final static CodePointMapper English = new CodePointMapper("English", EnglishMapper, 32, EnglishComparator);

    static final UnaryOperator<Integer> ASCIIMapperExt = x -> x & 0xFF;
    public static final Comparator<String> ASCIIComparatorExt = (o1, o2) -> {
        int l1 = o1.length();
        int l2 = o2.length();
        for (int i = 0; i <= l1 && i <= l2; i++) {
            int char1 = (i < l1) ? o1.charAt(i) : 0;
            int char2 = (i < l2) ? o2.charAt(i) : 0;
            int cf = ASCIIMapperExt.apply(char1) - ASCIIMapperExt.apply(char2);
            if (cf != 0) return cf;
        }
        return 0;
    };

    /**
     * CodePointMapper to yield a value in the range 0 -> 255 which is good for (8-bit) ASCII characters.
     */
    public final static CodePointMapper ASCIIExt = new CodePointMapper("ASCII (Ext)", ASCIIMapperExt, 256, ASCIIComparatorExt);

    static final UnaryOperator<Integer> ASCIIMapper = x -> x & 0x7F;
    public static final Comparator<String> ASCIIComparator = (o1, o2) -> {
        int l1 = o1.length();
        int l2 = o2.length();
        for (int i = 0; i <= l1 && i <= l2; i++) {
            int char1 = (i < l1) ? o1.charAt(i) : 0;
            int char2 = (i < l2) ? o2.charAt(i) : 0;
            int cf = ASCIIMapper.apply(char1) - ASCIIMapper.apply(char2);
            if (cf != 0) return cf;
        }
        return 0;
    };

    /**
     * CodePointMapper to yield a value in the range 0 -> 255 which is good for (8-bit) ASCII characters.
     */
    public final static CodePointMapper ASCII = new CodePointMapper("ASCII", ASCIIMapper, 128, ASCIIComparator);

    /**
     * Constructor.
     *
     * @param name       the name of this mapper.
     * @param mapper     a function which takes a codePoint and returns a valid character within the defined <code>range</code>.
     * @param range      an int which specifies the number of legal values that can be output by the <code>mapper</code>.
     * @param comparator a String comparator.
     */
    public CodePointMapper(String name, UnaryOperator<Integer> mapper, int range, Comparator<String> comparator) {
        this.name = name;
        this.mapper = mapper;
        this.range = range;
        this.comparator = comparator;
    }

    /**
     * Method to take a (Unicode) code point and yield an int in the appropriate range.
     *
     * @param codePoint a Unicode codePoint.
     * @return an int which is non-negative and less than the value of <code>range</code>.
     */
    public int map(int codePoint) {
        int result = mapper.apply(codePoint);
        if (inRange(result)) return result;
        else throw new RuntimeException("CodePointMapper " + this + ": " + "result out of range: " + result);
    }

    public String map(String s) {
        StringBuilder sb = new StringBuilder();
        for (char x : s.toCharArray()) sb.append((char) map(x));
        return sb.toString();
    }

    /**
     * Method to determine if the value <code>x</code> is within legal range, i.e., according to <code>range</code>.
     *
     * @param x the value.
     * @return true if it is in range.
     */
    boolean inRange(int x) {
        return x >= 0 && x < range;
    }

    private final String name;
    public final UnaryOperator<Integer> mapper;
    public final int range;
    public final Comparator<String> comparator;

    /**
     * Applies this function to the given argument.
     *
     * @param x the function argument
     * @return the function result
     */
    public Integer apply(Integer x) {
        return map(x);
    }

    public int compare(String o1, String o2) {
        return 0;
    }
}
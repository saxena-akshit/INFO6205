package edu.neu.coe.info6205.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Function;

/**
 * Class to handle logging of times, both raw and normalized.
 */
public class TimeLogger {

    /**
     * Method to log the time (in mSecs).
     * If minimumComparisons is null, we just log the raw time.
     * Otherwise, we log the normalized time based on minimumComparisons.
     *
     * @param description the description of the task being timed.
     * @param time        the raw time.
     * @param N           the size of the problem.
     */
    public void log(String description, double time, int N) {
        double t = minimumComparisons == null ? time : time / minimumComparisons.apply(N) * 1e6;
        logger.info(description + ": " + prefix + " " + formatTime(t));
    }

    public TimeLogger(String prefix, Function<Integer, Double> minimumComparisons) {
        this.prefix = prefix;
        this.minimumComparisons = minimumComparisons;
    }

    private final String prefix;
    private final Function<Integer, Double> minimumComparisons;

    private static String formatTime(double time) {
        decimalFormat.applyPattern(timePattern);
        return decimalFormat.format(time);
    }

    final static LazyLogger logger = new LazyLogger(TimeLogger.class);

    private static final Locale locale = new Locale("en", "US");
    private static final String timePattern = "######.0000";
    private static final DecimalFormat decimalFormat = (DecimalFormat)
            NumberFormat.getNumberInstance(locale);

}
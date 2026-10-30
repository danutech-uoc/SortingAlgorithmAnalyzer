package com.sortingapp.performance;

import java.util.Locale;

/**
 * Turns a duration in nanoseconds into readable text such as "845 ns", "12.40 µs",
 * "3.25 ms" or "1.502 s".
 *
 * @author Member 2 - Sorting Algorithms &amp; Performance Measurement
 */
public final class TimeFormatter {

    private TimeFormatter() {
        // Utility class - not meant to be instantiated
    }

    /**
     * @param nanoseconds a duration in nanoseconds
     * @return the duration with a suitable unit
     */
    public static String format(long nanoseconds) {
        if (nanoseconds < 1_000L) {
            return nanoseconds + " ns";
        }
        if (nanoseconds < 1_000_000L) {
            return String.format(Locale.US, "%.2f µs", nanoseconds / 1e3);
        }
        if (nanoseconds < 1_000_000_000L) {
            return String.format(Locale.US, "%.2f ms", nanoseconds / 1e6);
        }
        return String.format(Locale.US, "%.3f s", nanoseconds / 1e9);
    }
}

package com.sortingapp.performance;

/**
 * Timing results of one sorting algorithm.
 *
 * @author Member 2 - Sorting Algorithms &amp; Performance Measurement
 */
public final class AlgorithmResult {

    private final String algorithmName;
    private final String timeComplexity;
    private final long[] runTimes;
    private final boolean sortedCorrectly;

    AlgorithmResult(String algorithmName, String timeComplexity, long[] runTimes, boolean sortedCorrectly) {
        this.algorithmName = algorithmName;
        this.timeComplexity = timeComplexity;
        this.runTimes = runTimes.clone();
        this.sortedCorrectly = sortedCorrectly;
    }

    /**
     * @return the algorithm's display name
     */
    public String getAlgorithmName() {
        return algorithmName;
    }

    /**
     * @return the time complexity as "best / average / worst"
     */
    public String getTimeComplexity() {
        return timeComplexity;
    }

    /**
     * @return the execution time of every run, in nanoseconds
     */
    public long[] getRunTimes() {
        return runTimes.clone();
    }

    /**
     * @return the number of timed runs
     */
    public int getRunCount() {
        return runTimes.length;
    }

    /**
     * @return the mean execution time over all runs, in nanoseconds
     */
    public long getAverageNanos() {
        long total = 0;
        for (long time : runTimes) {
            total += time;
        }
        return total / runTimes.length;
    }

    /**
     * @return the shortest execution time of all runs, in nanoseconds
     */
    public long getBestNanos() {
        long best = Long.MAX_VALUE;
        for (long time : runTimes) {
            best = Math.min(best, time);
        }
        return best;
    }

    /**
     * @return true if every run produced exactly the same output as the reference sort
     */
    public boolean isSortedCorrectly() {
        return sortedCorrectly;
    }
}

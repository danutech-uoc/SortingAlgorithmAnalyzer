package com.sortingapp.performance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The outcome of one analysis: results of every algorithm, the fastest one,
 * and the sorted data.
 *
 * @author Member 2 - Sorting Algorithms &amp; Performance Measurement
 */
public final class AnalysisReport {

    private final List<AlgorithmResult> results;
    private final double[] sortedData;

    AnalysisReport(List<AlgorithmResult> results, double[] sortedData) {
        this.results = Collections.unmodifiableList(new ArrayList<>(results));
        this.sortedData = sortedData;
    }

    /**
     * @return the results in the order the algorithms were run
     */
    public List<AlgorithmResult> getResults() {
        return results;
    }

    /**
     * The best-performing algorithm is the correctly sorting one with the shortest
     * average execution time. If two are equal, the one that ran first wins.
     *
     * @return the fastest algorithm's result
     */
    public AlgorithmResult getFastest() {
        AlgorithmResult fastest = null;
        for (AlgorithmResult result : results) {
            if (!result.isSortedCorrectly()) {
                continue;
            }
            if (fastest == null || result.getAverageNanos() < fastest.getAverageNanos()) {
                fastest = result;
            }
        }
        return fastest;
    }

    /**
     * @param result one of this report's results
     * @return its position when ordered by average time (1 = fastest)
     */
    public int getRank(AlgorithmResult result) {
        int rank = 1;
        for (AlgorithmResult other : results) {
            if (other.getAverageNanos() < result.getAverageNanos()) {
                rank++;
            }
        }
        return rank;
    }

    /**
     * @return true if every algorithm sorted the data correctly
     */
    public boolean allSortedCorrectly() {
        for (AlgorithmResult result : results) {
            if (!result.isSortedCorrectly()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return a copy of the data in ascending order
     */
    public double[] getSortedData() {
        return sortedData.clone();
    }

    /**
     * @return how many values were sorted
     */
    public int getDataSize() {
        return sortedData.length;
    }
}

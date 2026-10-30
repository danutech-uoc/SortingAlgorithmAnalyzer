package com.sortingapp.performance;

import com.sortingapp.algorithms.HeapSort;
import com.sortingapp.algorithms.InsertionSort;
import com.sortingapp.algorithms.MergeSort;
import com.sortingapp.algorithms.QuickSort;
import com.sortingapp.algorithms.ShellSort;
import com.sortingapp.algorithms.SortingAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Runs every sorting algorithm on the same data, measures how long each one takes,
 * and checks that each one sorted the data correctly.
 * <p>
 * How the measurement is kept fair:
 * <ul>
 *   <li>Every run sorts a <b>fresh copy</b> of the original, unsorted data. Otherwise the
 *       second algorithm would receive data that is already sorted.</li>
 *   <li>Only the {@code sort()} call is timed; copying the array happens outside the timer.</li>
 *   <li>Time is measured with {@link System#nanoTime()}, which is meant for measuring
 *       elapsed time with nanosecond precision. {@code System.currentTimeMillis()} only has
 *       millisecond precision and can jump if the computer clock is adjusted.</li>
 *   <li>Each algorithm is <b>warmed up</b> on a small sample first, so the Java JIT compiler
 *       has optimised its code before timing starts.</li>
 *   <li>Each algorithm can be run several times and the average is reported, which smooths
 *       out noise from other programs running on the computer.</li>
 * </ul>
 *
 * @author Member 2 - Sorting Algorithms &amp; Performance Measurement
 */
public class PerformanceAnalyzer {

    /** Receives progress updates while an analysis is running. */
    public interface ProgressListener {
        /**
         * @param completedRuns number of timed runs finished so far
         * @param totalRuns     number of timed runs in the whole analysis
         * @param message       description of what is happening now
         */
        void onProgress(int completedRuns, int totalRuns, String message);
    }

    private static final int WARM_UP_SIZE = 2_000;
    private static final int WARM_UP_ROUNDS = 10;

    private final List<SortingAlgorithm> algorithms;

    /**
     * Creates an analyzer for the five required algorithms.
     */
    public PerformanceAnalyzer() {
        this(Arrays.asList(
                new InsertionSort(),
                new ShellSort(),
                new MergeSort(),
                new QuickSort(),
                new HeapSort()));
    }

    /**
     * @param algorithms the algorithms to compare
     */
    public PerformanceAnalyzer(List<SortingAlgorithm> algorithms) {
        this.algorithms = Collections.unmodifiableList(new ArrayList<>(algorithms));
    }

    /**
     * @return the algorithms that are compared
     */
    public List<SortingAlgorithm> getAlgorithms() {
        return algorithms;
    }

    /**
     * Times every algorithm on the given data.
     *
     * @param data     the values to sort; this array is not modified
     * @param runs     how many timed runs per algorithm (at least 1)
     * @param listener receives progress updates; may be null
     * @return the timing results and the sorted data
     * @throws IllegalArgumentException if data is empty or runs is less than 1
     */
    public AnalysisReport analyze(double[] data, int runs, ProgressListener listener) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("There is no data to sort.");
        }
        if (runs < 1) {
            throw new IllegalArgumentException("The number of runs must be at least 1.");
        }

        // Reference result from Java's built-in sort, used to verify every algorithm
        double[] expected = data.clone();
        Arrays.sort(expected);

        int totalRuns = algorithms.size() * runs;
        int completedRuns = 0;
        List<AlgorithmResult> results = new ArrayList<>();

        for (SortingAlgorithm algorithm : algorithms) {
            report(listener, completedRuns, totalRuns, "Warming up " + algorithm.getName() + "...");
            warmUp(algorithm, data);

            long[] times = new long[runs];
            boolean sortedCorrectly = true;

            for (int run = 0; run < runs; run++) {
                report(listener, completedRuns, totalRuns,
                        String.format("Running %s (run %d of %d)...", algorithm.getName(), run + 1, runs));

                double[] copy = data.clone(); // fresh unsorted copy for every run

                long start = System.nanoTime();
                algorithm.sort(copy);
                times[run] = System.nanoTime() - start;

                sortedCorrectly &= Arrays.equals(copy, expected);
                completedRuns++;
            }
            results.add(new AlgorithmResult(algorithm.getName(), algorithm.getTimeComplexity(), times, sortedCorrectly));
        }

        report(listener, totalRuns, totalRuns, "Analysis complete.");
        return new AnalysisReport(results, expected);
    }

    /** Sorts a few small copies so the JIT compiler optimises the algorithm before it is timed. */
    private void warmUp(SortingAlgorithm algorithm, double[] data) {
        int size = Math.min(data.length, WARM_UP_SIZE);
        for (int round = 0; round < WARM_UP_ROUNDS; round++) {
            algorithm.sort(Arrays.copyOf(data, size));
        }
    }

    private static void report(ProgressListener listener, int completed, int total, String message) {
        if (listener != null) {
            listener.onProgress(completed, total, message);
        }
    }
}

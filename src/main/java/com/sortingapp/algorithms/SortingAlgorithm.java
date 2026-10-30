package com.sortingapp.algorithms;

/**
 * Common contract for all sorting algorithms.
 * <p>
 * Because every algorithm implements this interface, the performance analyzer can
 * run and time all of them with the same code, without knowing which one it is using.
 *
 * @author Member 2 - Sorting Algorithms &amp; Performance Measurement
 */
public interface SortingAlgorithm {

    /**
     * Sorts the array in ascending order, in place.
     *
     * @param data the values to sort; the array itself is rearranged
     */
    void sort(double[] data);

    /**
     * @return the display name of the algorithm, e.g. "Quick Sort"
     */
    String getName();

    /**
     * @return the time complexity as "best / average / worst", e.g. "O(n log n) / O(n log n) / O(n log n)"
     */
    String getTimeComplexity();
}

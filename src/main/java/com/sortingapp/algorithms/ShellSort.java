package com.sortingapp.algorithms;

/**
 * Shell Sort.
 * <p>
 * An improved Insertion Sort. Instead of only comparing neighbours, it first sorts
 * elements that are far apart ("gap" positions apart), so values can move long
 * distances in a single step. The gap shrinks each round; the final round uses a
 * gap of 1, which is a normal Insertion Sort on an almost-sorted array.
 * <p>
 * Uses Knuth's gap sequence 1, 4, 13, 40, 121, ... (gap = 3 * gap + 1), which gives
 * a worst case of O(n^1.5) - much better than halving the gap each time.
 *
 * @author Member 2 - Sorting Algorithms &amp; Performance Measurement
 */
public class ShellSort implements SortingAlgorithm {

    @Override
    public void sort(double[] data) {
        int n = data.length;

        // Find the largest gap in Knuth's sequence that is smaller than n / 3
        int gap = 1;
        while (gap < n / 3) {
            gap = 3 * gap + 1;
        }

        while (gap >= 1) {
            // Gapped insertion sort: each element is compared with the one 'gap' positions before it
            for (int i = gap; i < n; i++) {
                double temp = data[i];
                int j = i;
                while (j >= gap && data[j - gap] > temp) {
                    data[j] = data[j - gap];
                    j -= gap;
                }
                data[j] = temp;
            }
            gap /= 3; // move to the previous gap in the sequence: ..., 40, 13, 4, 1
        }
    }

    @Override
    public String getName() {
        return "Shell Sort";
    }

    @Override
    public String getTimeComplexity() {
        return "O(n log n) / ~O(n^1.25) / O(n^1.5)";
    }
}

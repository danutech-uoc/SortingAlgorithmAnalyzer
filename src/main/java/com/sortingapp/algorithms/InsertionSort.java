package com.sortingapp.algorithms;

/**
 * Insertion Sort.
 * <p>
 * Builds the sorted result one element at a time: the left part of the array is always
 * sorted, and each new element is shifted left until it reaches its correct place,
 * like sorting playing cards in your hand.
 * <p>
 * Very fast on small or nearly sorted data (O(n) when already sorted), but O(n&sup2;)
 * on random data because each element may have to move past all elements before it.
 *
 * @author Member 2 - Sorting Algorithms &amp; Performance Measurement
 */
public class InsertionSort implements SortingAlgorithm {

    @Override
    public void sort(double[] data) {
        for (int i = 1; i < data.length; i++) {
            double key = data[i];
            int j = i - 1;
            // Shift every larger element of the sorted part one position to the right
            while (j >= 0 && data[j] > key) {
                data[j + 1] = data[j];
                j--;
            }
            // Drop the key into the gap that was opened
            data[j + 1] = key;
        }
    }

    @Override
    public String getName() {
        return "Insertion Sort";
    }

    @Override
    public String getTimeComplexity() {
        return "O(n) / O(n²) / O(n²)";
    }
}

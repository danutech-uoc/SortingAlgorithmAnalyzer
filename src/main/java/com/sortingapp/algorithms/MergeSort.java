package com.sortingapp.algorithms;

/**
 * Merge Sort.
 * <p>
 * Divide and conquer: split the array into two halves, sort each half recursively,
 * then merge the two sorted halves into one sorted array.
 * <p>
 * Always O(n log n): the array is halved log n times, and each level of merging
 * touches all n elements. It needs O(n) extra memory for merging. It is stable
 * (equal values keep their original order).
 *
 * @author Member 2 - Sorting Algorithms &amp; Performance Measurement
 */
public class MergeSort implements SortingAlgorithm {

    @Override
    public void sort(double[] data) {
        if (data.length < 2) {
            return;
        }
        // One buffer is allocated once and reused by every merge, instead of a new array per merge
        double[] buffer = new double[data.length];
        mergeSort(data, buffer, 0, data.length - 1);
    }

    private void mergeSort(double[] data, double[] buffer, int left, int right) {
        if (left >= right) {
            return; // zero or one element is already sorted
        }
        int mid = left + (right - left) / 2; // avoids int overflow of (left + right) / 2
        mergeSort(data, buffer, left, mid);
        mergeSort(data, buffer, mid + 1, right);
        merge(data, buffer, left, mid, right);
    }

    /**
     * Merges the sorted ranges data[left..mid] and data[mid+1..right].
     */
    private void merge(double[] data, double[] buffer, int left, int mid, int right) {
        System.arraycopy(data, left, buffer, left, right - left + 1);

        int i = left;      // next element of the left half
        int j = mid + 1;   // next element of the right half
        int k = left;      // next position to write in data

        // Repeatedly take the smaller front element; '<=' keeps the sort stable
        while (i <= mid && j <= right) {
            data[k++] = buffer[i] <= buffer[j] ? buffer[i++] : buffer[j++];
        }
        // Copy what is left of the left half
        while (i <= mid) {
            data[k++] = buffer[i++];
        }
        // Anything left in the right half is already in its correct place
    }

    @Override
    public String getName() {
        return "Merge Sort";
    }

    @Override
    public String getTimeComplexity() {
        return "O(n log n) / O(n log n) / O(n log n)";
    }
}

package com.sortingapp.algorithms;

/**
 * Quick Sort.
 * <p>
 * Divide and conquer: pick a "pivot" value, rearrange the array so smaller values are
 * on its left and larger values on its right, then sort both sides recursively.
 * <p>
 * Three improvements over the textbook version make it reliable on real data:
 * <ul>
 *   <li><b>Median-of-three pivot</b> - the first, middle and last values are put in order
 *       and the middle one becomes the pivot. Always picking the last value makes
 *       already-sorted data (e.g. a timestamp column) take O(n&sup2;).</li>
 *   <li><b>Hoare partitioning</b> - two indexes move towards each other from both ends and
 *       swap pairs that are on the wrong side. It does very few swaps, needs almost none
 *       on sorted data, and splits runs of equal values evenly, so columns with many
 *       repeated values (ages, ratings, ...) stay O(n log n).</li>
 *   <li><b>Smaller side first</b> - the smaller part is sorted recursively and the larger
 *       part in a loop, so the recursion depth stays below log2(n) and large inputs
 *       cannot cause a StackOverflowError.</li>
 * </ul>
 *
 * @author Member 2 - Sorting Algorithms &amp; Performance Measurement
 */
public class QuickSort implements SortingAlgorithm {

    @Override
    public void sort(double[] data) {
        quickSort(data, 0, data.length - 1);
    }

    private void quickSort(double[] data, int low, int high) {
        while (low < high) {
            int split = partition(data, low, high);

            // data[low..split] <= pivot <= data[split+1..high]
            // Recurse into the smaller part, loop on the larger part
            if (split - low < high - split) {
                quickSort(data, low, split);
                low = split + 1;
            } else {
                quickSort(data, split + 1, high);
                high = split;
            }
        }
    }

    /**
     * Hoare partition around the median of the first, middle and last values.
     *
     * @return index split such that data[low..split] &lt;= pivot &lt;= data[split+1..high]
     */
    private int partition(double[] data, int low, int high) {
        int mid = low + (high - low) / 2;

        // Median of three: order data[low] <= data[mid] <= data[high]
        if (data[mid] < data[low]) {
            swap(data, mid, low);
        }
        if (data[high] < data[low]) {
            swap(data, high, low);
        }
        if (data[high] < data[mid]) {
            swap(data, high, mid);
        }
        double pivot = data[mid];

        int i = low - 1;
        int j = high + 1;
        while (true) {
            // Move i right until it finds a value that belongs on the right side
            do {
                i++;
            } while (data[i] < pivot);
            // Move j left until it finds a value that belongs on the left side
            do {
                j--;
            } while (data[j] > pivot);

            if (i >= j) {
                return j; // the indexes have crossed: partitioning is finished
            }
            swap(data, i, j);
        }
    }

    private static void swap(double[] data, int i, int j) {
        double temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    @Override
    public String getName() {
        return "Quick Sort";
    }

    @Override
    public String getTimeComplexity() {
        return "O(n log n) / O(n log n) / O(n²)";
    }
}

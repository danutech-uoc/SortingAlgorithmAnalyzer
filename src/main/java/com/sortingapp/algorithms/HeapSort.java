package com.sortingapp.algorithms;

/**
 * Heap Sort.
 * <p>
 * Treats the array as a binary tree where the children of index i are at 2i+1 and 2i+2.
 * <ol>
 *   <li>Build a <b>max-heap</b>: every parent is larger than its children, so the
 *       largest value is at index 0.</li>
 *   <li>Repeatedly swap the largest value (index 0) to the end of the array, shrink the
 *       heap by one, and "sift down" the new root to restore the heap.</li>
 * </ol>
 * Always O(n log n) and needs no extra memory, but it jumps around the array a lot,
 * so in practice it is usually slower than Quick Sort and Merge Sort.
 *
 * @author Member 2 - Sorting Algorithms &amp; Performance Measurement
 */
public class HeapSort implements SortingAlgorithm {

    @Override
    public void sort(double[] data) {
        int n = data.length;

        // Step 1: build a max-heap, starting from the last parent node and moving up to the root
        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDown(data, i, n);
        }

        // Step 2: move the current maximum to the end, then repair the smaller heap
        for (int end = n - 1; end > 0; end--) {
            swap(data, 0, end);
            siftDown(data, 0, end);
        }
    }

    /**
     * Moves data[root] down the tree until both of its children are smaller.
     *
     * @param size number of elements that belong to the heap
     */
    private void siftDown(double[] data, int root, int size) {
        while (true) {
            int largest = root;
            int left = 2 * root + 1;
            int right = left + 1;

            if (left < size && data[left] > data[largest]) {
                largest = left;
            }
            if (right < size && data[right] > data[largest]) {
                largest = right;
            }
            if (largest == root) {
                return; // heap property holds
            }
            swap(data, root, largest);
            root = largest;
        }
    }

    private static void swap(double[] data, int i, int j) {
        double temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    @Override
    public String getName() {
        return "Heap Sort";
    }

    @Override
    public String getTimeComplexity() {
        return "O(n log n) / O(n log n) / O(n log n)";
    }
}

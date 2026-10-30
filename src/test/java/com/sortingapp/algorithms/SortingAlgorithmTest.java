package com.sortingapp.algorithms;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.sortingapp.TestSupport.check;

/**
 * Checks every sorting algorithm against Java's built-in {@link Arrays#sort(double[])}.
 */
public final class SortingAlgorithmTest {

    private static final int LARGE = 200_000;

    private SortingAlgorithmTest() {
    }

    public static void runAll() {
        List<SortingAlgorithm> all = Arrays.asList(
                new InsertionSort(), new ShellSort(), new MergeSort(), new QuickSort(), new HeapSort());
        Random random = new Random(42);

        for (SortingAlgorithm algorithm : all) {
            String name = algorithm.getName();
            check(name + ": has a name", !name.isEmpty());
            check(name + ": has a complexity", !algorithm.getTimeComplexity().isEmpty());

            checkSorts(algorithm, name + ": empty array", new double[0]);
            checkSorts(algorithm, name + ": single element", new double[]{7});
            checkSorts(algorithm, name + ": two elements", new double[]{2, 1});
            checkSorts(algorithm, name + ": assignment example", new double[]{64, 34, 25, 12, 22, 11, 90});
            checkSorts(algorithm, name + ": already sorted", new double[]{1, 2, 3, 4, 5, 6});
            checkSorts(algorithm, name + ": reverse sorted", new double[]{6, 5, 4, 3, 2, 1});
            checkSorts(algorithm, name + ": duplicates", new double[]{3, 1, 3, 3, 2, 1, 2});
            checkSorts(algorithm, name + ": negatives and decimals",
                    new double[]{-2.5, 10, 0, -100, 3.14, 2.71, -0.5, 1e6, -1e-3});

            for (int size = 2; size <= 64; size++) {
                checkSorts(algorithm, name + ": random size " + size, randomArray(random, size, 20));
            }
            checkSorts(algorithm, name + ": 20,000 random values", randomArray(random, 20_000, 1_000_000));
            checkSorts(algorithm, name + ": 20,000 values with many duplicates", randomArray(random, 20_000, 5));
        }

        // Large inputs that break naive implementations (deep recursion, O(n^2) on duplicates)
        double[] ascending = new double[LARGE];
        double[] descending = new double[LARGE];
        double[] allEqual = new double[LARGE];
        for (int i = 0; i < LARGE; i++) {
            ascending[i] = i;
            descending[i] = LARGE - i;
            allEqual[i] = 42;
        }
        for (SortingAlgorithm algorithm : Arrays.asList(new ShellSort(), new MergeSort(), new QuickSort(), new HeapSort())) {
            String name = algorithm.getName();
            checkSorts(algorithm, name + ": 200,000 ascending", ascending);
            checkSorts(algorithm, name + ": 200,000 descending", descending);
            checkSorts(algorithm, name + ": 200,000 equal values", allEqual);
        }
        // Insertion Sort's best case: already sorted data takes O(n)
        checkSorts(new InsertionSort(), "Insertion Sort: 200,000 ascending", ascending);
    }

    private static void checkSorts(SortingAlgorithm algorithm, String name, double[] input) {
        double[] expected = input.clone();
        Arrays.sort(expected);
        double[] actual = input.clone();
        algorithm.sort(actual);
        check(name, Arrays.equals(expected, actual));
    }

    private static double[] randomArray(Random random, int size, int range) {
        double[] values = new double[size];
        for (int i = 0; i < size; i++) {
            values[i] = random.nextInt(range) - range / 2;
        }
        return values;
    }
}

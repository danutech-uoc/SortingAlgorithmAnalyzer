package com.sortingapp;

import com.sortingapp.algorithms.SortingAlgorithmTest;
import com.sortingapp.data.CSVLoaderTest;
import com.sortingapp.data.DataValidatorTest;
import com.sortingapp.performance.PerformanceAnalyzerTest;

/**
 * Runs every test and prints a summary. Exits with code 1 if any test fails.
 */
public final class TestRunner {

    private TestRunner() {
    }

    public static void main(String[] args) throws Exception {
        run("Sorting algorithms", SortingAlgorithmTest::runAll);
        run("Data validation", DataValidatorTest::runAll);
        run("CSV loading", CSVLoaderTest::runAll);
        run("Performance analyzer", PerformanceAnalyzerTest::runAll);

        System.out.println();
        System.out.printf("Passed: %d   Failed: %d%n", TestSupport.getPassed(), TestSupport.getFailed());
        if (TestSupport.getFailed() > 0) {
            System.exit(1);
        }
        System.out.println("ALL TESTS PASSED");
    }

    private static void run(String name, TestSupport.ThrowingRunnable tests) throws Exception {
        int failedBefore = TestSupport.getFailed();
        long start = System.nanoTime();
        tests.run();
        long millis = (System.nanoTime() - start) / 1_000_000;
        String status = TestSupport.getFailed() == failedBefore ? "OK" : "FAILED";
        System.out.printf("%-22s %s (%d ms)%n", name, status, millis);
    }
}

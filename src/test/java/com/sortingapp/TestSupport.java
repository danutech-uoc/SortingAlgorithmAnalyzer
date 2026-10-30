package com.sortingapp;

/**
 * A minimal assertion helper so the tests run with plain {@code java}, without JUnit.
 */
public final class TestSupport {

    /** A block of code that may throw. */
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static int passed;
    private static int failed;

    private TestSupport() {
    }

    /**
     * Records a passed or failed check.
     *
     * @param name      what is being checked
     * @param condition true if the check passed
     */
    public static void check(String name, boolean condition) {
        if (condition) {
            passed++;
        } else {
            failed++;
            System.out.println("  FAIL: " + name);
        }
    }

    /**
     * Checks that the action throws an exception of the given type.
     *
     * @return the thrown exception, or null if none (or the wrong type) was thrown
     */
    public static Throwable expectThrows(String name, Class<? extends Throwable> type, ThrowingRunnable action) {
        try {
            action.run();
        } catch (Throwable error) {
            boolean matches = type.isInstance(error);
            check(name + " (expected " + type.getSimpleName() + ", got " + error.getClass().getSimpleName() + ")", matches);
            return matches ? error : null;
        }
        check(name + " (expected " + type.getSimpleName() + ", nothing was thrown)", false);
        return null;
    }

    public static int getPassed() {
        return passed;
    }

    public static int getFailed() {
        return failed;
    }
}

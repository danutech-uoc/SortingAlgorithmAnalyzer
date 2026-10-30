package com.sortingapp.data;

/**
 * The numeric values of one CSV column, ready to be sorted.
 * Blank cells are left out and counted separately.
 *
 * @author Member 1 - File Handling &amp; User Interface
 */
public final class NumericColumn {

    private final String name;
    private final double[] values;
    private final int blankCount;

    NumericColumn(String name, double[] values, int blankCount) {
        this.name = name;
        this.values = values;
        this.blankCount = blankCount;
    }

    /**
     * @return the column header
     */
    public String getName() {
        return name;
    }

    /**
     * @return a copy of the values in their original (unsorted) file order
     */
    public double[] getValues() {
        return values.clone();
    }

    /**
     * @return the number of values in the column
     */
    public int size() {
        return values.length;
    }

    /**
     * @return how many blank cells were skipped
     */
    public int getBlankCount() {
        return blankCount;
    }
}

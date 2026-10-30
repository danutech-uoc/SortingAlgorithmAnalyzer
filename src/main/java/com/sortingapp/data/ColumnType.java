package com.sortingapp.data;

/**
 * The kind of values found in a CSV column.
 * Only {@link #NUMERIC} columns can be sorted.
 *
 * @author Member 1 - File Handling &amp; User Interface
 */
public enum ColumnType {
    /** Every non-blank cell is a number. */
    NUMERIC("Numeric"),
    /** At least one cell contains text. */
    TEXT("Text"),
    /** Every cell is blank. */
    EMPTY("Empty");

    private final String label;

    ColumnType(String label) {
        this.label = label;
    }

    /**
     * @return a short label to display in the user interface
     */
    public String getLabel() {
        return label;
    }
}

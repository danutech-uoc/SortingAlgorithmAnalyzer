package com.sortingapp.data;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Validation rules for input files and column values.
 *
 * @author Member 1 - File Handling &amp; User Interface
 */
public final class DataValidator {

    /**
     * A plain decimal number: optional sign, digits with an optional decimal point,
     * and an optional exponent. Examples: 42, -3.5, .75, 1.2e5.
     * Values such as "NaN", "Infinity" or "1,000" are rejected on purpose.
     */
    private static final Pattern NUMBER = Pattern.compile("[+-]?(\\d+(\\.\\d*)?|\\.\\d+)([eE][+-]?\\d+)?");

    private static final int MAX_VALUE_LENGTH_IN_MESSAGE = 30;

    private DataValidator() {
        // Utility class - not meant to be instantiated
    }

    /**
     * Checks that the file exists, is readable, is not empty and has a .csv extension.
     *
     * @param file the file chosen by the user
     * @throws DataException if the file cannot be used
     */
    public static void validateFile(File file) throws DataException {
        if (file == null) {
            throw new DataException("No file was selected.");
        }
        if (!file.exists()) {
            throw new DataException("The file does not exist:\n" + file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new DataException("\"" + file.getName() + "\" is a folder, not a file.");
        }
        if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new DataException("\"" + file.getName() + "\" is not a CSV file.\nPlease choose a file with the .csv extension.");
        }
        if (!file.canRead()) {
            throw new DataException("The file cannot be read (check its permissions):\n" + file.getAbsolutePath());
        }
        if (file.length() == 0) {
            throw new DataException("The file \"" + file.getName() + "\" is empty.");
        }
    }

    /**
     * @param value a cell value
     * @return true if the value is null or contains only whitespace
     */
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * @param value a cell value
     * @return true if the value (ignoring surrounding spaces) is a number
     */
    public static boolean isNumeric(String value) {
        return value != null && NUMBER.matcher(value.trim()).matches();
    }

    /**
     * Works out whether a column holds numbers, text, or nothing at all.
     *
     * @param cells the raw cell values of the column
     * @return the detected column type
     */
    public static ColumnType detectType(String[] cells) {
        boolean hasValue = false;
        for (String cell : cells) {
            if (isBlank(cell)) {
                continue;
            }
            hasValue = true;
            if (!isNumeric(cell)) {
                return ColumnType.TEXT;
            }
        }
        return hasValue ? ColumnType.NUMERIC : ColumnType.EMPTY;
    }

    /**
     * Converts the cells of a column into numbers. Blank cells are skipped and counted.
     *
     * @param name  the column header (used in error messages)
     * @param cells the raw cell values of the column
     * @return the numeric values of the column
     * @throws DataException if a cell is not a number, a number is too large, or the column is empty
     */
    public static NumericColumn toNumericColumn(String name, String[] cells) throws DataException {
        double[] values = new double[cells.length];
        int count = 0;
        int blanks = 0;

        for (int i = 0; i < cells.length; i++) {
            if (isBlank(cells[i])) {
                blanks++;
                continue;
            }
            String cell = cells[i].trim();
            if (!isNumeric(cell)) {
                throw new DataException(String.format(
                        "Column \"%s\" contains non-numeric values (data row %d: \"%s\").%nPlease select a numeric column.",
                        name, i + 1, abbreviate(cell)));
            }
            double value = Double.parseDouble(cell);
            if (Double.isInfinite(value)) {
                throw new DataException(String.format(
                        "Column \"%s\" contains a number that is too large (data row %d).", name, i + 1));
            }
            // Adding 0.0 turns -0.0 into 0.0, so "-0" and "0" are treated as the same value
            values[count++] = value + 0.0;
        }

        if (count == 0) {
            throw new DataException("Column \"" + name + "\" is empty - it has no values to sort.");
        }
        return new NumericColumn(name, Arrays.copyOf(values, count), blanks);
    }

    private static String abbreviate(String value) {
        return value.length() <= MAX_VALUE_LENGTH_IN_MESSAGE
                ? value
                : value.substring(0, MAX_VALUE_LENGTH_IN_MESSAGE) + "...";
    }
}

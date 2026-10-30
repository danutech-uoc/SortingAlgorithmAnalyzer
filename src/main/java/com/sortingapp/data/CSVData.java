package com.sortingapp.data;

import java.util.Collections;
import java.util.List;

/**
 * An immutable, in-memory copy of a loaded CSV file: its headers, rows and the
 * detected type of every column. Created by {@link CSVLoader#load(java.io.File)}.
 *
 * @author Member 1 - File Handling &amp; User Interface
 */
public final class CSVData {

    private final String fileName;
    private final String[] headers;
    private final List<String[]> rows;
    private final ColumnType[] columnTypes;

    CSVData(String fileName, String[] headers, List<String[]> rows) {
        this.fileName = fileName;
        this.headers = headers;
        this.rows = Collections.unmodifiableList(rows);
        this.columnTypes = new ColumnType[headers.length];
        for (int column = 0; column < headers.length; column++) {
            columnTypes[column] = DataValidator.detectType(getColumn(column));
        }
    }

    /**
     * @return the name of the file the data was loaded from
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return a copy of the column headers
     */
    public String[] getHeaders() {
        return headers.clone();
    }

    /**
     * @param column column index (0-based)
     * @return the header of the column
     */
    public String getHeader(int column) {
        return headers[column];
    }

    /**
     * @return the number of data rows (the header row is not counted)
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * @return the number of columns
     */
    public int getColumnCount() {
        return headers.length;
    }

    /**
     * @param row    row index (0-based, header excluded)
     * @param column column index (0-based)
     * @return the raw cell value; an empty string if the cell is missing
     */
    public String getValue(int row, int column) {
        return rows.get(row)[column];
    }

    /**
     * @param column column index (0-based)
     * @return whether the column is numeric, text or empty
     */
    public ColumnType getColumnType(int column) {
        return columnTypes[column];
    }

    /**
     * @return how many columns can be sorted
     */
    public int getNumericColumnCount() {
        int count = 0;
        for (ColumnType type : columnTypes) {
            if (type == ColumnType.NUMERIC) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return the index of the first numeric column, or -1 if there is none
     */
    public int findFirstNumericColumn() {
        for (int column = 0; column < columnTypes.length; column++) {
            if (columnTypes[column] == ColumnType.NUMERIC) {
                return column;
            }
        }
        return -1;
    }

    /**
     * @param column column index (0-based)
     * @return the raw cell values of the column, top to bottom
     */
    public String[] getColumn(int column) {
        String[] cells = new String[rows.size()];
        for (int row = 0; row < cells.length; row++) {
            cells[row] = rows.get(row)[column];
        }
        return cells;
    }

    /**
     * Extracts a column as numbers so it can be sorted.
     *
     * @param column column index (0-based)
     * @return the numeric values of the column
     * @throws DataException if the index is invalid or the column is not numeric
     */
    public NumericColumn getNumericColumn(int column) throws DataException {
        if (column < 0 || column >= headers.length) {
            throw new DataException("Please select a valid column.");
        }
        return DataValidator.toNumericColumn(headers[column], getColumn(column));
    }
}

package com.sortingapp.ui;

import com.sortingapp.data.CSVData;

import javax.swing.table.AbstractTableModel;

/**
 * Shows the first rows of a loaded CSV file in a read-only table.
 * The first column is the row number; each header also shows the column type.
 *
 * @author Member 1 - File Handling &amp; User Interface
 */
class PreviewTableModel extends AbstractTableModel {

    private final CSVData data;
    private final int rowLimit;

    /**
     * @param data     the loaded CSV file
     * @param rowLimit the maximum number of rows to show
     */
    PreviewTableModel(CSVData data, int rowLimit) {
        this.data = data;
        this.rowLimit = rowLimit;
    }

    @Override
    public int getRowCount() {
        return Math.min(data.getRowCount(), rowLimit);
    }

    @Override
    public int getColumnCount() {
        return data.getColumnCount() + 1;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "#";
        }
        return data.getHeader(column - 1) + " (" + data.getColumnType(column - 1).getLabel() + ")";
    }

    @Override
    public Object getValueAt(int row, int column) {
        return column == 0 ? String.valueOf(row + 1) : data.getValue(row, column - 1);
    }
}

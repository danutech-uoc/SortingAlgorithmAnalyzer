package com.sortingapp.ui;

import com.sortingapp.data.CSVData;
import com.sortingapp.data.CSVLoader;
import com.sortingapp.data.ColumnType;
import com.sortingapp.data.DataException;
import com.sortingapp.data.NumericColumn;
import com.sortingapp.performance.AlgorithmResult;
import com.sortingapp.performance.AnalysisReport;
import com.sortingapp.performance.PerformanceAnalyzer;
import com.sortingapp.performance.TimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * The main application window.
 * <p>
 * Workflow: <b>Browse CSV</b> &rarr; choose a numeric column &rarr; <b>Analyze</b> &rarr;
 * view execution times, the fastest algorithm, a chart and the sorted values.
 * <p>
 * Loading files and running the algorithms happen on background threads
 * ({@link SwingWorker}), so the window never freezes. All Swing components are only
 * updated on the Event Dispatch Thread.
 *
 * @author Member 1 - File Handling &amp; User Interface
 */
public class MainFrame extends JFrame {

    private static final int PREVIEW_ROW_LIMIT = 500;
    private static final int SORTED_OUTPUT_LIMIT = 5_000;
    /** Above this many values the user is warned that Insertion Sort (O(n^2)) may be slow. */
    private static final int LARGE_DATASET_WARNING = 100_000;
    private static final int DEFAULT_RUNS = 3;
    private static final int MAX_RUNS = 20;

    private static final Color FASTEST_ROW_COLOR = new Color(212, 237, 218);
    private static final Color OK_COLOR = new Color(30, 130, 60);
    private static final Color ERROR_COLOR = new Color(190, 40, 40);

    private static final int TAB_PREVIEW = 0;
    private static final int TAB_RESULTS = 1;

    private final PerformanceAnalyzer analyzer = new PerformanceAnalyzer();

    private CSVData dataset;
    private File lastDirectory;
    private boolean busy;
    private int fastestRow = -1;

    // Step 1 - load a file
    private final JButton browseButton = new JButton("Browse CSV...");
    private final JLabel fileInfoLabel = new JLabel("No file loaded. Click \"Browse CSV...\" to choose a dataset.");

    // Step 2 - choose a column and run
    private final JComboBox<String> columnSelector = new JComboBox<>();
    private final JLabel columnTypeLabel = new JLabel(" ");
    private final JSpinner runsSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_RUNS, 1, MAX_RUNS, 1));
    private final JButton analyzeButton = new JButton("Analyze");

    // Output tabs
    private final JTabbedPane tabs = new JTabbedPane();
    private final JTable previewTable = new JTable();
    private final JLabel previewInfoLabel = new JLabel("Load a CSV file to preview its contents.");
    private final DefaultTableModel resultsModel = new DefaultTableModel(new Object[]{
            "Rank", "Algorithm", "Time complexity (best / average / worst)",
            "Average time", "Fastest run", "Correctly sorted"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable resultsTable = new JTable(resultsModel);
    private final JLabel fastestLabel = new JLabel();
    private final BarChartPanel chartPanel = new BarChartPanel();
    private final JTextArea sortedOutputArea = new JTextArea();

    // Status bar
    private final JLabel statusLabel = new JLabel("Ready");
    private final JProgressBar progressBar = new JProgressBar(0, 100);

    /**
     * Builds the window and all of its components.
     */
    public MainFrame() {
        super("Sorting Algorithm Performance Analyzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createControlPanel(), BorderLayout.NORTH);
        add(createOutputTabs(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);

        browseButton.addActionListener(e -> browseFile());
        analyzeButton.addActionListener(e -> runAnalysis());
        columnSelector.addActionListener(e -> updateColumnTypeIndicator());

        clearResults();
        updateControls();
        setSize(1080, 720);
        setMinimumSize(new Dimension(840, 560));
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------------------------ layout

    private JPanel createControlPanel() {
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filePanel.setBorder(BorderFactory.createTitledBorder("Step 1 - Load a dataset"));
        browseButton.setToolTipText("Choose a .csv file from your computer");
        filePanel.add(browseButton);
        filePanel.add(fileInfoLabel);

        JPanel columnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        columnPanel.setBorder(BorderFactory.createTitledBorder("Step 2 - Choose a numeric column and run the analysis"));
        columnSelector.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        columnSelector.setRenderer(new ColumnRenderer());
        columnSelector.setToolTipText("Only columns marked [Numeric] can be sorted");
        runsSpinner.setToolTipText("Each algorithm sorts a fresh copy of the data this many times; the average time is reported");
        analyzeButton.setFont(analyzeButton.getFont().deriveFont(Font.BOLD));
        analyzeButton.setToolTipText("Run all 5 sorting algorithms on the selected column");

        columnPanel.add(new JLabel("Column to sort:"));
        columnPanel.add(columnSelector);
        columnPanel.add(columnTypeLabel);
        columnPanel.add(Box.createHorizontalStrut(12));
        columnPanel.add(new JLabel("Runs per algorithm:"));
        columnPanel.add(runsSpinner);
        columnPanel.add(analyzeButton);

        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 4));
        panel.setBorder(new EmptyBorder(8, 8, 0, 8));
        panel.add(filePanel);
        panel.add(columnPanel);
        return panel;
    }

    private JTabbedPane createOutputTabs() {
        // Tab 1: data preview
        JPanel previewPanel = new JPanel(new BorderLayout(0, 6));
        previewPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        previewTable.setFillsViewportHeight(true);
        previewTable.getTableHeader().setReorderingAllowed(false);
        previewPanel.add(previewInfoLabel, BorderLayout.NORTH);
        previewPanel.add(new JScrollPane(previewTable), BorderLayout.CENTER);

        // Tab 2: results table, fastest algorithm and chart
        fastestLabel.setFont(fastestLabel.getFont().deriveFont(Font.BOLD, 16f));
        fastestLabel.setBorder(new EmptyBorder(4, 4, 4, 4));

        resultsTable.setRowHeight(26);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.getTableHeader().setReorderingAllowed(false);
        resultsTable.setDefaultRenderer(Object.class, new ResultRowRenderer());
        TableColumnModel columns = resultsTable.getColumnModel();
        int[] widths = {50, 130, 270, 110, 110, 110};
        for (int i = 0; i < widths.length; i++) {
            columns.getColumn(i).setPreferredWidth(widths[i]);
        }
        JScrollPane tableScroll = new JScrollPane(resultsTable);
        tableScroll.setPreferredSize(new Dimension(600, 6 * 26 + 30));

        chartPanel.setBorder(BorderFactory.createTitledBorder("Average execution time (shorter bar = faster)"));

        JPanel resultsBody = new JPanel(new BorderLayout(0, 8));
        resultsBody.add(tableScroll, BorderLayout.NORTH);
        resultsBody.add(chartPanel, BorderLayout.CENTER);

        JPanel resultsPanel = new JPanel(new BorderLayout(0, 8));
        resultsPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        resultsPanel.add(fastestLabel, BorderLayout.NORTH);
        resultsPanel.add(resultsBody, BorderLayout.CENTER);

        // Tab 3: sorted values
        sortedOutputArea.setEditable(false);
        sortedOutputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        sortedOutputArea.setMargin(new java.awt.Insets(8, 8, 8, 8));

        tabs.addTab("Data Preview", previewPanel);
        tabs.addTab("Results", resultsPanel);
        tabs.addTab("Sorted Output", new JScrollPane(sortedOutputArea));
        return tabs;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout(8, 0));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(4, 8, 4, 8)));
        progressBar.setPreferredSize(new Dimension(240, 16));
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        statusBar.add(statusLabel, BorderLayout.CENTER);
        statusBar.add(progressBar, BorderLayout.EAST);
        return statusBar;
    }

    // ------------------------------------------------------------ step 1: file

    /** Opens a file chooser and loads the chosen CSV file. */
    private void browseFile() {
        JFileChooser chooser = new JFileChooser(lastDirectory);
        chooser.setDialogTitle("Select a CSV file");
        // "All Files" stays available, so choosing a wrong file type shows a clear error message
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        lastDirectory = file.getParentFile();
        loadFile(file);
    }

    /** Reads the file on a background thread, then shows it in the window. */
    private void loadFile(File file) {
        setBusy(true, "Loading " + file.getName() + "...");
        progressBar.setIndeterminate(true);

        new SwingWorker<CSVData, Void>() {
            @Override
            protected CSVData doInBackground() throws Exception {
                return CSVLoader.load(file);
            }

            @Override
            protected void done() {
                setBusy(false, null);
                try {
                    showDataset(get());
                } catch (ExecutionException ex) {
                    statusLabel.setText("Could not load " + file.getName());
                    showError("Invalid file", messageOf(ex.getCause()));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    /** Fills the column selector and preview table with a newly loaded file. */
    private void showDataset(CSVData data) {
        dataset = data;
        clearResults();

        columnSelector.removeAllItems();
        for (String header : data.getHeaders()) {
            columnSelector.addItem(header);
        }
        int firstNumeric = data.findFirstNumericColumn();
        if (firstNumeric >= 0) {
            columnSelector.setSelectedIndex(firstNumeric);
        }
        updateColumnTypeIndicator();

        previewTable.setModel(new PreviewTableModel(data, PREVIEW_ROW_LIMIT));
        // With many columns, allow horizontal scrolling instead of squeezing them together
        previewTable.setAutoResizeMode(data.getColumnCount() > 8
                ? JTable.AUTO_RESIZE_OFF : JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        previewTable.getColumnModel().getColumn(0).setMaxWidth(70); // keep the row-number column narrow
        previewInfoLabel.setText(String.format(
                "Showing %,d of %,d data rows. Columns marked (Numeric) can be sorted.",
                Math.min(PREVIEW_ROW_LIMIT, data.getRowCount()), data.getRowCount()));

        fileInfoLabel.setText(String.format("%s  -  %,d rows, %d columns (%d numeric)",
                data.getFileName(), data.getRowCount(), data.getColumnCount(), data.getNumericColumnCount()));
        tabs.setSelectedIndex(TAB_PREVIEW);
        statusLabel.setText("Loaded " + data.getFileName() + ". Choose a numeric column and click Analyze.");
        updateControls();

        if (firstNumeric < 0) {
            JOptionPane.showMessageDialog(this,
                    "The file was loaded, but none of its columns contain only numbers.\n"
                            + "Sorting requires a numeric column - please choose a different file.",
                    "No numeric columns", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Shows whether the selected column can be sorted. */
    private void updateColumnTypeIndicator() {
        int column = columnSelector.getSelectedIndex();
        if (dataset == null || column < 0 || column >= dataset.getColumnCount()) {
            columnTypeLabel.setText(" ");
            return;
        }
        ColumnType type = dataset.getColumnType(column);
        if (type == ColumnType.NUMERIC) {
            columnTypeLabel.setText("✔ Numeric column - ready to sort");
            columnTypeLabel.setForeground(OK_COLOR);
        } else if (type == ColumnType.TEXT) {
            columnTypeLabel.setText("✖ Text column - cannot be sorted");
            columnTypeLabel.setForeground(ERROR_COLOR);
        } else {
            columnTypeLabel.setText("✖ Empty column - nothing to sort");
            columnTypeLabel.setForeground(ERROR_COLOR);
        }
    }

    // --------------------------------------------------------- step 2: analyze

    /** Validates the selected column, then runs all algorithms on a background thread. */
    private void runAnalysis() {
        if (dataset == null) {
            showError("No dataset", "Please load a CSV file first.");
            return;
        }
        int columnIndex = columnSelector.getSelectedIndex();
        if (columnIndex < 0) {
            showError("No column selected", "Please select a column to sort.");
            return;
        }

        NumericColumn column;
        try {
            column = dataset.getNumericColumn(columnIndex);
        } catch (DataException ex) {
            showError("Invalid column", ex.getMessage());
            return;
        }

        if (column.size() >= LARGE_DATASET_WARNING) {
            int choice = JOptionPane.showConfirmDialog(this, String.format(
                    "The column has %,d values. Insertion Sort is O(n²) and may take a long time%n"
                            + "on data this large. Do you want to continue?", column.size()),
                    "Large dataset", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        int runs = (Integer) runsSpinner.getValue();
        clearResults();
        setBusy(true, "Running sorting algorithms...");

        SwingWorker<AnalysisReport, String> worker = new SwingWorker<AnalysisReport, String>() {
            @Override
            protected AnalysisReport doInBackground() {
                return analyzer.analyze(column.getValues(), runs, (completed, total, message) -> {
                    setProgress(completed * 100 / total);
                    publish(message);
                });
            }

            @Override
            protected void process(List<String> messages) {
                statusLabel.setText(messages.get(messages.size() - 1));
            }

            @Override
            protected void done() {
                setBusy(false, null);
                try {
                    showResults(get(), column);
                } catch (ExecutionException ex) {
                    statusLabel.setText("Analysis failed.");
                    showError("Analysis failed", messageOf(ex.getCause()));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        worker.addPropertyChangeListener(event -> {
            if ("progress".equals(event.getPropertyName())) {
                progressBar.setValue((Integer) event.getNewValue());
            }
        });
        worker.execute();
    }

    /** Fills the results table, fastest-algorithm banner, chart and sorted output. */
    private void showResults(AnalysisReport report, NumericColumn column) {
        List<AlgorithmResult> results = report.getResults();
        AlgorithmResult fastest = report.getFastest();
        fastestRow = results.indexOf(fastest);

        resultsModel.setRowCount(0);
        for (AlgorithmResult result : results) {
            resultsModel.addRow(new Object[]{
                    report.getRank(result),
                    result.getAlgorithmName(),
                    result.getTimeComplexity(),
                    TimeFormatter.format(result.getAverageNanos()),
                    TimeFormatter.format(result.getBestNanos()),
                    result.isSortedCorrectly() ? "Yes" : "No"});
        }

        int runs = results.get(0).getRunCount();
        if (fastest != null) {
            fastestLabel.setText(String.format(
                    "★ Fastest algorithm: %s  -  average %s over %d run%s, %,d values from column \"%s\"",
                    fastest.getAlgorithmName(), TimeFormatter.format(fastest.getAverageNanos()),
                    runs, runs == 1 ? "" : "s", report.getDataSize(), column.getName()));
            fastestLabel.setForeground(OK_COLOR);
        }
        chartPanel.setResults(results, fastest);

        sortedOutputArea.setText(buildSortedOutput(report.getSortedData(), column));
        sortedOutputArea.setCaretPosition(0);

        tabs.setSelectedIndex(TAB_RESULTS);
        statusLabel.setText(fastest != null
                ? "Analysis complete. Fastest: " + fastest.getAlgorithmName()
                : "Analysis complete.");

        if (!report.allSortedCorrectly()) {
            showError("Sorting error", "At least one algorithm did not sort the data correctly (see the table).");
        }
    }

    private String buildSortedOutput(double[] sorted, NumericColumn column) {
        StringBuilder text = new StringBuilder();
        text.append("Column:              ").append(column.getName()).append('\n');
        text.append(String.format("Values sorted:       %,d (ascending order)%n", sorted.length));
        if (column.getBlankCount() > 0) {
            text.append(String.format("Blank cells skipped: %,d%n", column.getBlankCount()));
        }
        text.append("Minimum:             ").append(formatValue(sorted[0])).append('\n');
        text.append("Maximum:             ").append(formatValue(sorted[sorted.length - 1])).append("\n\n");

        int shown = Math.min(sorted.length, SORTED_OUTPUT_LIMIT);
        for (int i = 0; i < shown; i++) {
            text.append(String.format("%8d   ", i + 1)).append(formatValue(sorted[i])).append('\n');
        }
        if (sorted.length > shown) {
            text.append(String.format("%n... %,d more values not shown.%n", sorted.length - shown));
        }
        return text.toString();
    }

    /** Shows whole numbers without ".0" and avoids scientific notation such as 1.0E7. */
    private static String formatValue(double value) {
        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return Long.toString((long) value);
        }
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    // ----------------------------------------------------------------- helpers

    /** Enables or disables the controls while a background task is running. */
    private void setBusy(boolean isBusy, String message) {
        busy = isBusy;
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
        progressBar.setVisible(isBusy);
        if (message != null) {
            statusLabel.setText(message);
        }
        setCursor(isBusy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
        updateControls();
    }

    private void updateControls() {
        boolean hasData = dataset != null;
        browseButton.setEnabled(!busy);
        columnSelector.setEnabled(!busy && hasData);
        runsSpinner.setEnabled(!busy && hasData);
        analyzeButton.setEnabled(!busy && hasData);
    }

    private void clearResults() {
        resultsModel.setRowCount(0);
        fastestRow = -1;
        fastestLabel.setText("Choose a numeric column and click Analyze to compare the algorithms.");
        fastestLabel.setForeground(Color.GRAY);
        chartPanel.clear();
        sortedOutputArea.setText("");
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private static String messageOf(Throwable error) {
        if (error instanceof StackOverflowError) {
            return "The data is too large to sort (stack overflow).";
        }
        if (error instanceof OutOfMemoryError) {
            return "Not enough memory to process this file.";
        }
        return error.getMessage() != null ? error.getMessage() : error.toString();
    }

    // --------------------------------------------------------------- renderers

    /** Shows each column's type next to its name in the drop-down list; text columns are greyed out. */
    private final class ColumnRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            // index is -1 when painting the closed combo box itself
            int column = index >= 0 ? index : columnSelector.getSelectedIndex();
            if (dataset != null && value != null && column >= 0 && column < dataset.getColumnCount()) {
                ColumnType type = dataset.getColumnType(column);
                setText(value + "   [" + type.getLabel() + "]");
                if (type != ColumnType.NUMERIC && !isSelected) {
                    setForeground(Color.GRAY);
                }
            }
            return this;
        }
    }

    /** Highlights the fastest algorithm's row in green and bold. */
    private final class ResultRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            boolean fastest = row == fastestRow;
            if (!isSelected) {
                setBackground(fastest ? FASTEST_ROW_COLOR : table.getBackground());
                setForeground("No".equals(value) ? ERROR_COLOR : table.getForeground());
            }
            setFont(getFont().deriveFont(fastest ? Font.BOLD : Font.PLAIN));
            setHorizontalAlignment(column == 1 || column == 2 ? LEFT : CENTER);
            return this;
        }
    }
}

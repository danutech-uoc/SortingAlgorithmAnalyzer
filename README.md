# Sorting Algorithm Performance Analyzer

IC4302 – Programming III group assignment. This is a Java Swing application. You load a
`.csv` dataset, choose a numeric column, and the app runs five sorting algorithms on it.
It measures each algorithm's execution time and shows which one is fastest.

## Team Members

| Member | Name | Student No. | Responsibility |
|---|---|---|---|
| 1 | _[Name]_ | _[T0XXXX]_ | CSV file handling, validation and the GUI |
| 2 | _[Name]_ | _[T0XXXX]_ | Sorting algorithms and performance measurement |

## Requirements

- JDK 11 or newer (no other libraries are needed)

## How to Run

On Windows, double-click the scripts or run them from a terminal in this folder:

| Script | What it does |
|---|---|
| `build.bat` | Compiles the application into `bin/` |
| `run.bat` | Builds the application if needed, then starts it |
| `test.bat` | Builds the application and runs all tests |
| `package.bat ID1 ID2` | Creates the Moodle ZIP and a runnable JAR in `../submission/` |

On any operating system, run these commands from this folder:

```bash
# Compile
javac -encoding UTF-8 -d bin -sourcepath src/main/java src/main/java/com/sortingapp/Main.java

# Run
java -cp bin com.sortingapp.Main

# Tests (on Windows, use "bin;bin-test" as the classpath)
javac -encoding UTF-8 -cp bin -d bin-test -sourcepath src/test/java src/test/java/com/sortingapp/TestRunner.java
java -cp bin:bin-test com.sortingapp.TestRunner
```

In VS Code (with the Java Extension Pack installed), open `Main.java` and click **Run**.

## How to Use

1. Click **Browse CSV...** and choose a file. The **Data Preview** tab shows its rows,
   and every column is labelled *Numeric*, *Text* or *Empty*.
2. Choose a column in **Column to sort**. The indicator next to it shows whether the
   column can be sorted.
3. Optionally, change **Runs per algorithm**. Each algorithm sorts a fresh copy of the
   data this many times, and the average time is reported.
4. Click **Analyze**. The **Results** tab shows each algorithm's rank, time complexity,
   average and fastest time, and whether it sorted the data correctly. The fastest
   algorithm is highlighted in green and shown in a bar chart. The **Sorted Output**
   tab lists the sorted values.

## Features

- RFC 4180 CSV parsing: quoted values with commas or line breaks, escaped quotes (`""`),
  Windows and Unix line endings, Excel's UTF-8 byte order mark, blank lines and short rows
- Automatic detection of each column's type, with support for integers, decimals,
  negative numbers and scientific notation (`1.5e3`)
- Blank cells are skipped and counted
- Five sorting algorithms that all implement the same `SortingAlgorithm` interface
- Fair timing: `System.nanoTime()`, a fresh copy of the data for every run, a JIT
  warm-up before timing, and the average of several runs
- Each algorithm's output is checked against Java's `Arrays.sort`
- Loading and sorting run on background threads (`SwingWorker`), so the window never
  freezes and a progress bar shows how far the analysis has got
- Clear error messages for invalid input (see the table below)

## Error Handling

| Situation | Result |
|---|---|
| The file is not a `.csv` file | "... is not a CSV file" |
| The file is empty, or has a header but no data rows | A clear message; the app does not crash |
| A quoted value is never closed (corrupted file) | "The file appears to be corrupted..." |
| The selected column contains text | "Column ... contains non-numeric values (data row N: ...)" |
| The selected column is empty | "Column ... is empty - it has no values to sort" |
| The file has no numeric columns | A warning after loading |
| The column has 100,000 or more values | A warning that Insertion Sort (O(n²)) may be slow, before it runs |

## Algorithms Implemented

| Algorithm | Best | Average | Worst | Notes |
|---|---|---|---|---|
| Insertion Sort | O(n) | O(n²) | O(n²) | Fast on small or nearly sorted data |
| Shell Sort | O(n log n) | ~O(n^1.25) | O(n^1.5) | Uses Knuth's gap sequence 1, 4, 13, 40, ... |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | Stable; reuses a single buffer for merging |
| Quick Sort | O(n log n) | O(n log n) | O(n²) | Median-of-three pivot with Hoare partitioning |
| Heap Sort | O(n log n) | O(n log n) | O(n log n) | In place; uses an iterative sift-down |

The textbook Quick Sort always uses the last element as the pivot. That takes O(n²) on
sorted data and on columns with many repeated values, which are common in real
datasets (ages, ratings and so on), and it can crash with a `StackOverflowError`. This
implementation avoids both problems.

## Project Structure

```
src/main/java/com/sortingapp/
├── Main.java                       Entry point
├── ui/                             Member 1
│   ├── MainFrame.java              Main window and event handling
│   ├── PreviewTableModel.java      Data preview table
│   └── BarChartPanel.java          Execution time chart
├── data/                           Member 1
│   ├── CSVLoader.java              CSV parser
│   ├── CSVData.java                Loaded file (headers, rows, column types)
│   ├── DataValidator.java          File and number validation
│   ├── NumericColumn.java          A column converted to numbers
│   ├── ColumnType.java             NUMERIC / TEXT / EMPTY
│   └── DataException.java          User-friendly error
├── algorithms/                     Member 2
│   ├── SortingAlgorithm.java       Common interface
│   ├── InsertionSort.java
│   ├── ShellSort.java
│   ├── MergeSort.java
│   ├── QuickSort.java
│   └── HeapSort.java
└── performance/                    Member 2
    ├── PerformanceAnalyzer.java    Runs, times and verifies all algorithms
    ├── AlgorithmResult.java        Times of one algorithm
    ├── AnalysisReport.java         All results and the fastest algorithm
    └── TimeFormatter.java          ns / µs / ms / s formatting
src/test/java/                      Tests (run with test.bat)
sample_data/                        Test datasets
```

## Testing

`test.bat` runs more than 470 checks. They cover:

- Every algorithm on empty, single-element, sorted, reverse-sorted, duplicate-heavy and
  random arrays, and on decimal and negative values
- 200,000-element sorted, reverse-sorted and all-equal arrays
- CSV parsing edge cases and every invalid-file case listed above
- Timing, verification and fastest-algorithm selection

Sample files in `sample_data/`:

| File | Purpose |
|---|---|
| `students.csv` | Small, realistic dataset with text columns, decimals, quoted names and blank cells |
| `large_10000.csv`, `large_50000.csv` | Random data for performance comparison |
| `sorted_10000.csv`, `reverse_10000.csv` | Best and worst cases (watch Insertion Sort) |
| `invalid/` | Empty file, header-only file, corrupted file, file with no numeric columns, and a `.txt` file |

## Performance Results

See [PERFORMANCE_RESULTS.md](PERFORMANCE_RESULTS.md) for measured times on every sample dataset
and on a 7.7-million-row Kaggle dataset, with an explanation of each result.

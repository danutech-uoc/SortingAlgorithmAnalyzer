package com.sortingapp.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads CSV files into {@link CSVData}.
 * <p>
 * The parser follows the common CSV rules (RFC 4180):
 * <ul>
 *   <li>values are separated by commas;</li>
 *   <li>a value may be wrapped in double quotes, so it can contain commas or line breaks,
 *       e.g. {@code "Perera, Kasun"};</li>
 *   <li>a double quote inside a quoted value is written twice ({@code ""});</li>
 *   <li>Windows (CRLF) and Unix (LF) line endings are both accepted.</li>
 * </ul>
 * The first row is the header row. Blank lines are ignored, and rows with fewer
 * values than the header are padded with blank cells.
 *
 * @author Member 1 - File Handling &amp; User Interface
 */
public final class CSVLoader {

    private static final char DELIMITER = ',';
    private static final char QUOTE = '"';
    /** Invisible marker that Excel puts at the start of UTF-8 files. */
    private static final char BYTE_ORDER_MARK = '﻿';

    private CSVLoader() {
        // Utility class - not meant to be instantiated
    }

    /**
     * Validates and reads a CSV file.
     *
     * @param file the CSV file to read
     * @return the parsed headers and rows
     * @throws DataException if the file is invalid, unreadable, empty or corrupted
     */
    public static CSVData load(File file) throws DataException {
        DataValidator.validateFile(file);

        List<List<String>> records;
        // BufferedReader reads the file in large blocks instead of one byte at a time, which is much faster
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            records = parse(reader);
        } catch (IOException e) {
            throw new DataException("Could not read the file: " + e.getMessage(), e);
        }

        if (records.isEmpty()) {
            throw new DataException("The file \"" + file.getName() + "\" is empty - it has no header row.");
        }
        String[] headers = buildHeaders(records.get(0));
        if (records.size() < 2) {
            throw new DataException("The file \"" + file.getName() + "\" has a header row but no data rows.");
        }

        List<String[]> rows = new ArrayList<>(records.size() - 1);
        for (int i = 1; i < records.size(); i++) {
            rows.add(toRow(records.get(i), headers.length));
        }
        return new CSVData(file.getName(), headers, rows);
    }

    /**
     * Splits the text into records (rows) and fields (cells), one character at a time.
     */
    private static List<List<String>> parse(BufferedReader reader) throws IOException, DataException {
        List<List<String>> records = new ArrayList<>();
        List<String> record = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        boolean firstCharacter = true;

        int c;
        while ((c = reader.read()) != -1) {
            char ch = (char) c;
            if (firstCharacter) {
                firstCharacter = false;
                if (ch == BYTE_ORDER_MARK) {
                    continue;
                }
            }

            if (inQuotes) {
                if (ch != QUOTE) {
                    field.append(ch); // commas and line breaks are part of a quoted value
                    continue;
                }
                // A quote inside a quoted value: either an escaped quote ("") or the closing quote
                reader.mark(1);
                int next = reader.read();
                if (next == QUOTE) {
                    field.append(QUOTE);
                } else {
                    inQuotes = false;
                    if (next != -1) {
                        reader.reset();
                    }
                }
            } else if (ch == QUOTE && field.length() == 0) {
                inQuotes = true; // a quote at the start of a value opens a quoted value
            } else if (ch == DELIMITER) {
                record.add(field.toString());
                field.setLength(0);
            } else if (ch == '\r' || ch == '\n') {
                if (ch == '\r') {
                    // Treat the Windows line ending "\r\n" as a single line break
                    reader.mark(1);
                    int next = reader.read();
                    if (next != '\n' && next != -1) {
                        reader.reset();
                    }
                }
                endRecord(records, record, field);
                record = new ArrayList<>();
            } else {
                field.append(ch);
            }
        }

        if (inQuotes) {
            throw new DataException("The file appears to be corrupted: a quoted value is never closed.");
        }
        endRecord(records, record, field);
        return records;
    }

    /** Finishes the current record and keeps it unless it is a blank line. */
    private static void endRecord(List<List<String>> records, List<String> record, StringBuilder field) {
        record.add(field.toString());
        field.setLength(0);
        boolean blankLine = record.size() == 1 && record.get(0).trim().isEmpty();
        if (!blankLine) {
            records.add(record);
        }
    }

    /** Trims header names and gives blank headers a name such as "Column 3". */
    private static String[] buildHeaders(List<String> record) {
        String[] headers = new String[record.size()];
        for (int i = 0; i < headers.length; i++) {
            String header = record.get(i).trim();
            headers[i] = header.isEmpty() ? "Column " + (i + 1) : header;
        }
        return headers;
    }

    /** Makes every row exactly as wide as the header row. */
    private static String[] toRow(List<String> record, int width) {
        String[] row = new String[width];
        for (int i = 0; i < width; i++) {
            row[i] = i < record.size() ? record.get(i) : "";
        }
        return row;
    }
}

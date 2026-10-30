package com.sortingapp.data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import static com.sortingapp.TestSupport.check;
import static com.sortingapp.TestSupport.expectThrows;

/**
 * Tests CSV parsing and the handling of invalid files, using temporary files.
 */
public final class CSVLoaderTest {

    private CSVLoaderTest() {
    }

    public static void runAll() throws Exception {
        // Basic file
        CSVData basic = CSVLoader.load(write(".csv", "id,name,score\n1,Amal,90\n2,Nimal,75.5\n3,Sunil,82\n"));
        check("headers are read", Arrays.equals(basic.getHeaders(), new String[]{"id", "name", "score"}));
        check("row count excludes the header", basic.getRowCount() == 3);
        check("numeric column detected", basic.getColumnType(0) == ColumnType.NUMERIC);
        check("text column detected", basic.getColumnType(1) == ColumnType.TEXT);
        check("numeric column count", basic.getNumericColumnCount() == 2);
        check("first numeric column", basic.findFirstNumericColumn() == 0);
        check("decimal column extracted",
                Arrays.equals(basic.getNumericColumn(2).getValues(), new double[]{90, 75.5, 82}));
        expectThrows("text column cannot be extracted as numbers", DataException.class, () -> basic.getNumericColumn(1));
        expectThrows("invalid column index", DataException.class, () -> basic.getNumericColumn(5));

        // Quoted values with commas, escaped quotes and a line break
        CSVData quoted = CSVLoader.load(write(".csv",
                "name,note,score\n\"Perera, Kasun\",\"He said \"\"hi\"\"\",10\n\"Silva\",\"line one\nline two\",20\n"));
        check("quoted value keeps its comma", quoted.getValue(0, 0).equals("Perera, Kasun"));
        check("escaped quotes are unescaped", quoted.getValue(0, 1).equals("He said \"hi\""));
        check("quoted value keeps its line break", quoted.getValue(1, 1).equals("line one\nline two"));
        check("quoted file has 2 rows", quoted.getRowCount() == 2);
        check("column after quoted values is numeric", quoted.getColumnType(2) == ColumnType.NUMERIC);

        // Byte order mark, Windows line endings, blank lines, short rows, blank header
        CSVData messy = CSVLoader.load(write(".csv", "﻿a,,c\r\n1,2,3\r\n\r\n4,5\r\n  \r\n7,8,9"));
        check("BOM is removed from the first header", messy.getHeader(0).equals("a"));
        check("blank header gets a name", messy.getHeader(1).equals("Column 2"));
        check("blank lines are skipped", messy.getRowCount() == 3);
        check("short row is padded", messy.getValue(1, 2).isEmpty());
        NumericColumn c = messy.getNumericColumn(2);
        check("missing cell is skipped when sorting", c.size() == 2 && c.getBlankCount() == 1);

        // Invalid files
        expectThrows("non-existent file", DataException.class,
                () -> CSVLoader.load(new File("does-not-exist-12345.csv")));
        expectThrows("wrong extension", DataException.class, () -> CSVLoader.load(write(".txt", "a,b\n1,2\n")));
        expectThrows("empty file", DataException.class, () -> CSVLoader.load(write(".csv", "")));
        expectThrows("only blank lines", DataException.class, () -> CSVLoader.load(write(".csv", "\n\n  \n")));
        expectThrows("header without data rows", DataException.class, () -> CSVLoader.load(write(".csv", "a,b,c\n")));
        expectThrows("unclosed quote", DataException.class, () -> CSVLoader.load(write(".csv", "a,b\n\"oops,2\n")));
        expectThrows("folder instead of file", DataException.class,
                () -> CSVLoader.load(new File(System.getProperty("java.io.tmpdir"))));
        expectThrows("no file", DataException.class, () -> CSVLoader.load(null));

        // Uppercase extension is accepted
        CSVData upper = CSVLoader.load(write(".CSV", "x\n3\n1\n2\n"));
        check(".CSV extension accepted", upper.getRowCount() == 3);
    }

    private static File write(String suffix, String content) throws IOException {
        File file = File.createTempFile("csvloader-test", suffix);
        file.deleteOnExit();
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
        return file;
    }
}

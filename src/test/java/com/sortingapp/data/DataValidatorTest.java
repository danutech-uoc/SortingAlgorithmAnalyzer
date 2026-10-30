package com.sortingapp.data;

import java.util.Arrays;

import static com.sortingapp.TestSupport.check;
import static com.sortingapp.TestSupport.expectThrows;

/**
 * Tests number detection and column conversion.
 */
public final class DataValidatorTest {

    private DataValidatorTest() {
    }

    public static void runAll() throws Exception {
        for (String number : new String[]{"0", "42", "-7", "+3", "3.14", "-0.5", ".75", "5.", "1e3", "2.5E-4", " 12 "}) {
            check("isNumeric(\"" + number + "\")", DataValidator.isNumeric(number));
        }
        for (String text : new String[]{"", " ", "abc", "12abc", "1,000", "NaN", "Infinity", "--1", "1e", "0x1F", "$5", null}) {
            check("not isNumeric(\"" + text + "\")", !DataValidator.isNumeric(text));
        }

        check("detectType numeric", DataValidator.detectType(new String[]{"1", " 2.5", "", "-3"}) == ColumnType.NUMERIC);
        check("detectType text", DataValidator.detectType(new String[]{"1", "two", "3"}) == ColumnType.TEXT);
        check("detectType empty", DataValidator.detectType(new String[]{"", "  "}) == ColumnType.EMPTY);

        NumericColumn column = DataValidator.toNumericColumn("score", new String[]{"3", "", "-1.5", " 10 ", "-0"});
        check("blank cells are skipped", column.size() == 4 && column.getBlankCount() == 1);
        check("values are parsed in order", Arrays.equals(column.getValues(), new double[]{3, -1.5, 10, 0}));
        check("-0 is treated as 0", Double.doubleToLongBits(column.getValues()[3]) == Double.doubleToLongBits(0.0));

        Throwable textError = expectThrows("text column is rejected", DataException.class,
                () -> DataValidator.toNumericColumn("name", new String[]{"1", "Kasun"}));
        check("error message mentions non-numeric values",
                textError != null && textError.getMessage().contains("non-numeric values"));
        expectThrows("empty column is rejected", DataException.class,
                () -> DataValidator.toNumericColumn("empty", new String[]{"", " "}));
        expectThrows("overflowing number is rejected", DataException.class,
                () -> DataValidator.toNumericColumn("huge", new String[]{"1e400"}));
    }
}

package com.sortingapp.data;

/**
 * Thrown when a CSV file or a selected column cannot be used.
 * The message is written for the end user and is shown directly in the GUI.
 *
 * @author Member 1 - File Handling &amp; User Interface
 */
public class DataException extends Exception {

    /**
     * @param message user-friendly description of the problem
     */
    public DataException(String message) {
        super(message);
    }

    /**
     * @param message user-friendly description of the problem
     * @param cause   the underlying exception
     */
    public DataException(String message, Throwable cause) {
        super(message, cause);
    }
}

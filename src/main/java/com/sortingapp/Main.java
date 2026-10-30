package com.sortingapp;

import com.sortingapp.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point of the Sorting Algorithm Performance Analyzer.
 * <p>
 * Starts the Swing user interface on the Event Dispatch Thread (EDT),
 * which is the only thread allowed to create and update Swing components.
 */
public final class Main {

    private Main() {
        // Utility class - not meant to be instantiated
    }

    /**
     * Launches the application window.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Use the native look of the operating system (Windows, macOS, ...)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fall back to the default Swing look and feel
            }
            new MainFrame().setVisible(true);
        });
    }
}

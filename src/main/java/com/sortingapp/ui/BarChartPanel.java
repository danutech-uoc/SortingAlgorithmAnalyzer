package com.sortingapp.ui;

import com.sortingapp.performance.AlgorithmResult;
import com.sortingapp.performance.TimeFormatter;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A horizontal bar chart of the average execution time of each algorithm.
 * The fastest algorithm's bar is drawn in green.
 *
 * @author Member 1 - File Handling &amp; User Interface
 */
class BarChartPanel extends JPanel {

    private static final Color BAR_COLOR = new Color(66, 133, 244);
    private static final Color FASTEST_BAR_COLOR = new Color(46, 160, 67);
    private static final int PADDING = 14;
    private static final int MAX_BAR_HEIGHT = 34;

    private List<AlgorithmResult> results = Collections.emptyList();
    private AlgorithmResult fastest;

    BarChartPanel() {
        setPreferredSize(new Dimension(600, 240));
        setBackground(Color.WHITE);
        setForeground(Color.DARK_GRAY);
    }

    /**
     * @param results the results to draw
     * @param fastest the result to highlight
     */
    void setResults(List<AlgorithmResult> results, AlgorithmResult fastest) {
        this.results = new ArrayList<>(results);
        this.fastest = fastest;
        repaint();
    }

    /** Removes the chart. */
    void clear() {
        results = Collections.emptyList();
        fastest = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Insets insets = getInsets();
            int x = insets.left + PADDING;
            int y = insets.top + PADDING;
            int width = getWidth() - insets.left - insets.right - 2 * PADDING;
            int height = getHeight() - insets.top - insets.bottom - 2 * PADDING;
            FontMetrics metrics = g2.getFontMetrics();

            if (results.isEmpty()) {
                String message = "Run an analysis to see the comparison chart.";
                g2.setColor(Color.GRAY);
                g2.drawString(message, x + (width - metrics.stringWidth(message)) / 2, y + height / 2);
                return;
            }

            // Space for the algorithm names on the left and the time labels on the right
            int labelWidth = 0;
            int valueWidth = 0;
            long maxTime = 1;
            for (AlgorithmResult result : results) {
                labelWidth = Math.max(labelWidth, metrics.stringWidth(result.getAlgorithmName()));
                valueWidth = Math.max(valueWidth, metrics.stringWidth(TimeFormatter.format(result.getAverageNanos())));
                maxTime = Math.max(maxTime, result.getAverageNanos());
            }
            labelWidth += 12;
            valueWidth += 12;
            int barAreaWidth = Math.max(10, width - labelWidth - valueWidth);

            int rowHeight = height / results.size();
            int barHeight = Math.max(8, Math.min(MAX_BAR_HEIGHT, rowHeight - 10));

            for (int i = 0; i < results.size(); i++) {
                AlgorithmResult result = results.get(i);
                int barY = y + i * rowHeight + (rowHeight - barHeight) / 2;
                int textY = barY + (barHeight + metrics.getAscent() - metrics.getDescent()) / 2;
                int barLength = (int) Math.max(2, Math.round((double) result.getAverageNanos() / maxTime * barAreaWidth));

                g2.setColor(getForeground());
                g2.drawString(result.getAlgorithmName(), x, textY);

                g2.setColor(result == fastest ? FASTEST_BAR_COLOR : BAR_COLOR);
                g2.fillRoundRect(x + labelWidth, barY, barLength, barHeight, 6, 6);

                g2.setColor(getForeground());
                g2.drawString(TimeFormatter.format(result.getAverageNanos()), x + labelWidth + barLength + 8, textY);
            }
        } finally {
            g2.dispose();
        }
    }
}

package com.sortingapp.performance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.sortingapp.TestSupport.check;
import static com.sortingapp.TestSupport.expectThrows;

/**
 * Tests timing, verification and fastest-algorithm selection.
 */
public final class PerformanceAnalyzerTest {

    private PerformanceAnalyzerTest() {
    }

    public static void runAll() {
        Random random = new Random(7);
        double[] data = new double[5_000];
        for (int i = 0; i < data.length; i++) {
            data[i] = random.nextDouble() * 1000 - 500;
        }
        double[] original = data.clone();

        PerformanceAnalyzer analyzer = new PerformanceAnalyzer();
        List<String> messages = new ArrayList<>();
        AnalysisReport report = analyzer.analyze(data, 3, (done, total, message) -> messages.add(message));

        check("five algorithms are compared", report.getResults().size() == 5);
        check("input data is not modified", Arrays.equals(data, original));
        check("all algorithms sort correctly", report.allSortedCorrectly());

        double[] expected = original.clone();
        Arrays.sort(expected);
        check("sorted data is returned", Arrays.equals(report.getSortedData(), expected));
        check("data size", report.getDataSize() == data.length);

        AlgorithmResult fastest = report.getFastest();
        boolean fastestIsMinimum = true;
        for (AlgorithmResult result : report.getResults()) {
            check(result.getAlgorithmName() + ": 3 runs recorded", result.getRunCount() == 3);
            check(result.getAlgorithmName() + ": best <= average", result.getBestNanos() <= result.getAverageNanos());
            fastestIsMinimum &= fastest.getAverageNanos() <= result.getAverageNanos();
        }
        check("fastest has the lowest average", fastestIsMinimum);
        check("fastest has rank 1", report.getRank(fastest) == 1);
        check("progress is reported", !messages.isEmpty() && messages.get(messages.size() - 1).contains("complete"));

        expectThrows("empty data is rejected", IllegalArgumentException.class,
                () -> analyzer.analyze(new double[0], 1, null));
        expectThrows("zero runs are rejected", IllegalArgumentException.class,
                () -> analyzer.analyze(new double[]{1}, 0, null));

        check("format ns", TimeFormatter.format(500).equals("500 ns"));
        check("format µs", TimeFormatter.format(12_340).equals("12.34 µs"));
        check("format ms", TimeFormatter.format(3_250_000).equals("3.25 ms"));
        check("format s", TimeFormatter.format(1_502_000_000L).equals("1.502 s"));
    }
}

# Performance Results

These measurements were taken with this application's `PerformanceAnalyzer`, using
5 runs per algorithm. Each time is the average of the 5 runs, and the fastest time in
each row is in **bold**. Every algorithm sorted every dataset correctly; each output
was checked against Java's `Arrays.sort`.

**Test machine:** Intel Core i5-1135G7 @ 2.40 GHz, 24 GB RAM, Windows 11, Java 25.

## Results

| Dataset (file / column) | Insertion | Shell | Merge | Quick | Heap | Fastest |
|---|---|---|---|---|---|---|
| Small dataset, 30 rows of decimals (`students.csv` / `gpa`) | 5.40 µs | **4.58 µs** | 7.52 µs | 8.00 µs | 9.22 µs | Shell Sort |
| 10,000 random integers (`large_10000.csv` / `value`) | 28.54 ms | 1.18 ms | 2.16 ms | **0.91 ms** | 1.25 ms | Quick Sort |
| 50,000 random integers (`large_50000.csv` / `value`) | 189.36 ms | 4.97 ms | 4.29 ms | **4.06 ms** | 4.89 ms | Quick Sort |
| 50,000 random decimals (`large_50000.csv` / `price`) | 185.01 ms | 5.10 ms | 4.74 ms | **4.12 ms** | 4.71 ms | Quick Sort |
| 50,000 values, only 5 distinct (`large_50000.csv` / `rating`) | 149.99 ms | **1.31 ms** | 2.43 ms | 2.10 ms | 2.59 ms | Shell Sort |
| 10,000 values, already sorted (`sorted_10000.csv` / `value`) | **9.60 µs** | 75.52 µs | 220.70 µs | 174.98 µs | 515.94 µs | Insertion Sort |
| 10,000 values, reverse sorted (`reverse_10000.csv` / `value`) | 14.11 ms | **110.58 µs** | 263.60 µs | 174.64 µs | 548.20 µs | Shell Sort |
| 7,766,111 Bitcoin timestamps, already sorted (Kaggle `btcusd_1-min_data.csv` / `Timestamp`, 3 runs) | **7.93 ms** | 71.08 ms | 221.48 ms | 202.73 ms | 619.41 ms | Insertion Sort |

The Bitcoin file came from Kaggle and is too large to include in the submission.

## Observations

1. **There is no single best algorithm.** The fastest one depends on how big the data is
   and what order it is already in.
2. **Quick Sort wins on random data.** It is O(n log n) on average, does few swaps and
   reads memory in order, which the CPU cache handles well.
3. **Insertion Sort is far slower on random data.** At 50,000 values it is about 45 times
   slower than the others, because it is O(n²): when the data grows 5 times
   (10,000 → 50,000), its time grows about 6.6 times, while the O(n log n) algorithms
   grow only about 2–4 times.
4. **Insertion Sort wins on data that is already sorted.** Each value is compared once
   and nothing moves, which is its O(n) best case. This happens with real data such as
   timestamps, where it beat every other algorithm on 7.7 million values.
5. **Shell Sort is strong on reverse-sorted data and on duplicate-heavy data.** Its early
   rounds use large gaps, which move values long distances in a single step.
6. **Heap Sort is usually last of the O(n log n) algorithms.** It jumps around the array
   (from parent i to children 2i+1 and 2i+2), so the CPU cache helps it much less, and it
   gets no benefit from data that is already sorted.
7. **On tiny inputs (30 values), the differences are only a few microseconds.** The
   simple algorithms (Insertion Sort, Shell Sort) win because they have the least
   overhead; recursion and buffers cost more than they save at this size.

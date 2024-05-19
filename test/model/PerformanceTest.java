package model;

import java.util.Random;

public class PerformanceTest {
    public static final long CORNER_MASK = ~(1L | (1L << 7) | (1L << 56) | (1L << 63));

    public static void main(String[] args) {
        // Number of iterations for the test
        int iterations = 1000000;

        // Seed for random numbers
        Random random = new Random();

        // Perform test with original method
        long startTime = System.nanoTime();
        long originalResult = 0;
        for (int i = 0; i < iterations; i++) {
            long bitboard = random.nextLong();
            int offset = random.nextInt(37) - 18; // Limit offset to [-18, 18]
            long result;
            if (bitboard == 0) {
                result = 0; // Return early if the bitboard is empty
            } else {
                result = shiftOriginal(bitboard, offset);
            }
            originalResult += result;
        }
        long endTime = System.nanoTime();
        long originalTime = endTime - startTime;

        // Perform test with optimized method
        startTime = System.nanoTime();
        long optimizedResult = 0;
        for (int i = 0; i < iterations; i++) {
            long bitboard = random.nextLong();
            int offset = random.nextInt(37) - 18; // Limit offset to [-18, 18]
            long result;
            result = shiftOptimized(bitboard, offset);
            optimizedResult += result;
        }
        endTime = System.nanoTime();
        long optimizedTime = endTime - startTime;

        // Perform test with inline method
        startTime = System.nanoTime();
        long inlineResult = 0;
        for (int i = 0; i < iterations; i++) {
            long bitboard = random.nextLong();
            int offset = random.nextInt(37) - 18; // Limit offset to [-18, 18]
            long result;
            if (bitboard == 0) {
                result = 0; // Return early if the bitboard is empty
            } else {
                result = offset > 0 ? (bitboard << offset & CORNER_MASK) : (bitboard >>> -offset & CORNER_MASK);
            }
            inlineResult += result;
        }
        endTime = System.nanoTime();
        long inlineTime = endTime - startTime;

        // Output results
        System.out.println("Original method time: " + originalTime / 1e6 + " ms");
        System.out.println("Original method result: " + originalResult);
        System.out.println("Optimized method time: " + optimizedTime / 1e6 + " ms");
        System.out.println("Optimized method result: " + optimizedResult);
        System.out.println("Inline method time: " + inlineTime / 1e6 + " ms");
        System.out.println("Inline method result: " + inlineResult);
    }

    public static long shiftOriginal(long bitboard, int offset) {
        return offset > 0 ? (bitboard << offset & CORNER_MASK) : (bitboard >>> -offset & CORNER_MASK);
    }

    public static long shiftOptimized(long bitboard, int offset) {
        if(bitboard == 0) return 0;
        return offset > 0 ? (bitboard << offset & CORNER_MASK) : (bitboard >>> -offset & CORNER_MASK);
    }
}

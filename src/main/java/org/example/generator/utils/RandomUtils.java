package org.example.generator.utils;

import org.example.generator.annotation.Range;

import java.util.Random;

public final class RandomUtils {

    public static final Random RANDOM = new Random(7);
    private static final double DEFAULT_FLOAT_SPAN = 1.0;

    private RandomUtils() {
    }

    public static int randomExponentialSize(int min, double scale) {
        return min + (int) (RANDOM.nextExponential() * scale);
    }

    public static int randomContainerSize(Range range, int defaultMin, double scale) {
        if (range == null) {
            return randomExponentialSize(defaultMin, scale);
        }

        return (int) randomIntegral(range, 0, Integer.MAX_VALUE);
    }

    public static long randomIntegral(Range range, long typeMin, long typeMax) {
        validate(range);

        long min = Math.max(typeMin, (long) Math.ceil(range.min()));
        long max = Math.min(typeMax, (long) Math.floor(range.max()));
        if (min > max) {
            throw new IllegalArgumentException(String.format("%s is over %s and %s", range, typeMin, typeMax));
        } else if (min == max) {
            return min;
        }

        return RANDOM.nextLong(min, max + (max == Long.MAX_VALUE ? 0 : 1));
    }

    public static double randomFloating(Range range) {
        validate(range);
        double min = range.min();
        double max = range.max();
        boolean hasMin = min != Double.NEGATIVE_INFINITY;
        boolean hasMax = max != Double.POSITIVE_INFINITY;
        if (!hasMin && !hasMax) {
            return RANDOM.nextDouble();
        } else if (!hasMin) {
            min = max - DEFAULT_FLOAT_SPAN;
        } else if (!hasMax) {
            max = min + DEFAULT_FLOAT_SPAN;
        }

        if (!Double.isFinite(min) || !Double.isFinite(max)) {
            throw new IllegalArgumentException(range + " must have finite bounds");
        }
        return min == max ? min : RANDOM.nextDouble(min, max);
    }

    private static void validate(Range range) {
        if (Double.isNaN(range.min()) || Double.isNaN(range.max()) || range.min() > range.max()) {
            throw new IllegalArgumentException("Invalid " + range);
        }
    }
}

package org.example.generator;

import java.util.Random;

public final class RandomUtils {

    public static final Random RANDOM = new Random(7);

    private RandomUtils() {
    }

    public static int randomExponentialSize(int min, double scale) {
        return min + (int) (RANDOM.nextExponential() * scale);
    }
}

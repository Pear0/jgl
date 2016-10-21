package jgl.math;

import java.util.ArrayList;

/**
 * Created by william on 10/19/16.
 */
public class MathUtils {

    private static final ArrayList<Double> factorialCache = new ArrayList<>();

    public static double factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Assertion n >= 0 failed");
        }

        if (n < factorialCache.size()) {
            return factorialCache.get(n);
        }
        synchronized (factorialCache) {
            if (n < factorialCache.size()) {
                return factorialCache.get(n);
            }

            for (int k = factorialCache.size(); k <= n; k++) {
                if (k <= 1) {
                    factorialCache.add(1.0);
                }else {
                    factorialCache.add(factorialCache.get(k - 1) * k);
                }
            }

            return factorialCache.get(n);
        }
    }

    public static double combination(int n, int k) {
        return factorial(n) / factorial(k) / factorial(n - k);
    }

}

package xyz.sadiulhakim.util;

import java.text.DecimalFormat;

public class NumberFormat {

    private static final DecimalFormat FORMATER = new DecimalFormat("#,###.##");

    private NumberFormat() {
    }

    public static String format(String pattern, double value) {
        if (pattern == null || pattern.isEmpty()) {
            return FORMATER.format(value);
        }

        DecimalFormat format = new DecimalFormat(pattern);
        return format.format(value);
    }

    public static String format(double value) {

        return FORMATER.format(value);
    }
}

package xyz.sadiulhakim.util;

public class MathUtil {
    private MathUtil() {
    }

    public static boolean isInt(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static int intValue(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Exception ex) {
            return 0;
        }
    }
}

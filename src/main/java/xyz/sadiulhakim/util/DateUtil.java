package xyz.sadiulhakim.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss a");

    private DateUtil() {
    }

    public static OffsetDateTime readableDate(long instant) {
        return Instant.ofEpochMilli(instant)
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime();
    }

    public static String format(String format, OffsetDateTime dateTime) {

        if (format == null || format.isEmpty()) {
            return FORMATTER.format(dateTime);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(dateTime);
    }

    public static String format(OffsetDateTime dateTime) {
        return FORMATTER.format(dateTime);
    }
}

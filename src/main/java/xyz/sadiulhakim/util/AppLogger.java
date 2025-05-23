package xyz.sadiulhakim.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class AppLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppLogger.class);

    private AppLogger() {
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public static void error(String message) {
        JOptionPane.showMessageDialog(null, message);
        LOGGER.error(message);
    }

    public static void warn(String message) {
        LOGGER.warn(message);
    }
}

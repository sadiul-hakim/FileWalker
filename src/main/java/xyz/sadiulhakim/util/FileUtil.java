package xyz.sadiulhakim.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {
    private FileUtil() {
    }

    public static void delete(String pathText) {
        Path path = Path.of(pathText);
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException e) {
                AppLogger.error("Could not delete " + e.getMessage());
            }
        }
    }

    public static void make(String pathText) {
        Path path = Path.of(pathText);
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                AppLogger.error(e.getMessage());
            }
        }
    }

    public static void makeDir(String pathText) {
        Path path = Path.of(pathText);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                AppLogger.error(e.getMessage());
            }
        }
    }
}
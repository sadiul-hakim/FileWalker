package xyz.sadiulhakim.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;
import java.util.concurrent.ExecutorService;

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

    public static boolean deleteFileRecursively(File folder, ExecutorService EXECUTOR) {
        if (!folder.exists()) {
            return false;
        }

        try {
            Stack<File> visitedFolders = new Stack<>();

            // We have to delete visited folders from the last
            // we can only delete empty folders
            visitedFolders.push(folder);

            Deque<File> deque = new ArrayDeque<>();
            deque.push(folder);

            while (!deque.isEmpty()) {
                File current = deque.pop();
                File[] files = current.listFiles();

                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            deque.push(file); // Process directories later
                            visitedFolders.push(file);
                        } else {
                            EXECUTOR.submit(() -> file.delete()); // Delete files asynchronously
                        }
                    }
                }

                // Delete the folder itself after processing its contents
                EXECUTOR.submit(() -> current.delete());
            }

            while (!visitedFolders.isEmpty()) {
                File f = visitedFolders.pop();
                f.delete();
            }

            return true;
        } catch (Exception ignore) {
            return false;
        }
    }
}
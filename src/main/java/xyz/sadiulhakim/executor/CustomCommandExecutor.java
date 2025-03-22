package xyz.sadiulhakim.executor;

import xyz.sadiulhakim.resource.Monitor;
import xyz.sadiulhakim.util.AppLogger;
import xyz.sadiulhakim.util.CommandUtil;
import xyz.sadiulhakim.util.FileUtil;
import xyz.sadiulhakim.util.ThreadUtil;
import xyz.sadiulhakim.watcher.FileWatcher;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CustomCommandExecutor {

    private static final Scanner INPUT = new Scanner(System.in);

    private CustomCommandExecutor() {
    }

    public static void makeCommand(String[] commands, List<File> visitedPaths) {
        String folderName = commands[2].trim();
        String fullPath = visitedPaths.getLast() + File.separator + folderName;
        FileUtil.make(fullPath);
    }

    public static void makeDirCommand(String[] commands, List<File> visitedPaths) {
        String filePath = commands[2].trim();
        String fullPath = visitedPaths.getLast() + File.separator + filePath;
        FileUtil.makeDir(fullPath);
    }

    public static void delCommand(String[] commands) {

        boolean forceDelete = commands[commands.length - 1].equals("-f");

        if (!forceDelete) {
            FileUtil.delete(commands[2]);
            return;
        }

        ProcessAccessor.clear();
        System.out.println("Are you sure you want to delete this?");
        System.out.println("1. Yes");
        System.out.println("2. NO");
        System.out.print(": ");

        int opt = INPUT.nextInt();
        if (opt != 1)
            return;

        ProcessAccessor.clear();
        File filePath = new File(commands[2]);
        boolean deleted;
        if (filePath.isFile()) {
            deleted = filePath.delete();
        } else {
            System.out.println("Please keep waiting this might take a while.......");
            ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
            deleted = FileUtil.deleteFileRecursively(filePath, EXECUTOR);
            EXECUTOR.shutdown();
            try {
                if (!EXECUTOR.awaitTermination(10, TimeUnit.SECONDS)) {
                    EXECUTOR.shutdownNow();
                }
            } catch (InterruptedException e) {
                EXECUTOR.shutdownNow();
                Thread.currentThread().interrupt();
            }

        }
        System.out.println(deleted ? "Successfully deleted!" : "Something went wrong!");
    }

    public static void appendCommand(String[] commands) {

        try {

            boolean clearFile = commands[commands.length - 1].equals("-r");

            String appendSign;
            if (clearFile) {
                appendSign = commands[commands.length - 3];
            } else {
                appendSign = commands[commands.length - 2];
            }

            if (!appendSign.equals(">")) {
                return;
            }

            int appendSignIndexOffset = clearFile ? 3 : 2;

            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < commands.length - appendSignIndexOffset; i++) {
                sb.append(commands[i]).append(" ");
            }

            int fileNameIndexOffset = clearFile ? 2 : 1;

            var filePath = Path.of(commands[commands.length - fileNameIndexOffset]);
            if (clearFile) {
                Files.writeString(filePath, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } else {
                Files.writeString(filePath, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        } catch (Exception ex) {
            AppLogger.error(ex.getMessage());
        }
    }

    public static void renameCommand(String[] commands, List<File> visitedPaths) {

        try {
            String source = commands[2];
            String targetFile = commands[3];
            String targetFullPath = visitedPaths.getLast() + File.separator + targetFile;

            Files.move(Path.of(source), Path.of(targetFullPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            AppLogger.error(ex.getMessage());
        }
    }

    public static void moveCommand(String[] commands) {

        try {
            String source = commands[2];
            String target = commands[3];

            String lastPath = source.substring(source.lastIndexOf(File.separator));
            target = target + lastPath;

            Path targetPath = Path.of(target);
            Files.move(Path.of(source), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            AppLogger.error(ex.getMessage());
        }
    }

    public static void copyCommand(String[] commands) {

        try {
            String source = commands[2];
            String target = commands[3];

            String lastPath = source.substring(source.lastIndexOf(File.separator));
            target = target + lastPath;

            Path targetPath = Path.of(target);
            Files.copy(Path.of(source), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            AppLogger.error(ex.getMessage());
        }
    }

    public static void monitorResource() {
        ThreadUtil.EXECUTOR.submit(() -> {
            Monitor monitor = new Monitor();
            monitor.setTitle("Resources in use");
            monitor.setVisible(true);
        });
    }

    public static void watchCommand(String[] commands) {
        String filePath = commands[2];
        Thread watcherThread = Thread.ofPlatform().start(() -> FileWatcher.watch(filePath));

        ProcessAccessor.clear();
        System.out.println("Watching " + filePath + " ......");
        while (true) {
            System.out.print(": ");
            String command = INPUT.nextLine();
            if (command.equalsIgnoreCase(CommandUtil.EXIT_COMMAND)) {
                watcherThread.interrupt();
                break;
            }
        }
    }
}

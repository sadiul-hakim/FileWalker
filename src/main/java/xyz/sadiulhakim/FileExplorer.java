package xyz.sadiulhakim;

import xyz.sadiulhakim.executor.CustomCommandExecutor;
import xyz.sadiulhakim.executor.ProcessAccessor;
import xyz.sadiulhakim.util.*;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import static xyz.sadiulhakim.util.CommandUtil.*;

public class FileExplorer {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final int FILE_NAME_LENGTH = 85;

    private FileExplorer() {
    }

    public static void explore() {
        File[] files = File.listRoots();
        walk(files, new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());
    }

    private static void walk(File[] files, List<File[]> track, List<File> visitedPaths) {

        try {
            printPaths(files, visitedPaths);
            String text = INPUT.nextLine();
            if (text.isEmpty() || text.equalsIgnoreCase(EXIT_COMMAND)) {
                INPUT.close();
                ThreadUtil.EXECUTOR.shutdown();
                System.exit(0);
                return;
            }

            String[] commands = text.split(" ");

            // If length is 1 that means it's probably a number for walking
            int index;
            if (commands.length != 1) {
                processCommand(commands, files, visitedPaths);

                if (visitedPaths.isEmpty()) {
                    walk(files, track, visitedPaths);
                } else {
                    walk(visitedPaths.getLast().listFiles(), track, visitedPaths);
                }
            }

            if (!MathUtil.isInt(text)) {
                ThreadUtil.EXECUTOR.submit(() -> ProcessAccessor.execute(text));
                if (visitedPaths.isEmpty()) {
                    walk(files, track, visitedPaths);
                } else {
                    walk(visitedPaths.getLast().listFiles(), track, visitedPaths);
                }
            }

            index = Integer.parseInt(text);

            while (index < 0 || index > files.length || (index == 0 && track.isEmpty())) {
                printPaths(files, visitedPaths);
                index = INPUT.nextInt();
            }

            if (index == 0) {
                File[] lastOne = track.getLast();
                track.remove(lastOne);
                visitedPaths.remove(visitedPaths.getLast());
                walk(lastOne, track, visitedPaths);
                return;
            }

            if (files[index - 1].isFile()) {
                String absolutePath = files[index - 1].getAbsolutePath();
                ThreadUtil.EXECUTOR.submit(() -> ProcessAccessor.explore(absolutePath));
                walk(files, track, visitedPaths);
            }

            visitedPaths.add(files[index - 1]);
            track.add(files);
            walk(files[index - 1].listFiles(), track, visitedPaths);
        } catch (Exception ex) {
            AppLogger.error(ex.getMessage());
        }
    }

    private static void processCommand(String[] commands, File[] files, List<File> visitedPaths) {
        try {

            // Replace wildcards
            preProcess(commands, files, visitedPaths);
            if (commands[0].equals(CUSTOM_COMMAND_SIGN)) {
                processCustomCommand(commands, visitedPaths);
                return;
            }

            // Or, Process system specific command
            ThreadUtil.EXECUTOR.submit(() -> ProcessAccessor.execute(commands));
        } catch (Exception ex) {
            AppLogger.error(ex.getMessage());
        }
    }

    private static void processCustomCommand(String[] commands, List<File> visitedPaths) {

        String rootCommand = commands[1].toLowerCase();
        switch (rootCommand) {
            case CUSTOM_COMMAND_MAKE -> CustomCommandExecutor.makeCommand(commands, visitedPaths);
            case CUSTOM_COMMAND_MAKE_DIR -> CustomCommandExecutor.makeDirCommand(commands, visitedPaths);
            case CUSTOM_COMMAND_DELETE -> CustomCommandExecutor.delCommand(commands);
            case CUSTOM_COMMAND_APPEND -> CustomCommandExecutor.appendCommand(commands);
            case CUSTOM_COMMAND_RENAME -> CustomCommandExecutor.renameCommand(commands, visitedPaths);
            case CUSTOM_COMMAND_MOVE -> CustomCommandExecutor.moveCommand(commands);
            case CUSTOM_COMMAND_COPY -> CustomCommandExecutor.copyCommand(commands);
            case CUSTOM_COMMAND_WATCH -> CustomCommandExecutor.watchCommand(commands);
            case CUSTOM_COMMAND_MONITOR_RESOURCE -> CustomCommandExecutor.monitorResource();
        }
    }

    private static void preProcess(String[] commands, File[] files, List<File> visitedPaths) {
        for (int i = 0; i < commands.length; i++) {

            // Replace path index wildcard
            if (commands[i].startsWith("[") && commands[i].endsWith("]")) {
                String command = commands[i];
                String innerText = command.replace("[", "")
                        .replace("]", "");

                if (!MathUtil.isInt(innerText)) {
                    continue;
                }

                int fileIndex = MathUtil.intValue(innerText);
                commands[i] = files[fileIndex - 1].getAbsolutePath();
            }

            // Replace current directory wildcard
            else if (commands[i].contains("[/]")) {
                commands[i] = commands[i].replace("[/]", visitedPaths.getLast().getAbsolutePath());
            }
        }
    }

    private static void printPaths(File[] files, List<File> visitedPaths) {
        ProcessAccessor.clear();
        System.out.println("+----------File Walker----------+");

        if (!visitedPaths.isEmpty()) {
            System.out.println("> " + visitedPaths.getLast().getAbsolutePath());
        }
        for (int i = 0; i < files.length; i++) {
            printFileInfo(files[i], i);
        }

        System.out.print(": ");
    }

    private static void printFileInfo(File file, int i) {

        String path = file.getAbsolutePath();
        String finalPath = (path.length() > FILE_NAME_LENGTH) ?
                path.substring(0, FILE_NAME_LENGTH) :
                String.format("%-" + FILE_NAME_LENGTH + "s", path);
        String index = i < 9 ? "0" + (i + 1) : (i + 1) + "";
        System.out.println(index + ". " +
                finalPath + " ".repeat(10) +
                getFileDate(file) + " ".repeat(5) +
                getFileLength(file) + " MB");
    }

    private static String getFileDate(File file) {
        OffsetDateTime dateTime = DateUtil.readableDate(file.lastModified());
        return DateUtil.format(dateTime);
    }

    private static String getFileLength(File file) {
        double length = (file.length() / (1024.0 * 1024.0));
        return NumberFormat.format(length);
    }
}

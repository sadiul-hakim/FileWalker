package xyz.sadiulhakim;

import xyz.sadiulhakim.util.AppLogger;
import xyz.sadiulhakim.util.MathUtil;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class FileExplorer {

    private static final Scanner INPUT = new Scanner(System.in);

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
            if (text.equalsIgnoreCase("q")) {
                return;
            }

            String[] commands = text.split(" ");

            // If length is 1 that means it's probably a number for walking
            int index;
            if (commands.length != 1) {
                processCommand(commands, files, visitedPaths);
                walk(visitedPaths.getLast().listFiles(), track, visitedPaths);
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
                ProcessAccessor.explore(files[index - 1].getAbsolutePath());
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

            // Replace file index with file path
            for (int i = 0; i < commands.length; i++) {
                if (commands[i].startsWith("[") && commands[i].endsWith("]")) {
                    String command = commands[i];
                    String innerText = command.replace("[", "")
                            .replace("]", "");
                    int fileIndex = MathUtil.intValue(innerText);
                    commands[i] = files[fileIndex - 1].getAbsolutePath();
                } else if (commands[i].contains("[/]")) {
                    commands[i] = commands[i].replace("[/]", visitedPaths.getLast().getAbsolutePath());
                }
            }

            String result = ProcessAccessor.execute(commands);
            System.out.println(result);
        } catch (Exception ex) {
            AppLogger.error(ex.getMessage());
        }
    }

    private static void printPaths(File[] files, List<File> visitedPaths) {
        ProcessAccessor.clear();
        System.out.println("+----------File Walker----------+");

        if (!visitedPaths.isEmpty()) {
            System.out.println("> " + visitedPaths.getLast().getAbsolutePath());
        }
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getAbsolutePath());
        }

        System.out.print(": ");
    }
}

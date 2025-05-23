package xyz.sadiulhakim.executor;

import xyz.sadiulhakim.util.AppLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessAccessor {

    private static final List<String> CLEAR_COMMAND_ON_WINDOWS = List.of("cmd", "/c", "cls");
    private static final List<String> CLEAR_COMMAND_ON_OTHERS = List.of("clear");
    private static final List<String> PRE_COMMAND = List.of("cmd", "/c");
    private static final String SYSTEM = System.getProperty("os.name").toLowerCase();

    private ProcessAccessor() {
    }

    public static void execute(String... commands) {
        try {
            ArrayList<String> commandList;
            if (SYSTEM.contains("win")) {
                commandList = new ArrayList<>(PRE_COMMAND);
            } else {
                commandList = new ArrayList<>();
            }
            commandList.addAll(Arrays.asList(commands));
            ProcessBuilder builder = new ProcessBuilder(commandList);
            Process process = builder.start();
            process.waitFor();
        } catch (Exception ex) {
            AppLogger.error(ex.getMessage());
        }
    }

    public static void explore(String filePath) {
        try {
            ArrayList<String> commandList = new ArrayList<>();
            if (SYSTEM.contains("win")) {
                commandList = new ArrayList<>(PRE_COMMAND);

                if (new File(filePath).isDirectory()) {
                    commandList.add("explorer");
                }
            }

            commandList.add(filePath);
            new ProcessBuilder(commandList).inheritIO().start().waitFor();
        } catch (Exception ex) {
            AppLogger.error(ex.getMessage());
        }
    }

    public static void clear() {

        try {
            if (SYSTEM.contains("win")) {
                new ProcessBuilder(CLEAR_COMMAND_ON_WINDOWS).inheritIO().start().waitFor();
            } else {
                new ProcessBuilder(CLEAR_COMMAND_ON_OTHERS).inheritIO().start().waitFor();
            }
        } catch (Exception ex) {
            AppLogger.error(ex.getMessage());
        }
    }
}

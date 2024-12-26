package xyz.sadiulhakim;

import xyz.sadiulhakim.util.AppLogger;

import java.io.BufferedReader;
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

    public static String execute(String... commands) {
        try {
            ArrayList<String> commandList = new ArrayList<>(PRE_COMMAND);
            commandList.addAll(Arrays.asList(commands));

            ProcessBuilder builder = new ProcessBuilder(commandList);
            Process process = builder.start();
            process.waitFor();

            long pid = process.pid();

            try (BufferedReader reader = process.inputReader()) {
                return reader.lines().reduce("pid " + pid, (a, b) -> a.concat("\n").concat(b));
            }
        } catch (Exception ex) {
            AppLogger.error(ex.getMessage());
            return "";
        }
    }

    public static void explore(String filePath) {
        try {
            ArrayList<String> commandList = new ArrayList<>(PRE_COMMAND);
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

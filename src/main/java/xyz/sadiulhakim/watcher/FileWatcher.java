package xyz.sadiulhakim.watcher;

import xyz.sadiulhakim.util.AppLogger;

import java.nio.file.*;

public class FileWatcher {

    private FileWatcher() {
    }

    public static void watch(String path) {
        try (var service = FileSystems.getDefault().newWatchService()) {
            Path filePath = Path.of(path);
            filePath.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {

                if (Thread.interrupted()) {
                    break;
                }

                WatchKey key;
                try {
                    key = service.take(); // Blocks until events are available
                } catch (InterruptedException ex) {
                    AppLogger.error("Watch service interrupted");
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path filename = (Path) event.context();
                    System.out.println(kind.name() + ": " + filename);
                }

                boolean valid = key.reset();
                if (!valid) {
                    AppLogger.error("WatchKey no longer valid");
                    break;
                }
            }

        } catch (Exception ex) {
            AppLogger.error(ex.getMessage());
        }
    }
}

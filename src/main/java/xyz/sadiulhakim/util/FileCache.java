package xyz.sadiulhakim.util;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FileCache {
    private static final Map<String, Set<String>> FILE_CACHE = new ConcurrentHashMap<>();

    public static void put(String filePath, Set<String> files) {

        if (files == null || files.isEmpty())
            return;
        FILE_CACHE.put(filePath, files);
    }

    public static Set<String> get(String filePath) {
        return FILE_CACHE.getOrDefault(filePath, new HashSet<>());
    }
}

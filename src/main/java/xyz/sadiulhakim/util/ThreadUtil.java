package xyz.sadiulhakim.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ThreadUtil {
    public static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
}

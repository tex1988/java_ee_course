/*
 * https://www.baeldung.com/thread-pool-java-and-guava
 */
package com.gitlab.hillel.dnepr.java.ee.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public final class ThreadUtils {
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final long SHUTDOWN_TIME = 3;
    private static final int THREAD_COUNT = RUNTIME.availableProcessors() + 10;
    private static final ExecutorService executorService;

    static {
        executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        RUNTIME.addShutdownHook(new Thread() {
            @Override
            public void run() {
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(SHUTDOWN_TIME, TimeUnit.SECONDS)) {
                        LOGGER.error("Executor did not terminate in the specified time.");
                        final List<Runnable> droppedTasks = executorService.shutdownNow();
                        LOGGER.error("Executor was abruptly shut down. {} tasks will not be executed.", droppedTasks.size());
                    }
                } catch (InterruptedException e) {
                    LOGGER.error(StringUtils.EMPTY, e);
                }
            }
        });
        LOGGER.info("Thread pool executor is starter. Thread count: {}", THREAD_COUNT);
    }

    private ThreadUtils() {
    }

    public static void executeTask(Runnable task) {
        executeTask(task, 0, null);
    }

    public static void executeTask(Runnable task, long timeout) {
        executeTask(task, timeout, null);
    }

    public static void executeTask(Runnable task, long timeout, Consumer<Throwable> errorHandler) {
        final Consumer<Throwable> errorProcessing = Optional
                .ofNullable(errorHandler)
                .orElseGet(() -> e -> LOGGER.error(StringUtils.EMPTY, e));
        final Callable<Object> callable = () -> {
            try {
                task.run();
            } catch (Exception e) {
                errorProcessing.accept(e);
            }
            return null;
        };
        final List<Callable<Object>> callableList = Collections.singletonList(callable);
        try {
            if (timeout == 0) {
                executorService.invokeAll(callableList);
            } else {
                executorService.invokeAll(callableList, timeout, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            errorProcessing.accept(e);
        }
    }

    public static int getThreadCount() {
        return THREAD_COUNT;
    }
}
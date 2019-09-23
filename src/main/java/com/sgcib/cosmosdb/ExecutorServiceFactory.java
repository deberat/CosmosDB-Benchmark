package com.sgcib.cosmosdb;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

import static java.lang.String.format;

/**
 * Default factory for executor thread
 */
@Slf4j
public class ExecutorServiceFactory implements ThreadFactory {

    private static final String INJECTOR_EXECUTOR = "injector-executor";
    private static final Long DEFAULT_KEEP_ALIVE_IN_SEC = 36L;
    private static  final Thread.UncaughtExceptionHandler UNCAUGHT_EXCEPTION_HANDLER = (t, e) -> {
        log.error("Uncaught asynchronous exception : " + e.getMessage(), e);
        e.printStackTrace();
    };
    private ExecutorServiceFactory() {
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(format("%s",INJECTOR_EXECUTOR));
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(UNCAUGHT_EXCEPTION_HANDLER);
        return thread;
    }

    public static ThreadPoolExecutor create(int minThreads, int maxThreads, long threadKeepAlive) {
        return new ThreadPoolExecutor(minThreads, maxThreads, threadKeepAlive, TimeUnit.SECONDS,
                new SynchronousQueue<>(), new ExecutorServiceFactory());
    }
}
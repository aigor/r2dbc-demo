package org.aigor.r2dbc.demo.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public final class AppSchedulers {
    private AppSchedulers() { }

    public static ThreadPoolExecutor newExecutor(String prefix, int nThreads) {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads, new ThreadFactory() {
                private final AtomicInteger id = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    var thread = new Thread(r);
                    thread.setName(prefix + "-" + id.incrementAndGet());
                    thread.setDaemon(true);
                    return thread;
                }
            });
    }
}

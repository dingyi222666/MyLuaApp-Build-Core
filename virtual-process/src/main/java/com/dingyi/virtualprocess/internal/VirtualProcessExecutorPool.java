package com.dingyi.virtualprocess.internal;

import com.dingyi.virtualprocess.VirtualProcessExecutor;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualProcessExecutorPool {

    private static VirtualProcessExecutorPool instance;

    private VirtualProcessExecutorPool() {
    }

    public synchronized static VirtualProcessExecutorPool getInstance() {
        synchronized (VirtualProcessExecutorPool.class) {
            if (instance == null) {
                instance = new VirtualProcessExecutorPool();
            }

        }
        return instance;
    }


    private AtomicInteger threadAtomic;


    private Timer checkExecutorPoolTimer;

    private ExecutorService executorPool;

    private void createExecutorPool() {
        executorPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);
        threadAtomic = new AtomicInteger();

        checkExecutorPoolTimer = new Timer("ExecutorService-PoolCheck");

    }


    boolean canCloseExecutorPool() {
        return threadAtomic.get() >= ((ThreadPoolExecutor) executorPool).getPoolSize();
    }

    public void closeExecutorPool() {
        executorPool.shutdown();
        executorPool = null;

        checkExecutorPoolTimer.cancel();
        checkExecutorPoolTimer = null;
        threadAtomic.set(0);
    }

    public synchronized void execExecutor(VirtualProcessExecutor executor) {
        if (executorPool == null) {
            createExecutorPool();
        }
        executorPool.submit(executor);
        if (threadAtomic.get() == 0) {
            checkExecutorPoolTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (canCloseExecutorPool()) {
                        closeExecutorPool();
                    }
                }
            }, 1000, 1000 * 60);
        }
        threadAtomic.incrementAndGet();
    }
}

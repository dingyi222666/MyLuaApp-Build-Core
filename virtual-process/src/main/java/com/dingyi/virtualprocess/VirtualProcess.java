package com.dingyi.virtualprocess;

import com.dingyi.virtualprocess.internal.VirtualProcessExecutorPool;

import java.util.concurrent.CountDownLatch;

public class VirtualProcess {

    private VirtualProcessExecutor executor;

    private int processId;

    private Integer exitValue = null;

    private VirtualProcessGroup processGroup = VirtualProcessGroup.DEFAULT_PROCESS_GROUP;

    private final CountDownLatch waitForDownLatch = new CountDownLatch(2);

    private static int nextProcessId = 1145;

    boolean isStart() {
        return waitForDownLatch.getCount() < 2;
    }

    public VirtualProcess(VirtualProcessExecutor executor) {
        this.executor = executor;
        this.processId = generateProcessId();
    }

    public VirtualProcess() {
        this(null);
    }

    private static int generateProcessId() {
        return ++nextProcessId;
    }

    public void setExecutor(VirtualProcessExecutor executor) {
        this.executor = executor;
    }

    public void start() {
        processGroup.addProcess(this);
        waitForDownLatch.countDown();
        VirtualProcessExecutorPool.getInstance().execExecutor(executor);
    }


    public int getProcessId() {
        return processId;
    }


    public int waitFor() {
        try {
            waitForDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return exitValue;
    }

    public int exitValue() {
        return exitValue;
    }


    void exitValue(int exitValue) {
        waitForDownLatch.countDown();
        this.exitValue = exitValue;
    }
}

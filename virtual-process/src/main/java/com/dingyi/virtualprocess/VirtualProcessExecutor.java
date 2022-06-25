package com.dingyi.virtualprocess;


public abstract class VirtualProcessExecutor implements Runnable {


    final VirtualProcess process;


    public VirtualProcessExecutor(VirtualProcess process) {
        this.process = process;
    }

    @Override
    public void run() {
        int exitValue = runInProcess();
        exitValue(exitValue);
    }


    abstract public int runInProcess();


    void exitValue(int exitValue) {
        process.exitValue(exitValue);
    }

}

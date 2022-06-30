package com.dingyi.terminal.virtualprocess;

import android.util.ArrayMap;
import android.util.SparseArray;

import java.io.IOException;

public class VirtualProcessService {

    private static VirtualProcessService INSTANCE;

    private VirtualProcessService() {
    }

    public static VirtualProcessService getInstance() {
        synchronized (VirtualProcessService.class) {
            if (INSTANCE == null) {
                INSTANCE = new VirtualProcessService();
            }
        }
        return INSTANCE;
    }

    private ArrayMap<Long,Integer> mTidToPidMap = new ArrayMap<>();

    private SparseArray<VirtualProcess> mProcesses = new SparseArray<>();

    private int currentProcessId = 114514;

    private int generateProcessId() {
        return ++currentProcessId;
    }

    public static VirtualProcess createProcess(String cmd) {
        return getInstance().createProcessFor(new VirtualProcess(cmd));
    }


    public static VirtualProcess createProcess(String cmd, String... args) {
        return getInstance().createProcessFor(new VirtualProcess(cmd, args));
    }

    public static VirtualProcess createProcess(String cmd,
                                               String cwd,
                                               String[] args,
                                               String[] envVars) {
        return getInstance().createProcessFor(new VirtualProcess(cmd, cwd, args, envVars));
    }

    public VirtualProcess createProcessFor(VirtualProcess process) {
        process.setProcessId(generateProcessId());
        mProcesses.put(process.getProcessId(), process);
        new Thread("VirtualProcess-Listener") {
            @Override
            public void run() {
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    remove(process);
                }
                remove(process);
            }
            void remove(VirtualProcess process) {
                mProcesses.remove(process.getProcessId());
            }
        }.start();
        return process;
    }


    public static VirtualProcess getProcess(int processId) {
        return getInstance().getProcessFor(processId);
    }

    void putProcessWithThread(Thread thread,int processId) {
        mTidToPidMap.put(thread.getId(), processId);
    }

    int deleteProcessWithThread(Thread thread) {
        int processId = mTidToPidMap
                .getOrDefault(thread.getId(),-1);
        if (processId == -1) {
            return -1;
        }
        mTidToPidMap.remove(thread.getId());
        return processId;
    }

    public synchronized VirtualProcess getProcessFor(int processId) {
        return mProcesses.get(processId);
    }


    public synchronized static VirtualProcess currentProcess() {
        return getInstance().getCurrentProcessFor();
    }

    private VirtualProcess getCurrentProcessFor() {
        int processId = mTidToPidMap.getOrDefault(Thread.currentThread().getId(),-1);
        if (processId == -1) {
            return null;
        }
        return mProcesses.get(processId);
    }

    public static void waitFor(int processId) {
        try {
            getInstance().waitForImpl(processId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitForImpl(int processId) throws InterruptedException {
        VirtualProcess process = getProcessFor(processId);
        if (process != null) {
            process.waitFor();
        }
    }

    public static boolean killProcess(int processId) throws Exception {
        return getInstance().killProcessFor(processId);
    }

    public boolean killProcessFor(int processId) throws Exception {
        VirtualProcess process = mProcesses.get(processId);
        try {
            process.killProcess();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}



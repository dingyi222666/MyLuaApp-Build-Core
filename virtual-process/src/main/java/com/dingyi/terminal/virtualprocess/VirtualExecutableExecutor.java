package com.dingyi.terminal.virtualprocess;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

class VirtualExecutableExecutor implements Runnable {

    private final VirtualExecutable mBinary;
    private final VirtualProcess mProcess;


    VirtualExecutableExecutor(VirtualExecutable binary, VirtualProcess process) {
        mBinary = binary;
        mProcess = process;
    }


    @Override
    public void run() {
        int ret;

        VirtualProcessService
                .getInstance()
                .putProcessWithThread(Thread.currentThread(), mProcess.getProcessId());

        try {
            ret = mBinary.start(mProcess.args);
        } catch (Exception e) {
            ret = -1;
            try {
                mProcess
                        .getProcessEnvironment()
                        .processErrorStream
                        .write(dumpException(e));
            } catch (IOException ex) {
                // ignore
            }
            exit(ret);
            return;
        }


        exit(ret);


    }

    public void exit(int ret) {

        try {
            mProcess.getProcessEnvironment()
                    .destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }

        VirtualProcessService
                .getInstance()
                .deleteProcessWithThread(Thread.currentThread());

        mProcess.getProcessEnvironment()
                .exit(ret);

    }


    private byte[] dumpException(Exception e) {
        return e.getMessage().getBytes();
    }

}

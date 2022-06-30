package org.gradle.internal.nativeintegration.console;

import com.dingyi.terminal.virtualprocess.VirtualProcess;
import com.dingyi.terminal.virtualprocess.VirtualProcessService;

public class VirtualProcessConsoleMetaData implements  ConsoleMetaData {

    private final VirtualProcess virtualProcess;

    public VirtualProcessConsoleMetaData() {
        this.virtualProcess = VirtualProcessService.currentProcess();
    }

    @Override
    public boolean isStdOut() {
        return true;
    }

    @Override
    public boolean isStdErr() {
        return true;
    }

    @Override
    public int getCols() {
        return virtualProcess.getProcessEnvironment().getTermiosSupport().getColumn();
    }

    @Override
    public int getRows() {
        return virtualProcess.getProcessEnvironment().getTermiosSupport().getRow();
    }

    @Override
    public boolean isWrapStreams() {
        return true;
    }
}

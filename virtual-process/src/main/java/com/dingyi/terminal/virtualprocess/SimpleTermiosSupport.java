package com.dingyi.terminal.virtualprocess;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//TODO:Termios Support
public class SimpleTermiosSupport {


    private final VirtualTerminalEnvironment terminalEnvironment;
    private final VirtualProcessEnvironment processEnvironment;
    //The column and row are only used for the terminal.
    private int column;

    private int row;

    public SimpleTermiosSupport(
            VirtualTerminalEnvironment terminalEnvironment,
            VirtualProcessEnvironment processEnvironment) {
        this.terminalEnvironment = terminalEnvironment;
        this.processEnvironment = processEnvironment;
    }

    void doWrapper() {
        //do nothing.
    }


    public synchronized void setColumn(int column) {
        synchronized (this) {
            this.column = column;
        }
    }

    public synchronized void setSize(int column, int row) {
        synchronized (this) {
            this.row = row;
            this.column = column;
        }
    }

    public synchronized void setRow(int row) {
        synchronized (this) {
            this.row = row;
        }
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }




}

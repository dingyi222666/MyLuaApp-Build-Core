package com.dingyi.terminal.virtualprocess;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

public class VirtualProcessEnvironment {

    InputStream processInputStream;
    OutputStream processOutputStream;

    OutputStream processErrorStream;

    CountDownLatch processStartLatch = new CountDownLatch(1);

    SimpleTermiosSupport termiosSupport;

    private String[] args;

    private String cwd;

    private Map<String, String> env = new HashMap<>();

    private ReentrantLock envLock = new ReentrantLock();

    Integer exitValue = null;


    public VirtualProcessEnvironment(InputStream processInputStream, OutputStream processOutputStream, OutputStream processErrorStream) {
        this.processInputStream = processInputStream;

        if (processErrorStream == null) {
            processErrorStream = System.err;
        }

        this.processOutputStream = processOutputStream;
        this.processErrorStream = processErrorStream;


    }

    void setArguments(String[] args) {
        this.args = args;
    }

    public String[] getArguments() {
        return args;
    }


    public SimpleTermiosSupport getTermiosSupport() {
        return termiosSupport;
    }

    public boolean putEnvironment(String key, String value) {
        envLock.lock();
        try {
            env.put(key, value);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            envLock.unlock();
        }
    }

    public boolean putEnvironments(Map<String, String> env) {
        envLock.lock();
        try {
            this.env.putAll(env);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            envLock.unlock();
        }
    }

    public String getEnvironment(String key) {
        envLock.lock();
        try {
            return env.get(key);
        } catch (Exception e) {
            return null;
        } finally {
            envLock.unlock();
        }
    }

    public String removeEnvironment(String key) {
        envLock.lock();
        try {
            return env.remove(key);
        } catch (Exception e) {
            return null;
        } finally {
            envLock.unlock();
        }
    }

    public Map<String, String> getReadOnlyEnvironment() {
        envLock.lock();
        try {
            return new HashMap<>(env);
        } catch (Exception e) {
            return null;
        } finally {
            envLock.unlock();
        }
    }

    public VirtualProcessEnvironment(InputStream processInputStream) {
        this(processInputStream, System.out, System.err);
    }

    public VirtualProcessEnvironment() {
        this(System.in);
    }


    public Map<String, String> getEnv() {
        if (env == null) {
            env = new HashMap<>();
        }
        return env;
    }

    public void destroy() throws IOException {
        processInputStream.close();
        processOutputStream.close();
        processErrorStream.close();

    }

    public InputStream getInputStream() {
        return processInputStream;
    }

    public OutputStream getOutputStream() {
        return processOutputStream;
    }

    public OutputStream getErrorStream() {
        return processErrorStream;
    }


    public void print(String s) throws IOException {
        processOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
        flush();
    }

    public void println(String s) throws IOException {
        processOutputStream.write(s.getBytes(StandardCharsets.UTF_8));
        processOutputStream.write('\n');
        flush();
    }




    public void flush() throws IOException {
        processOutputStream.flush();
        processErrorStream.flush();
    }

    /**
     * This method only set the exit value of the process.
     * And not destroy the process.
     *
     * @param value
     */
    void exit(int value) {
        processStartLatch.countDown();
        exitValue = value;
    }

    public Integer exitValue() {
        return exitValue;
    }


    public void setCurrentWorkDir(String absolutePath) {
        cwd = absolutePath;
    }
    public String getCurrentWorkDir() {
        return cwd;
    }
}


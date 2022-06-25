package com.dingyi.virtualprocess;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author dingyi
 * A virtual environment.
 */
public class VirtualProcessEnvironment {

    private final Map<String, String> env = new HashMap<String, String>();

    private String cwd;

    private final ReentrantLock envLock = new ReentrantLock();

    public VirtualProcessEnvironment(String cwd) {
        this.cwd = cwd;
        env.putAll(System.getenv());
    }

    public VirtualProcessEnvironment(Map<String, String> env) {
        this.env.putAll(env);
        this.cwd = new File("").getAbsoluteFile().getAbsolutePath();
    }


    public void setEnvironment(String key, String value) {
        envLock.lock();
        try {
            env.put(key, value);
        } finally {
            envLock.unlock();
        }
    }

    public String getEnvironment(String key) {
        envLock.lock();
        try {
            return env.get(key);
        } finally {
            envLock.unlock();
        }
    }


    public String getCurrentWorkDir() {
        return cwd;
    }

    public void setCurrentWorkDir(String cwd) {
        this.cwd = cwd;
    }

}

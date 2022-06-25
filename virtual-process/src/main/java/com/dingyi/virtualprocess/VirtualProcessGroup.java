package com.dingyi.virtualprocess;

import java.util.HashMap;
import java.util.Map;

public class VirtualProcessGroup {

    private final Map<Integer, VirtualProcess> processes = new HashMap<>();

    private final String name;

    public VirtualProcessGroup() {
        this(generateNextProcessGroupName());
    }

    public VirtualProcessGroup(String name) {
        this.name = name;
    }

    private static int nextId = 0;

    static String generateNextProcessGroupName() {
        return "VirtualProcessGroup-" + nextId++;
    }


    public String getName() {
        return name;
    }

    void addProcess(VirtualProcess process) {
        processes.put(process.getProcessId(), process);
    }


    void removeProcess(VirtualProcess process) {
        processes.remove(process.getProcessId());
    }


    public static VirtualProcessGroup DEFAULT_PROCESS_GROUP = new VirtualProcessGroup("root");
}

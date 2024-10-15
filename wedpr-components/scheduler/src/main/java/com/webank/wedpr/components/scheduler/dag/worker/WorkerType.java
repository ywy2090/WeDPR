package com.webank.wedpr.components.scheduler.dag.worker;

public enum WorkerType {
    // specific job worker
    PSI("PSI"),
    MPC("MPC"),
    MODEL("MODEL");

    private final String value;

    WorkerType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

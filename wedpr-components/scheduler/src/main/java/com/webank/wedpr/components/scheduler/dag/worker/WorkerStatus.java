package com.webank.wedpr.components.scheduler.dag.worker;

public enum WorkerStatus {
    PENDING,
    RUNNING,
    FAILURE,
    KILLED,
    SUCCESS;
}

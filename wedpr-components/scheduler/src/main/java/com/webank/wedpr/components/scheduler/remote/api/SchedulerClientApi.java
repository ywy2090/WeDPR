package com.webank.wedpr.components.scheduler.remote.api;

import com.webank.wedpr.components.scheduler.local.executor.ExecuteResult;

public interface SchedulerClientApi {
    void postJob(String jobId, String request) throws Exception;

    void killJob(String jobId) throws Exception;

    ExecuteResult queryJob(String jobId) throws Exception;
}

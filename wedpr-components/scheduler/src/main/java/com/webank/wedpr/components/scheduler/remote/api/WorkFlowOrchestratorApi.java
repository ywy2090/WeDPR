package com.webank.wedpr.components.scheduler.remote.api;

import com.webank.wedpr.components.project.dao.JobDO;
import com.webank.wedpr.components.scheduler.remote.workflow.WorkFlow;

public interface WorkFlowOrchestratorApi {
    /**
     * build workflow by job
     *
     * @param jobDO
     * @return
     * @throws Exception
     */
    WorkFlow buildWorkFlow(JobDO jobDO) throws Exception;
}

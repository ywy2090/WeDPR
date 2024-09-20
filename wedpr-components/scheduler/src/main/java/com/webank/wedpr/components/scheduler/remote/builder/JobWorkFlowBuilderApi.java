package com.webank.wedpr.components.scheduler.remote.builder;

import com.webank.wedpr.components.project.dao.JobDO;
import com.webank.wedpr.components.scheduler.local.executor.Executor;
import com.webank.wedpr.components.scheduler.remote.workflow.WorkFlow;
import com.webank.wedpr.components.scheduler.remote.workflow.WorkFlowNode;

public interface JobWorkFlowBuilderApi extends Executor {
    void buildWorkFlow(JobDO jobDO, WorkFlow workflow) throws Exception;

    void buildWorkFlow(JobDO jobDO, WorkFlow workflow, WorkFlowNode upstream) throws Exception;
}

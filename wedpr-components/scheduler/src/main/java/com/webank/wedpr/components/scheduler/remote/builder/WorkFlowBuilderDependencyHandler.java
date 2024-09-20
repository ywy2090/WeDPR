package com.webank.wedpr.components.scheduler.remote.builder;

import com.webank.wedpr.components.project.dao.JobDO;
import com.webank.wedpr.components.scheduler.remote.workflow.WorkFlow;
import com.webank.wedpr.components.scheduler.remote.workflow.WorkFlowNode;

public interface WorkFlowBuilderDependencyHandler {
    void handleDependency(JobDO jobDO, WorkFlow workflow, WorkFlowNode workFlowNode)
            throws Exception;
}

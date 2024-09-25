package com.webank.wedpr.components.scheduler.remote.builder;

import com.webank.wedpr.components.project.dao.JobDO;
import com.webank.wedpr.components.scheduler.local.executor.ExecuteResult;
import com.webank.wedpr.components.scheduler.local.executor.Executor;
import com.webank.wedpr.components.scheduler.remote.workflow.WorkFlow;
import com.webank.wedpr.components.scheduler.remote.workflow.WorkFlowNode;
import com.webank.wedpr.core.utils.ObjectMapperFactory;
import java.util.List;

public class JobWorkFlowBuilderImpl implements JobWorkFlowBuilderApi {

    private final Executor executor;
    private final JobWorkFlowBuilderManager jobWorkflowBuilderManager;

    public JobWorkFlowBuilderImpl(
            Executor executor, JobWorkFlowBuilderManager jobWorkflowBuilderManager) {
        this.executor = executor;
        this.jobWorkflowBuilderManager = jobWorkflowBuilderManager;
    }

    @Override
    public void buildWorkFlow(JobDO jobDO, WorkFlow workflow) throws Exception {
        Object args = this.prepare(jobDO);

        WorkFlowNode workflowNode = addWorkFlowNode(workflow, jobDO.getJobType(), args);
        List<WorkFlowBuilderDependencyHandler> depsHandlers =
                jobWorkflowBuilderManager.getHandler(jobDO.getJobType());
        if (depsHandlers == null) {
            return;
        }

        for (WorkFlowBuilderDependencyHandler depsHandler : depsHandlers) {
            depsHandler.handleDependency(jobDO, workflow, workflowNode);
        }
    }

    @Override
    public void buildWorkFlow(JobDO jobDO, WorkFlow workflow, WorkFlowNode upstream)
            throws Exception {
        Object args = this.prepare(jobDO);
        WorkFlowNode workflowNode = addWorkFlowNode(workflow, jobDO.getJobType(), args);

        List<WorkFlowBuilderDependencyHandler> handlers =
                jobWorkflowBuilderManager.getHandler(jobDO.getJobType());
        if (handlers == null) {
            return;
        }

        for (WorkFlowBuilderDependencyHandler workFlowBuilderDependencyHandler : handlers) {
            workFlowBuilderDependencyHandler.handleDependency(jobDO, workflow, workflowNode);
        }
    }

    private WorkFlowNode addWorkFlowNode(WorkFlow workflow, String jobType, Object args)
            throws Exception {
        // args
        String argsAsString = ObjectMapperFactory.getObjectMapper().writeValueAsString(args);
        // workflow build
        return workflow.addWorkFlowNode(null, jobType, argsAsString);
    }

    @Override
    public Object prepare(JobDO jobDO) throws Exception {
        return executor.prepare(jobDO);
    }

    @Override
    public void execute(JobDO jobDO) throws Exception {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void kill(JobDO jobDO) throws Exception {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public ExecuteResult queryStatus(String jobID) throws Exception {
        throw new UnsupportedOperationException("Not supported");
    }
}

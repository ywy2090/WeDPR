package com.webank.wedpr.components.scheduler.remote;

import com.webank.wedpr.components.project.JobChecker;
import com.webank.wedpr.components.project.dao.JobDO;
import com.webank.wedpr.components.scheduler.local.executor.impl.ml.model.ModelJobParam;
import com.webank.wedpr.components.scheduler.local.executor.impl.ml.request.PreprocessingRequest;
import com.webank.wedpr.components.scheduler.local.executor.impl.model.FileMetaBuilder;
import com.webank.wedpr.components.scheduler.remote.api.WorkFlowOrchestratorApi;
import com.webank.wedpr.components.scheduler.remote.builder.JobWorkFlowBuilderManager;
import com.webank.wedpr.components.scheduler.remote.workflow.WorkFlow;
import com.webank.wedpr.core.protocol.JobType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkFlowOrchestrator implements WorkFlowOrchestratorApi {

    private static final Logger logger = LoggerFactory.getLogger(WorkFlowOrchestrator.class);

    private JobWorkFlowBuilderManager jobWorkflowBuilderManager = null;
    private FileMetaBuilder fileMetaBuilder = null;
    private JobChecker jobChecker = null;

    public JobChecker getJobChecker() {
        return jobChecker;
    }

    public void setJobChecker(JobChecker jobChecker) {
        this.jobChecker = jobChecker;
    }

    public WorkFlowOrchestrator(
            JobWorkFlowBuilderManager jobWorkflowBuilderManager,
            FileMetaBuilder fileMetaBuilder,
            JobChecker jobChecker) {
        this.jobWorkflowBuilderManager = jobWorkflowBuilderManager;
        this.fileMetaBuilder = fileMetaBuilder;
        this.jobChecker = jobChecker;
    }

    /**
     * build workflow by job
     *
     * @param jobDO
     * @return
     * @throws Exception
     */
    @Override
    public WorkFlow buildWorkFlow(JobDO jobDO) throws Exception {
        String jobId = jobDO.getId();
        String jobType = jobDO.getJobType();

        logger.info("build workflow, jobId: {}, jobType: {}", jobId, jobType);

        WorkFlow workflow = new WorkFlow(jobId);

        if (jobDO.getOriginalJobType() == null) {
            jobDO.setOriginalJobType(jobDO.getType());
        }

        jobDO.setOriginalJobType(jobDO.getType());
        if (JobType.isPSIJob(jobType)) {
            buildPSIWorkFlow(jobDO, workflow);
        } else if (JobType.isMPCJob(jobType)) {
            buildMPCWorkFlow(jobDO, workflow);
        } else if (JobType.isXGBJob(jobType)) {
            buildXGBWorkFlow(jobDO, workflow);
        } else {
            throw new UnsupportedOperationException("Unsupported job type: " + jobType);
        }

        return workflow;
    }

    // PSI
    public void buildPSIWorkFlow(JobDO jobDO, WorkFlow workflow) throws Exception {
        String jobType = jobDO.getJobType();
        //        logger.info("build PSI workflow, jobId: {}", jobDO.getId());
        jobWorkflowBuilderManager.getJobWorkFlowBuilder(jobType).buildWorkFlow(jobDO, workflow);
    }

    // MPC
    public void buildMPCWorkFlow(JobDO jobDO, WorkFlow workFlow) {
        // TODO: impl mpc
    }

    // ML
    public void buildXGBWorkFlow(JobDO jobDO, WorkFlow workflow) throws Exception {

        ModelJobParam modelJobParam = (ModelJobParam) jobChecker.checkAndParseParam(jobDO);
        jobDO.setJobParam(modelJobParam);

        logger.info(
                "build xgb workflow, usePSI: {}, jobId: {}", modelJobParam.usePSI(), jobDO.getId());

        if (modelJobParam.usePSI()) {
            // ml-psi
            jobDO.setJobType(JobType.ML_PSI.getType());
        } else {
            // ml-preprocessing
            PreprocessingRequest preprocessingRequest =
                    modelJobParam.toPreprocessingRequest(fileMetaBuilder);
            jobDO.setJobRequest(preprocessingRequest);
            jobDO.setJobType(JobType.MLPreprocessing.getType());
        }

        jobWorkflowBuilderManager
                .getJobWorkFlowBuilder(jobDO.getJobType())
                .buildWorkFlow(jobDO, workflow);
    }
}

package com.webank.wedpr.components.scheduler.remote.builder;

import com.webank.wedpr.components.project.JobChecker;
import com.webank.wedpr.components.project.dao.JobDO;
import com.webank.wedpr.components.scheduler.local.executor.impl.ml.MLExecutor;
import com.webank.wedpr.components.scheduler.local.executor.impl.ml.model.ModelJobParam;
import com.webank.wedpr.components.scheduler.local.executor.impl.ml.request.FeatureEngineeringRequest;
import com.webank.wedpr.components.scheduler.local.executor.impl.ml.request.PreprocessingRequest;
import com.webank.wedpr.components.scheduler.local.executor.impl.ml.request.XGBJobRequest;
import com.webank.wedpr.components.scheduler.local.executor.impl.model.FileMetaBuilder;
import com.webank.wedpr.components.scheduler.local.executor.impl.psi.MLPSIExecutor;
import com.webank.wedpr.components.scheduler.local.executor.impl.psi.PSIExecutor;
import com.webank.wedpr.components.scheduler.remote.workflow.WorkFlow;
import com.webank.wedpr.components.scheduler.remote.workflow.WorkFlowNode;
import com.webank.wedpr.components.storage.api.FileStorageInterface;
import com.webank.wedpr.core.protocol.JobType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobWorkFlowBuilderManager {

    private static final Logger logger = LoggerFactory.getLogger(JobWorkFlowBuilderManager.class);

    private final FileMetaBuilder fileMetaBuilder;
    private final FileStorageInterface storage;
    private final JobChecker jobChecker;

    protected Map<String, JobWorkFlowBuilderApi> jobWorkFlowBuilderMap = new ConcurrentHashMap<>();

    protected Map<String, List<WorkFlowBuilderDependencyHandler>> jobWorkFlowDependencyHandlerMap =
            new ConcurrentHashMap<>();

    public JobWorkFlowBuilderManager(
            FileMetaBuilder fileMetaBuilder, FileStorageInterface storage, JobChecker jobChecker) {
        this.fileMetaBuilder = fileMetaBuilder;
        this.storage = storage;
        this.jobChecker = jobChecker;
    }

    public void initialize() {
        initializeJobWorkFlowBuilderManager();
        initializeJobWorkFlowDependencyHandler();
    }

    public void initializeJobWorkFlowBuilderManager() {
        logger.info("register PSI workflow builder success");
        registerJobWorkFlowBuilder(
                JobType.PSI.getType(),
                new JobWorkFlowBuilderImpl(
                        new PSIExecutor(storage, fileMetaBuilder, jobChecker), this));

        logger.info("register ML PSI workflow builder success");
        registerJobWorkFlowBuilder(
                JobType.ML_PSI.getType(),
                new JobWorkFlowBuilderImpl(new MLPSIExecutor(storage, fileMetaBuilder), this));

        logger.info("register ML workflow builder success");
        registerJobWorkFlowBuilder(
                JobType.MLPreprocessing.getType(),
                new JobWorkFlowBuilderImpl(new MLExecutor(), this));
        registerJobWorkFlowBuilder(
                JobType.FeatureEngineer.getType(),
                new JobWorkFlowBuilderImpl(new MLExecutor(), this));
        registerJobWorkFlowBuilder(
                JobType.XGB_TRAIN.getType(), new JobWorkFlowBuilderImpl(new MLExecutor(), this));
        registerJobWorkFlowBuilder(
                JobType.XGB_PREDICT.getType(), new JobWorkFlowBuilderImpl(new MLExecutor(), this));

        logger.info("register job workflow builder end");
    }

    public void initializeJobWorkFlowDependencyHandler() {
        registerJobWorkFlowDependencyHandler(
                JobType.ML_PSI.getType(),
                (jobDO, workflow, upstream) -> {
                    // preprocessing
                    ModelJobParam modelJobParam = (ModelJobParam) jobDO.getJobParam();
                    PreprocessingRequest preprocessingRequest =
                            modelJobParam.toPreprocessingRequest(fileMetaBuilder);
                    jobDO.setJobRequest(preprocessingRequest);
                    jobDO.setJobType(JobType.MLPreprocessing.getType());

                    JobWorkFlowBuilderImpl jobWorkFlowBuilder =
                            new JobWorkFlowBuilderImpl(
                                    getJobWorkFlowBuilder(jobDO.getJobType()), this);
                    jobWorkFlowBuilder.buildWorkFlow(jobDO, workflow, upstream);
                });

        registerJobWorkFlowDependencyHandler(
                JobType.MLPreprocessing.getType(),
                (jobDO, workflow, upstream) -> {
                    if (jobDO.getOriginalJobType() == null) {
                        return;
                    }
                    ModelJobParam modelJobParam = (ModelJobParam) jobDO.getJobParam();
                    jobDO.setJobType(JobType.FeatureEngineer.getType());
                    // try to execute FeatureEngineer job
                    if (executeFeatureEngineerJob(jobDO, modelJobParam, workflow, upstream)) {
                        return;
                    }

                    // execute xgb request
                    jobDO.setType(jobDO.getOriginalJobType());
                    if (executeXGBJob(jobDO, modelJobParam, workflow, upstream)) {
                        return;
                    }
                });

        registerJobWorkFlowDependencyHandler(
                JobType.MLPreprocessing.getType(),
                (jobDO, workflow, upstream) -> {
                    jobDO.setType(jobDO.getOriginalJobType());
                    ModelJobParam modelJobParam = (ModelJobParam) jobDO.getJobParam();

                    executeXGBJob(jobDO, modelJobParam, workflow, upstream);
                });
    }

    private boolean executeFeatureEngineerJob(
            JobDO jobDO, ModelJobParam modelJobParam, WorkFlow workflow, WorkFlowNode upstream)
            throws Exception {
        // try to execute FeatureEngineer job
        FeatureEngineeringRequest featureEngineeringRequest =
                modelJobParam.toFeatureEngineerRequest();
        if (featureEngineeringRequest == null) {
            return false;
        }

        jobDO.setJobRequest(featureEngineeringRequest);

        JobWorkFlowBuilderImpl jobWorkFlowBuilder =
                new JobWorkFlowBuilderImpl(getJobWorkFlowBuilder(jobDO.getJobType()), this);
        jobWorkFlowBuilder.buildWorkFlow(jobDO, workflow, upstream);
        return true;
    }

    private boolean executeXGBJob(
            JobDO jobDO, ModelJobParam modelJobParam, WorkFlow workflow, WorkFlowNode upstream)
            throws Exception {
        // execute xgb request
        XGBJobRequest xgbJobRequest = modelJobParam.toXGBJobRequest();
        if (xgbJobRequest == null) {
            return false;
        }

        jobDO.setJobRequest(xgbJobRequest);

        JobWorkFlowBuilderImpl jobWorkFlowBuilder =
                new JobWorkFlowBuilderImpl(getJobWorkFlowBuilder(jobDO.getJobType()), this);
        jobWorkFlowBuilder.buildWorkFlow(jobDO, workflow, upstream);
        return true;
    }

    public void registerJobWorkFlowBuilder(
            String jobType, JobWorkFlowBuilderApi jobWorkFlowBuilder) {
        jobWorkFlowBuilderMap.put(jobType, jobWorkFlowBuilder);
        logger.info("register builder : {}", jobType);
    }

    public JobWorkFlowBuilderApi getJobWorkFlowBuilder(String jobType) {
        JobWorkFlowBuilderApi jobWorkFlowBuilder = jobWorkFlowBuilderMap.get(jobType);
        if (jobWorkFlowBuilder == null) {
            logger.error("Unsupported job workflow type, jobType: {}", jobType);
            throw new UnsupportedOperationException(
                    "Unsupported job workflow type, jobType: " + jobType);
        }

        return jobWorkFlowBuilder;
    }

    public void registerJobWorkFlowDependencyHandler(
            String jobType, WorkFlowBuilderDependencyHandler workFlowBuilderDependencyHandler) {
        List<WorkFlowBuilderDependencyHandler> workFlowBuilderDependencyHandlers =
                jobWorkFlowDependencyHandlerMap.get(jobType);
        if (workFlowBuilderDependencyHandlers == null) {
            workFlowBuilderDependencyHandlers = new ArrayList<>();
            workFlowBuilderDependencyHandlers.add(workFlowBuilderDependencyHandler);
        }

        workFlowBuilderDependencyHandlers.add(workFlowBuilderDependencyHandler);
    }

    public List<WorkFlowBuilderDependencyHandler> getHandler(String jobType) {
        return jobWorkFlowDependencyHandlerMap.get(jobType);
    }
}

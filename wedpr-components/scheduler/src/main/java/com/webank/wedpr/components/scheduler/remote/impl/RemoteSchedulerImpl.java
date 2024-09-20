package com.webank.wedpr.components.scheduler.remote.impl;

import com.webank.wedpr.components.project.JobChecker;
import com.webank.wedpr.components.project.dao.JobDO;
import com.webank.wedpr.components.project.dao.ProjectMapperWrapper;
import com.webank.wedpr.components.scheduler.Scheduler;
import com.webank.wedpr.components.scheduler.local.executor.ExecuteResult;
import com.webank.wedpr.components.scheduler.local.executor.impl.ExecutiveContext;
import com.webank.wedpr.components.scheduler.local.executor.impl.model.FileMetaBuilder;
import com.webank.wedpr.components.scheduler.remote.WorkFlowOrchestrator;
import com.webank.wedpr.components.scheduler.remote.api.WorkFlowOrchestratorApi;
import com.webank.wedpr.components.scheduler.remote.builder.JobWorkFlowBuilderManager;
import com.webank.wedpr.components.scheduler.remote.client.SchedulerClient;
import com.webank.wedpr.components.scheduler.remote.config.SchedulerConfig;
import com.webank.wedpr.components.scheduler.remote.workflow.WorkFlow;
import com.webank.wedpr.components.storage.api.FileStorageInterface;
import com.webank.wedpr.core.protocol.JobStatus;
import com.webank.wedpr.core.utils.ObjectMapperFactory;
import com.webank.wedpr.core.utils.ThreadPoolService;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteSchedulerImpl implements Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(RemoteSchedulerImpl.class);

    private final String agency;
    private final ProjectMapperWrapper projectMapperWrapper;
    private final ThreadPoolService threadPoolService;
    private final WorkFlowOrchestratorApi workflowOrchestrator;
    private final SchedulerClient schedulerClient;

    protected List<ExecutiveContext> proceedingJobs = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService queryStatusWorker = new ScheduledThreadPoolExecutor(1);

    public RemoteSchedulerImpl(
            String agency,
            Integer queryStatusIntervalMs,
            ThreadPoolService threadPoolService,
            ProjectMapperWrapper projectMapperWrapper,
            JobChecker jobChecker,
            FileStorageInterface fileStorageInterface,
            FileMetaBuilder fileMetaBuilder) {
        this.agency = agency;
        this.projectMapperWrapper = projectMapperWrapper;
        this.threadPoolService = threadPoolService;
        this.schedulerClient =
                new SchedulerClient(
                        "", SchedulerConfig.getMaxTotalConnection(), SchedulerConfig.buildConfig());

        JobWorkFlowBuilderManager workflowBuilderManagerJob =
                new JobWorkFlowBuilderManager(fileMetaBuilder, fileStorageInterface, jobChecker);

        this.workflowOrchestrator =
                new WorkFlowOrchestrator(workflowBuilderManagerJob, fileMetaBuilder, jobChecker);

        workflowBuilderManagerJob.initialize();

        this.queryStatusWorker.scheduleAtFixedRate(
                this::queryAllJobStatus, 0, queryStatusIntervalMs, TimeUnit.MILLISECONDS);
    }

    private void queryAllJobStatus() {

        for (ExecutiveContext context : proceedingJobs) {
            queryJobStatus(context);
        }

        int proceedingJobsSize = proceedingJobs.size();
        if (proceedingJobsSize > 0) {
            logger.info(
                    "## remote scheduler query all jobs status proceeding jobs size: {}",
                    proceedingJobsSize);
        } else {
            logger.info("## remote scheduler query all jobs status, no jobs is proceeding");
        }
    }

    @Override
    public void batchKillJobs(List<JobDO> jobs) {
        for (JobDO jobDO : jobs) {
            if (!jobDO.isJobParty(this.agency)) {
                continue;
            }
            // set the job status to killing
            this.projectMapperWrapper.updateSingleJobStatus(null, null, jobDO, JobStatus.Killing);
            threadPoolService.getThreadPool().execute(() -> killJob(jobDO));
        }
    }

    @Override
    public void batchRunJobs(List<JobDO> jobs) {
        for (JobDO jobDO : jobs) {
            if (!jobDO.isJobParty(this.agency)) {
                continue;
            }
            // set the job status to running
            this.projectMapperWrapper.updateSingleJobStatus(null, null, jobDO, JobStatus.Running);
            threadPoolService.getThreadPool().execute(() -> runJob(jobDO));
        }
    }

    public void queryJobStatus(ExecutiveContext executiveContext) {

        String jobId = executiveContext.getJob().getId();
        try {
            ExecuteResult executeResult = schedulerClient.queryJob(jobId);
            if (executeResult.finished()) {
                logger.info("## remote scheduler job is finished, jobId: {}", jobId);
                executiveContext.onTaskFinished(executeResult);
                proceedingJobs.remove(executiveContext);
            }
        } catch (Exception e) {
            logger.error(
                    "## remote scheduler query status for job failed, job: {}, error: ",
                    executiveContext.getJob().toString(),
                    e);
            executiveContext.onTaskFinished(
                    new ExecuteResult(
                            "Job "
                                    + executiveContext.getJob().getId()
                                    + " failed for "
                                    + e.getMessage(),
                            ExecuteResult.ResultStatus.FAILED));
            proceedingJobs.remove(executiveContext);
        }
    }

    public void runJob(JobDO jobDO) {

        try {
            WorkFlow workflow = workflowOrchestrator.buildWorkFlow(jobDO);
            String workflowStr = ObjectMapperFactory.getObjectMapper().writeValueAsString(workflow);

            schedulerClient.postJob(jobDO.getId(), workflowStr);

            proceedingJobs.add(
                    new ExecutiveContext(
                            jobDO, this::onJobFinish, jobDO.getTaskID(), projectMapperWrapper));

            logger.info("remote scheduler post for run job successfully, jobId: {}", jobDO.getId());

        } catch (Exception e) {
            logger.error(
                    "remote scheduler post for run job exception, jobId: {}, e: ",
                    jobDO.getId(),
                    e);
            // set the job status to failed
            this.projectMapperWrapper.updateSingleJobStatus(null, null, jobDO, JobStatus.RunFailed);
        }
    }

    public void killJob(JobDO jobDO) {
        try {
            schedulerClient.killJob(jobDO.getId());

            logger.info(
                    "remote scheduler post for kill job successfully, jobId: {}", jobDO.getId());
            // set the job status to failed
            this.projectMapperWrapper.updateSingleJobStatus(null, null, jobDO, JobStatus.Killed);
        } catch (Exception e) {
            logger.error(
                    "remote scheduler post for kill job failed, jobId: {}, e: ", jobDO.getId(), e);
            // set the job status to failed
            this.projectMapperWrapper.updateSingleJobStatus(
                    null, null, jobDO, JobStatus.KillFailed);
        }
    }

    private void onJobFailed(JobDO jobDO, ExecuteResult result) {
        try {
            this.projectMapperWrapper.updateFinalJobResult(
                    jobDO, JobStatus.RunFailed, result.serialize());

            logger.info(
                    "remote scheduler update job status to failed, jobId {}, result: {}",
                    jobDO.getId(),
                    result);

        } catch (Exception e) {
            logger.error(
                    "remote scheduler update job status to failed exception, jobId {}, result: {}, e: ",
                    jobDO.getId(),
                    result,
                    e);
        }
    }

    private void onJobSuccess(JobDO jobDO, ExecuteResult result) {
        try {
            this.projectMapperWrapper.updateFinalJobResult(
                    jobDO, JobStatus.RunSuccess, result.serialize());

            logger.info(
                    "remote scheduler update job status to success, jobId {}, result: {}",
                    jobDO.getId(),
                    result);
        } catch (Exception e) {
            logger.error(
                    "remote scheduler update job status to success exception, jobId {}, result: {}, e: ",
                    jobDO.getId(),
                    result,
                    e);
        }
    }

    public void onJobFinish(JobDO jobDO, ExecuteResult result) {
        if (result.getResultStatus().failed()) {
            onJobFailed(jobDO, result);
            return;
        }
        onJobSuccess(jobDO, result);
    }
}

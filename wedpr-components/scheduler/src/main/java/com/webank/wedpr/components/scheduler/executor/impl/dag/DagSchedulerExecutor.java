package com.webank.wedpr.components.scheduler.executor.impl.dag;

import com.webank.wedpr.common.protocol.ExecutorType;
import com.webank.wedpr.common.utils.ThreadPoolService;
import com.webank.wedpr.components.loadbalancer.LoadBalancer;
import com.webank.wedpr.components.project.JobChecker;
import com.webank.wedpr.components.project.dao.JobDO;
import com.webank.wedpr.components.scheduler.api.WorkFlowOrchestratorApi;
import com.webank.wedpr.components.scheduler.dag.DagWorkFlowSchedulerImpl;
import com.webank.wedpr.components.scheduler.dag.api.WorkFlowScheduler;
import com.webank.wedpr.components.scheduler.executor.ExecuteResult;
import com.webank.wedpr.components.scheduler.executor.Executor;
import com.webank.wedpr.components.scheduler.executor.callback.TaskFinishedHandler;
import com.webank.wedpr.components.scheduler.executor.impl.ExecutiveContext;
import com.webank.wedpr.components.scheduler.executor.impl.ExecutiveContextBuilder;
import com.webank.wedpr.components.scheduler.executor.impl.model.FileMetaBuilder;
import com.webank.wedpr.components.scheduler.executor.manager.ExecutorManager;
import com.webank.wedpr.components.scheduler.mapper.JobWorkerMapper;
import com.webank.wedpr.components.scheduler.workflow.WorkFlow;
import com.webank.wedpr.components.scheduler.workflow.WorkFlowOrchestrator;
import com.webank.wedpr.components.scheduler.workflow.builder.JobWorkFlowBuilderManager;
import com.webank.wedpr.components.storage.api.FileStorageInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DagSchedulerExecutor implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(DagSchedulerExecutor.class);

    private final WorkFlowScheduler workFlowScheduler;
    private final WorkFlowOrchestratorApi workflowOrchestrator;
    private final ExecutorManager executorManager;

    private final ExecutiveContextBuilder executiveContextBuilder;

    private final ThreadPoolService threadPoolService;

    public DagSchedulerExecutor(
            LoadBalancer loadBalancer,
            JobWorkerMapper jobWorkerMapper,
            JobChecker jobChecker,
            FileStorageInterface fileStorageInterface,
            FileMetaBuilder fileMetaBuilder,
            ExecutorManager executorManager,
            ExecutiveContextBuilder executiveContextBuilder,
            ThreadPoolService threadPoolService) {
        this.executiveContextBuilder = executiveContextBuilder;
        this.threadPoolService = threadPoolService;
        this.executorManager = executorManager;

        this.workFlowScheduler = new DagWorkFlowSchedulerImpl(loadBalancer, jobWorkerMapper);

        JobWorkFlowBuilderManager jobWorkflowBuilderManager =
                new JobWorkFlowBuilderManager(fileMetaBuilder, fileStorageInterface, jobChecker);

        this.workflowOrchestrator =
                new WorkFlowOrchestrator(jobWorkflowBuilderManager, fileMetaBuilder, jobChecker);

        jobWorkflowBuilderManager.initialize();
    }

    @Override
    public Object prepare(JobDO jobDO) throws Exception {
        return null;
    }

    @Override
    public void execute(JobDO jobDO) throws Exception {
        this.threadPoolService
                .getThreadPool()
                .execute(
                        () -> {
                            innerExecute(jobDO);
                        });
    }

    public void innerExecute(JobDO jobDO) {
        long startTimeMillis = System.currentTimeMillis();

        logger.info("execute start, jobId: {}", jobDO.getId());

        TaskFinishedHandler taskFinishHandler =
                executorManager.getTaskFinishHandler(ExecutorType.DAG.getType());

        ExecutiveContext executiveContext =
                executiveContextBuilder.build(jobDO, taskFinishHandler, jobDO.getId());

        try {
            WorkFlow workflow = workflowOrchestrator.buildWorkFlow(jobDO);

            this.workFlowScheduler.schedule(jobDO.getId(), workflow);

            executiveContext.onTaskFinished(new ExecuteResult(ExecuteResult.ResultStatus.SUCCESS));

            long endTimeMillis = System.currentTimeMillis();

            logger.info(
                    "execute end, jobId: {}, elapsed: {} ms",
                    jobDO.getId(),
                    (endTimeMillis - startTimeMillis));

        } catch (Exception e) {

            executiveContext.onTaskFinished(
                    new ExecuteResult(e.getMessage(), ExecuteResult.ResultStatus.FAILED));

            long endTimeMillis = System.currentTimeMillis();

            logger.warn(
                    "execute end exception, jobId: {}, elapsed: {} ms, e: ",
                    jobDO.getId(),
                    (endTimeMillis - startTimeMillis),
                    e);
        }
    }

    @Override
    public void kill(JobDO jobDO) throws Exception {}

    @Override
    public ExecuteResult queryStatus(String jobID) throws Exception {
        return null;
    }
}

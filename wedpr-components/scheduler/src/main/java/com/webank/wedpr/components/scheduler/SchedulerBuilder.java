/*
 * Copyright 2017-2025  [webank-wedpr]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.webank.wedpr.components.scheduler;

import com.webank.wedpr.components.project.JobChecker;
import com.webank.wedpr.components.project.dao.ProjectMapperWrapper;
import com.webank.wedpr.components.scheduler.local.config.SchedulerTaskConfig;
import com.webank.wedpr.components.scheduler.local.core.SchedulerTaskImpl;
import com.webank.wedpr.components.scheduler.local.executor.ExecutorManager;
import com.webank.wedpr.components.scheduler.local.executor.impl.ExecutorManagerImpl;
import com.webank.wedpr.components.scheduler.local.executor.impl.model.FileMetaBuilder;
import com.webank.wedpr.components.scheduler.local.impl.LocalSchedulerImpl;
import com.webank.wedpr.components.scheduler.remote.config.SchedulerConfig;
import com.webank.wedpr.components.scheduler.remote.impl.RemoteSchedulerImpl;
import com.webank.wedpr.components.storage.api.FileStorageInterface;
import com.webank.wedpr.components.sync.ResourceSyncer;
import com.webank.wedpr.core.config.WeDPRCommonConfig;
import com.webank.wedpr.core.utils.ThreadPoolService;
import com.webank.wedpr.core.utils.WeDPRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerBuilder.class);
    private static final String WORKER_NAME = "scheduler";

    public static SchedulerTaskImpl buildSchedulerTask(
            ProjectMapperWrapper projectMapperWrapper,
            FileStorageInterface storage,
            ResourceSyncer resourceSyncer,
            FileMetaBuilder fileMetaBuilder,
            JobChecker jobChecker)
            throws Exception {
        try {
            String agency = WeDPRCommonConfig.getAgency();

            logger.info("## create SchedulerTask, agency: {}", agency);

            Scheduler scheduler =
                    buildScheduler(
                            agency, projectMapperWrapper, storage, fileMetaBuilder, jobChecker);

            SchedulerTaskImpl schedulerTask =
                    new SchedulerTaskImpl(projectMapperWrapper, resourceSyncer, scheduler);
            logger.info("create SchedulerTask success");
            return schedulerTask;
        } catch (Exception e) {
            logger.error("create SchedulerTask failed, error: ", e);
            throw new WeDPRException("Create SchedulerTask failed for " + e.getMessage(), e);
        }
    }

    public static Scheduler buildScheduler(
            String agency,
            ProjectMapperWrapper projectMapperWrapper,
            FileStorageInterface storage,
            FileMetaBuilder fileMetaBuilder,
            JobChecker jobChecker) {
        boolean enableRemoteScheduler = SchedulerConfig.getEnableRemoteScheduler();

        logger.info("## build scheduler, enable remote scheduler: {}", enableRemoteScheduler);

        ThreadPoolService schedulerWorker =
                new ThreadPoolService(WORKER_NAME, SchedulerTaskConfig.getWorkerQueueSize());

        if (enableRemoteScheduler) {
            return new RemoteSchedulerImpl(
                    agency,
                    SchedulerTaskConfig.getQueryJobStatusIntervalMs(),
                    schedulerWorker,
                    projectMapperWrapper,
                    jobChecker,
                    storage,
                    fileMetaBuilder);
        } else {
            // create and start the executorManager
            ExecutorManager executorManager =
                    new ExecutorManagerImpl(
                            SchedulerTaskConfig.getQueryJobStatusIntervalMs(),
                            fileMetaBuilder,
                            storage,
                            jobChecker,
                            projectMapperWrapper);

            return new LocalSchedulerImpl(
                    agency,
                    executorManager,
                    schedulerWorker,
                    projectMapperWrapper,
                    jobChecker,
                    storage,
                    fileMetaBuilder);
        }
    }
}

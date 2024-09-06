package com.webank.wedpr.components.report.handler;

import com.webank.wedpr.components.project.dao.JobDatasetDO;
import com.webank.wedpr.components.project.dao.ProjectMapper;
import com.webank.wedpr.components.transport.Transport;
import com.webank.wedpr.components.transport.model.Message;
import com.webank.wedpr.core.protocol.ReportStatusEnum;
import com.webank.wedpr.core.utils.Constant;
import com.webank.wedpr.core.utils.ObjectMapperFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/** Created by caryliao on 2024/9/4 10:54 */
@Slf4j
public class JobDatasetRelationReportMessageHandler implements Transport.MessageHandler {
    private ProjectMapper projectMapper;

    public JobDatasetRelationReportMessageHandler(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Override
    public void call(Message msg) {
        byte[] payload = msg.getPayload();
        try {
            JobDatasetReportResponse jobDatasetReportResponse =
                    ObjectMapperFactory.getObjectMapper()
                            .readValue(payload, JobDatasetReportResponse.class);
            if (Constant.WEDPR_SUCCESS == jobDatasetReportResponse.getCode()) {
                // report ok ,then set report status to 1
                List<String> jobIdList = jobDatasetReportResponse.getJobIdList();
                ArrayList<JobDatasetDO> jobDatasetDOList = new ArrayList<>();
                for (String jobId : jobIdList) {
                    JobDatasetDO jobDatasetDO = new JobDatasetDO();
                    jobDatasetDO.setJobId(jobId);
                    jobDatasetDO.setReportStatus(ReportStatusEnum.DONE_REPORT.getReportStatus());
                    jobDatasetDOList.add(jobDatasetDO);
                }
                projectMapper.batchUpdateJobDatasetInfo(jobDatasetDOList);
            } else {
                log.warn("report job dataset relation error:{}", jobDatasetReportResponse);
            }
        } catch (IOException e) {
            log.warn("handle JobDatasetReportResponse error", e);
        }
    }
}

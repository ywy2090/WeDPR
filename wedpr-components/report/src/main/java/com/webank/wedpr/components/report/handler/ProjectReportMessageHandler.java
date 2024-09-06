package com.webank.wedpr.components.report.handler;

import com.webank.wedpr.components.project.dao.ProjectDO;
import com.webank.wedpr.components.project.dao.ProjectMapper;
import com.webank.wedpr.components.transport.Transport;
import com.webank.wedpr.components.transport.model.Message;
import com.webank.wedpr.core.protocol.ReportStatusEnum;
import com.webank.wedpr.core.utils.Constant;
import com.webank.wedpr.core.utils.ObjectMapperFactory;
import com.webank.wedpr.core.utils.WeDPRException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/** Created by caryliao on 2024/9/4 10:54 */
@Slf4j
public class ProjectReportMessageHandler implements Transport.MessageHandler {
    private ProjectMapper projectMapper;

    public ProjectReportMessageHandler(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Override
    public void call(Message msg) throws WeDPRException {
        byte[] payload = msg.getPayload();
        try {
            ProjectReportResponse projectReportResponse =
                    ObjectMapperFactory.getObjectMapper()
                            .readValue(payload, ProjectReportResponse.class);
            if (Constant.WEDPR_SUCCESS == projectReportResponse.getCode()) {
                // report ok ,then set report status to 1
                List<String> projectIdList = projectReportResponse.getProjectIdList();
                ArrayList<ProjectDO> projectDOList = new ArrayList<>();
                for (String projectId : projectIdList) {
                    ProjectDO projectDO = new ProjectDO();
                    projectDO.setId(projectId);
                    projectDO.setReportStatus(ReportStatusEnum.DO_REPORT.getReportStatus());
                    projectDOList.add(projectDO);
                }
                projectMapper.batchUpdateProjectInfo(projectDOList);
            } else {
                log.warn("report project error:{}", projectReportResponse);
            }
        } catch (IOException e) {
            log.warn("handle ProjectReportResponse error", e);
        }
    }
}

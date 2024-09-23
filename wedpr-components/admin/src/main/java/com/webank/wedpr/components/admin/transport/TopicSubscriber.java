package com.webank.wedpr.components.admin.transport;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedpr.components.admin.entity.WedprJobDatasetRelation;
import com.webank.wedpr.components.admin.entity.WedprJobTable;
import com.webank.wedpr.components.admin.entity.WedprProjectTable;
import com.webank.wedpr.components.admin.service.WedprJobDatasetRelationService;
import com.webank.wedpr.components.admin.service.WedprJobTableService;
import com.webank.wedpr.components.admin.service.WedprProjectTableService;
import com.webank.wedpr.components.meta.sys.config.dao.SysConfigDO;
import com.webank.wedpr.components.meta.sys.config.service.SysConfigService;
import com.webank.wedpr.components.transport.message.JobDatasetReportResponse;
import com.webank.wedpr.components.transport.message.JobReportResponse;
import com.webank.wedpr.components.transport.message.ProjectReportResponse;
import com.webank.wedpr.components.transport.message.SysConfigReportResponse;
import com.webank.wedpr.core.protocol.TransportComponentEnum;
import com.webank.wedpr.core.protocol.TransportTopicEnum;
import com.webank.wedpr.core.utils.Constant;
import com.webank.wedpr.core.utils.ObjectMapperFactory;
import com.webank.wedpr.sdk.jni.generated.Error;
import com.webank.wedpr.sdk.jni.transport.IMessage;
import com.webank.wedpr.sdk.jni.transport.WeDPRTransport;
import com.webank.wedpr.sdk.jni.transport.handlers.MessageDispatcherCallback;
import com.webank.wedpr.sdk.jni.transport.handlers.MessageErrorCallback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/** Created by caryliao on 2024/9/4 15:03 */
@Service
@Slf4j
public class TopicSubscriber implements CommandLineRunner {
    @Autowired private WeDPRTransport weDPRTransport;

    @Autowired private WedprProjectTableService wedprProjectTableService;
    @Autowired private WedprJobTableService wedprJobTableService;
    @Autowired private WedprJobDatasetRelationService wedprJobDatasetRelationService;
    @Autowired private SysConfigService sysConfigService;

    @Override
    public void run(String... args) throws Exception {
        try {
            weDPRTransport.registerComponent(TransportComponentEnum.REPORT.name());
            subscribeProjectTopic();
            subscribeJobTopic();
            subscribeJobDatasetRelationTopic();
            subscribeSysConfigTopic();
        } catch (Exception e) {
            log.warn("subscribe topic error", e);
        }
    }

    private void subscribeSysConfigTopic() {
        weDPRTransport.registerTopicHandler(
                TransportTopicEnum.SYS_CONFIG_REPORT.name(),
                new MessageDispatcherCallback() {
                    @Override
                    public void onMessage(IMessage message) {
                        byte[] payload = message.getPayload();
                        List<SysConfigDO> sysConfigDOList = null;
                        SysConfigReportResponse response = new SysConfigReportResponse();
                        try {
                            sysConfigDOList =
                                    (List<SysConfigDO>)
                                            ObjectMapperFactory.getObjectMapper()
                                                    .readValue(payload, List.class);
                        } catch (IOException e) {
                            log.warn("parse message error", e);
                            response.setCode(Constant.WEDPR_FAILED);
                            response.setMsg("parse message error" + e.getMessage());
                        }
                        List<String> configKeyList = new ArrayList<>();
                        MessageErrorCallback messageErrorCallback = null;
                        byte[] responsePayload = null;
                        try {
                            for (SysConfigDO sysConfigDO : sysConfigDOList) {
                                String configKey = sysConfigDO.getConfigKey();
                                SysConfigDO queriedSysConfigDO =
                                        sysConfigService.getById(configKey);
                                if (queriedSysConfigDO == null) {
                                    sysConfigService.save(sysConfigDO);
                                } else {
                                    sysConfigService.updateById(sysConfigDO);
                                }
                                configKeyList.add(configKey);
                            }
                            messageErrorCallback =
                                    new MessageErrorCallback() {
                                        @Override
                                        public void onError(Error error) {
                                            log.error(
                                                    "Error code:{}, message:{}",
                                                    error.errorCode(),
                                                    error.errorMessage());
                                        }
                                    };
                            response.setCode(Constant.WEDPR_FAILED);
                            response.setMsg(Constant.WEDPR_SUCCESS_MSG);
                            response.setConfigKeyList(configKeyList);
                            responsePayload =
                                    ObjectMapperFactory.getObjectMapper()
                                            .writeValueAsBytes(response);
                        } catch (JsonProcessingException e) {
                            log.error("handle error", e);
                            response.setCode(Constant.WEDPR_FAILED);
                            response.setMsg("handle error" + e.getMessage());
                        }
                        IMessage.IMessageHeader messageHeader = message.getHeader();
                        weDPRTransport.asyncSendResponse(
                                messageHeader.getSrcNode(),
                                messageHeader.getTraceID(),
                                responsePayload,
                                0,
                                messageErrorCallback);
                    }
                });
    }

    private void subscribeJobDatasetRelationTopic() {
        weDPRTransport.registerTopicHandler(
                TransportTopicEnum.JOB_DATASET_REPORT.name(),
                new MessageDispatcherCallback() {
                    @Override
                    public void onMessage(IMessage message) {
                        byte[] payload = message.getPayload();
                        List<WedprJobDatasetRelation> wedprJobDatasetRelationList = null;
                        JobDatasetReportResponse response = new JobDatasetReportResponse();
                        try {
                            wedprJobDatasetRelationList =
                                    (List<WedprJobDatasetRelation>)
                                            ObjectMapperFactory.getObjectMapper()
                                                    .readValue(payload, List.class);
                        } catch (IOException e) {
                            log.warn("parse message error", e);
                            response.setCode(Constant.WEDPR_FAILED);
                            response.setMsg("parse message error" + e.getMessage());
                        }
                        List<String> jobIdList = new ArrayList<>();
                        MessageErrorCallback messageErrorCallback = null;
                        byte[] responsePayload = null;
                        try {
                            for (WedprJobDatasetRelation wedprJobDatasetRelation :
                                    wedprJobDatasetRelationList) {
                                String jobId = wedprJobDatasetRelation.getJobId();
                                LambdaQueryWrapper<WedprJobDatasetRelation> lambdaQueryWrapper =
                                        new LambdaQueryWrapper<>();
                                lambdaQueryWrapper.eq(WedprJobDatasetRelation::getJobId, jobId);
                                WedprJobDatasetRelation queriedWedprJobDatasetRelation =
                                        wedprJobDatasetRelationService.getOne(lambdaQueryWrapper);
                                if (queriedWedprJobDatasetRelation == null) {
                                    wedprJobDatasetRelationService.save(wedprJobDatasetRelation);
                                } else {
                                    wedprJobDatasetRelationService.update(
                                            wedprJobDatasetRelation, lambdaQueryWrapper);
                                }
                                jobIdList.add(jobId);
                            }
                            messageErrorCallback =
                                    new MessageErrorCallback() {
                                        @Override
                                        public void onError(Error error) {
                                            log.error(
                                                    "Error code:{}, message:{}",
                                                    error.errorCode(),
                                                    error.errorMessage());
                                        }
                                    };
                            response.setCode(Constant.WEDPR_FAILED);
                            response.setMsg(Constant.WEDPR_SUCCESS_MSG);
                            response.setJobIdList(jobIdList);
                            responsePayload =
                                    ObjectMapperFactory.getObjectMapper()
                                            .writeValueAsBytes(response);
                        } catch (JsonProcessingException e) {
                            log.error("handle error", e);
                            response.setCode(Constant.WEDPR_FAILED);
                            response.setMsg("handle error" + e.getMessage());
                        }
                        IMessage.IMessageHeader messageHeader = message.getHeader();
                        weDPRTransport.asyncSendResponse(
                                messageHeader.getSrcNode(),
                                messageHeader.getTraceID(),
                                responsePayload,
                                0,
                                messageErrorCallback);
                    }
                });
    }

    private void subscribeJobTopic() {
        weDPRTransport.registerTopicHandler(
                TransportTopicEnum.JOB_REPORT.name(),
                new MessageDispatcherCallback() {
                    @Override
                    public void onMessage(IMessage message) {
                        byte[] payload = message.getPayload();
                        List<WedprJobTable> wedprJobTableList = null;
                        JobReportResponse response = new JobReportResponse();
                        try {
                            wedprJobTableList =
                                    (List<WedprJobTable>)
                                            ObjectMapperFactory.getObjectMapper()
                                                    .readValue(payload, List.class);
                        } catch (IOException e) {
                            log.warn("parse message error", e);
                            response.setCode(Constant.WEDPR_FAILED);
                            response.setMsg("parse message error" + e.getMessage());
                        }
                        List<String> jobIdList = new ArrayList<>();
                        MessageErrorCallback messageErrorCallback = null;
                        byte[] responsePayload = null;
                        try {
                            for (WedprJobTable wedprJobTable : wedprJobTableList) {
                                String jobId = wedprJobTable.getId();
                                WedprJobTable queriedWedprJobTable =
                                        wedprJobTableService.getById(jobId);
                                if (queriedWedprJobTable == null) {
                                    wedprJobTableService.save(wedprJobTable);
                                } else {
                                    wedprJobTableService.updateById(wedprJobTable);
                                }
                                jobIdList.add(jobId);
                            }
                            messageErrorCallback =
                                    new MessageErrorCallback() {
                                        @Override
                                        public void onError(Error error) {
                                            log.error(
                                                    "Error code:{}, message:{}",
                                                    error.errorCode(),
                                                    error.errorMessage());
                                        }
                                    };
                            response.setCode(Constant.WEDPR_FAILED);
                            response.setMsg(Constant.WEDPR_SUCCESS_MSG);
                            response.setJobIdList(jobIdList);
                            responsePayload =
                                    ObjectMapperFactory.getObjectMapper()
                                            .writeValueAsBytes(response);
                        } catch (Exception e) {
                            log.error("handle error", e);
                            response.setCode(Constant.WEDPR_FAILED);
                            response.setMsg("handle error" + e.getMessage());
                        }
                        IMessage.IMessageHeader messageHeader = message.getHeader();
                        weDPRTransport.asyncSendResponse(
                                messageHeader.getSrcNode(),
                                messageHeader.getTraceID(),
                                responsePayload,
                                0,
                                messageErrorCallback);
                    }
                });
    }

    private void subscribeProjectTopic() {
        weDPRTransport.registerTopicHandler(
                TransportTopicEnum.PROJECT_REPORT.name(),
                new MessageDispatcherCallback() {
                    @Override
                    public void onMessage(IMessage message) {
                        byte[] payload = message.getPayload();
                        List<WedprProjectTable> wedprProjectTableList = null;
                        ProjectReportResponse response = new ProjectReportResponse();
                        try {
                            wedprProjectTableList =
                                    (List<WedprProjectTable>)
                                            ObjectMapperFactory.getObjectMapper()
                                                    .readValue(payload, List.class);
                        } catch (IOException e) {
                            log.warn("parse message error", e);
                            response.setCode(Constant.WEDPR_FAILED);
                            response.setMsg("parse message error" + e.getMessage());
                        }
                        List<String> projectIdList = new ArrayList<>();
                        MessageErrorCallback messageErrorCallback = null;
                        byte[] responsePayload = null;
                        try {
                            for (WedprProjectTable wedprProjectTable : wedprProjectTableList) {
                                String projectId = wedprProjectTable.getId();
                                WedprProjectTable queriedWedprProjectTable =
                                        wedprProjectTableService.getById(projectId);
                                if (queriedWedprProjectTable == null) {
                                    wedprProjectTableService.save(wedprProjectTable);
                                } else {
                                    wedprProjectTableService.updateById(wedprProjectTable);
                                }
                                projectIdList.add(projectId);
                            }
                            messageErrorCallback =
                                    new MessageErrorCallback() {
                                        @Override
                                        public void onError(Error error) {
                                            log.error(
                                                    "Error code:{}, message:{}",
                                                    error.errorCode(),
                                                    error.errorMessage());
                                        }
                                    };
                            response.setCode(Constant.WEDPR_FAILED);
                            response.setMsg(Constant.WEDPR_SUCCESS_MSG);
                            response.setProjectIdList(projectIdList);
                            responsePayload =
                                    ObjectMapperFactory.getObjectMapper()
                                            .writeValueAsBytes(response);
                        } catch (Exception e) {
                            log.error("handle error", e);
                            response.setCode(Constant.WEDPR_FAILED);
                            response.setMsg("handle error" + e.getMessage());
                        }
                        IMessage.IMessageHeader messageHeader = message.getHeader();
                        weDPRTransport.asyncSendResponse(
                                messageHeader.getSrcNode(),
                                messageHeader.getTraceID(),
                                responsePayload,
                                0,
                                messageErrorCallback);
                    }
                });
    }
}

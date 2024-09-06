package com.webank.wedpr.components.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedpr.components.admin.entity.WedprJobDatasetRelation;
import com.webank.wedpr.components.admin.request.GetJobByDatasetRequest;
import com.webank.wedpr.components.admin.response.ListJobResponse;

/**
 * 服务类
 *
 * @author caryliao
 * @since 2024-09-06
 */
public interface WedprJobDatasetRelationService extends IService<WedprJobDatasetRelation> {

    ListJobResponse queryJobsByDatasetId(GetJobByDatasetRequest getJobByDatasetRequest);
}

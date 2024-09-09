package com.webank.wedpr.components.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedpr.components.admin.entity.WedprJobTable;
import com.webank.wedpr.components.admin.request.GetWedprJobListRequest;
import com.webank.wedpr.components.admin.response.ListJobResponse;

/**
 * 服务类
 *
 * @author caryliao
 * @since 2024-09-04
 */
public interface WedprJobTableService extends IService<WedprJobTable> {
    ListJobResponse listJob(GetWedprJobListRequest getWedprJobListRequest);
}

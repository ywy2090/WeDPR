package com.webank.wedpr.components.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedpr.components.admin.common.Utils;
import com.webank.wedpr.components.admin.entity.WedprJobTable;
import com.webank.wedpr.components.admin.mapper.WedprJobTableMapper;
import com.webank.wedpr.components.admin.request.GetWedprJobListRequest;
import com.webank.wedpr.components.admin.response.ListJobResponse;
import com.webank.wedpr.components.admin.service.WedprJobTableService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 服务实现类
 *
 * @author caryliao
 * @since 2024-09-04
 */
@Service
public class WedprJobTableServiceImpl extends ServiceImpl<WedprJobTableMapper, WedprJobTable>
        implements WedprJobTableService {
    @Override
    public ListJobResponse listJob(GetWedprJobListRequest request) {
        LambdaQueryWrapper<WedprJobTable> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        String projectName = request.getProjectName();
        String ownerAgency = request.getOwnerAgency();
        String jobType = request.getJobType();
        String status = request.getStatus();
        String startTimeStr = request.getStartTime();
        String endTimeStr = request.getEndTime();
        Integer pageNum = request.getPageNum();
        Integer pageSize = request.getPageSize();
        lambdaQueryWrapper.eq(WedprJobTable::getProjectName, projectName);
        if (!StringUtils.isEmpty(ownerAgency)) {
            lambdaQueryWrapper.eq(WedprJobTable::getOwnerAgency, ownerAgency);
        }
        if (!StringUtils.isEmpty(jobType)) {
            lambdaQueryWrapper.eq(WedprJobTable::getJobType, jobType);
        }
        if (!StringUtils.isEmpty(status)) {
            lambdaQueryWrapper.eq(WedprJobTable::getStatus, status);
        }
        if (!StringUtils.isEmpty(startTimeStr)) {
            LocalDateTime startTime = Utils.getLocalDateTime(startTimeStr);
            lambdaQueryWrapper.ge(WedprJobTable::getCreateTime, startTime);
        }
        if (!StringUtils.isEmpty(endTimeStr)) {
            LocalDateTime endTime = Utils.getLocalDateTime(endTimeStr);
            lambdaQueryWrapper.le(WedprJobTable::getCreateTime, endTime);
        }
        lambdaQueryWrapper.orderByDesc(WedprJobTable::getLastUpdateTime);
        Page<WedprJobTable> projectTablePage = new Page<>(pageNum, pageSize);
        Page<WedprJobTable> page = page(projectTablePage, lambdaQueryWrapper);
        ListJobResponse listProjectResponse = new ListJobResponse();
        listProjectResponse.setTotal(page.getTotal());
        listProjectResponse.setJobList(page.getRecords());
        return listProjectResponse;
    }
}

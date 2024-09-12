package com.webank.wedpr.components.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedpr.components.admin.common.Utils;
import com.webank.wedpr.components.admin.entity.WedprJobTable;
import com.webank.wedpr.components.admin.mapper.WedprJobTableMapper;
import com.webank.wedpr.components.admin.request.GetWedprJobListRequest;
import com.webank.wedpr.components.admin.response.*;
import com.webank.wedpr.components.admin.service.WedprJobTableService;
import com.webank.wedpr.core.protocol.JobStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${dashbord.decimalPlaces:0}")
    private Integer decimalPlaces;

    @Autowired private WedprJobTableMapper wedprJobTableMapper;

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

    @Override
    public GetJobStatisticsResponse getJobStatistics() {
        // query job overview
        int totalCount = count();
        LambdaQueryWrapper<WedprJobTable> wedprJobTableQueryWrapper = new LambdaQueryWrapper<>();
        wedprJobTableQueryWrapper.eq(WedprJobTable::getStatus, JobStatus.RunSuccess.getStatus());
        int successCount = count(wedprJobTableQueryWrapper);
        String successProportion = Utils.getPercentage(successCount, totalCount, decimalPlaces);
        JobOverview jobOverview = new JobOverview();
        jobOverview.setTotalCount(totalCount);
        jobOverview.setSuccessCount(successCount);
        jobOverview.setSuccessProportion(successProportion);

        // query jobTypeStatistic
        List<WedprJobTable> jobTableList1 = wedprJobTableMapper.jobTypeStatistic();
        List<JobTypeStatistic> jobTypeStatisticList = new ArrayList<>();
        for (WedprJobTable wedprJobTable : jobTableList1) {
            JobTypeStatistic jobTypeStatistic = new JobTypeStatistic();
            jobTypeStatistic.setJobType(wedprJobTable.getJobType());
            Integer countByJobType = wedprJobTable.getCount();
            jobTypeStatistic.setCount(countByJobType);
            jobTypeStatisticList.add(jobTypeStatistic);
        }
        // query agencyJobTypeStatistic
        List<WedprJobTable> jobTableList2 = wedprJobTableMapper.jobAgencyStatistic();
        List<WedprJobTable> jobTableList3 = wedprJobTableMapper.jobAgencyTypeStatistic();
        ArrayList<AgencyJobTypeStatistic> agencyJobTypeStatisticList =
                new ArrayList<>(jobTableList2.size());
        for (WedprJobTable wedprJobTable2 : jobTableList2) {
            AgencyJobTypeStatistic agencyJobTypeStatistic = new AgencyJobTypeStatistic();
            agencyJobTypeStatistic.setAgencyName(wedprJobTable2.getOwnerAgency());
            agencyJobTypeStatistic.setTotalCount(wedprJobTable2.getCount());
            List<JobTypeStatistic> jobTypeStatisticsList = new ArrayList<>();
            for (WedprJobTable wedprJobTable3 : jobTableList3) {
                if (wedprJobTable3.getOwnerAgency().equals(wedprJobTable2.getOwnerAgency())) {
                    JobTypeStatistic jobTypeStatistic = new JobTypeStatistic();
                    jobTypeStatistic.setJobType(wedprJobTable3.getJobType());
                    jobTypeStatistic.setCount(wedprJobTable3.getCount());
                    jobTypeStatisticsList.add(jobTypeStatistic);
                }
            }
            agencyJobTypeStatistic.setJobTypeStatistic(jobTypeStatisticsList);
            agencyJobTypeStatisticList.add(agencyJobTypeStatistic);
        }
        GetJobStatisticsResponse response = new GetJobStatisticsResponse();
        response.setJobOverview(jobOverview);
        response.setJobTypeStatistic(jobTypeStatisticList);
        response.setAgencyJobTypeStatistic(agencyJobTypeStatisticList);
        return response;
    }
}

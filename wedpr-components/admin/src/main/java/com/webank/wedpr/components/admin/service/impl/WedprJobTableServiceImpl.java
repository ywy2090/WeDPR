package com.webank.wedpr.components.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedpr.components.admin.common.Utils;
import com.webank.wedpr.components.admin.entity.WedprAgency;
import com.webank.wedpr.components.admin.entity.WedprJobTable;
import com.webank.wedpr.components.admin.mapper.WedprJobTableMapper;
import com.webank.wedpr.components.admin.request.GetJobDateLineRequest;
import com.webank.wedpr.components.admin.request.GetWedprJobListRequest;
import com.webank.wedpr.components.admin.response.*;
import com.webank.wedpr.components.admin.service.WedprAgencyService;
import com.webank.wedpr.components.admin.service.WedprJobTableService;
import com.webank.wedpr.core.protocol.JobStatus;
import com.webank.wedpr.core.protocol.JobType;
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

    @Autowired private WedprAgencyService wedprAgencyService;

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
        JobType[] jobTypes = JobType.values();
        for (JobType jobTypeItem : jobTypes) {
            JobTypeStatistic jobTypeStatistic = new JobTypeStatistic();
            String jobType = jobTypeItem.getType();
            jobTypeStatistic.setJobType(jobType);
            jobTypeStatistic.setCount(0);
            for (WedprJobTable wedprJobTable : jobTableList1) {
                if (jobType.equals(wedprJobTable.getJobType())) {
                    jobTypeStatistic.setCount(wedprJobTable.getCount());
                }
            }
            jobTypeStatisticList.add(jobTypeStatistic);
        }

        // query agencyJobTypeStatistic
        List<WedprAgency> wedprAgencyList = wedprAgencyService.list();
        List<WedprJobTable> jobTableList2 = wedprJobTableMapper.jobAgencyStatistic();
        List<WedprJobTable> jobTableList3 = wedprJobTableMapper.jobAgencyTypeStatistic();
        ArrayList<AgencyJobTypeStatistic> agencyJobTypeStatisticList =
                new ArrayList<>(jobTableList2.size());
        for (WedprAgency wedprAgency : wedprAgencyList) {
            AgencyJobTypeStatistic agencyJobTypeStatistic = new AgencyJobTypeStatistic();
            String agencyName = wedprAgency.getAgencyName();
            agencyJobTypeStatistic.setAgencyName(agencyName);
            agencyJobTypeStatistic.setTotalCount(0);
            for (WedprJobTable wedprJobTable2 : jobTableList2) {
                if (agencyName.equals(wedprJobTable2.getOwnerAgency())) {
                    agencyJobTypeStatistic.setTotalCount(wedprJobTable2.getCount());
                    List<JobTypeStatistic> jobTypeStatisticsList = new ArrayList<>();
                    for (WedprJobTable wedprJobTable3 : jobTableList3) {
                        if (wedprJobTable3
                                .getOwnerAgency()
                                .equals(wedprJobTable2.getOwnerAgency())) {
                            for (JobType jobTypeItem : jobTypes) {
                                String jobType = jobTypeItem.getType();
                                JobTypeStatistic jobTypeStatistic = new JobTypeStatistic();
                                jobTypeStatistic.setJobType(jobType);
                                jobTypeStatistic.setCount(0);
                                if (jobType.equals(wedprJobTable3.getJobType())) {
                                    jobTypeStatistic.setCount(wedprJobTable3.getCount());
                                }
                                jobTypeStatisticsList.add(jobTypeStatistic);
                            }
                        }
                    }
                    agencyJobTypeStatistic.setJobTypeStatistic(jobTypeStatisticsList);
                }
            }
            agencyJobTypeStatisticList.add(agencyJobTypeStatistic);
        }
        GetJobStatisticsResponse response = new GetJobStatisticsResponse();
        response.setJobOverview(jobOverview);
        response.setJobTypeStatistic(jobTypeStatisticList);
        response.setAgencyJobTypeStatistic(agencyJobTypeStatisticList);
        return response;
    }

    @Override
    public GetJobLineResponse getJobDateLine(GetJobDateLineRequest getJobDateLineRequest) {
        String startTime = getJobDateLineRequest.getStartTime();
        String endTime = getJobDateLineRequest.getEndTime();
        JobType[] jobTypeList = JobType.values();
        List<JobTypeStat> jobTypeStatList = new ArrayList<>();
        for (JobType jobTypeEnum : jobTypeList) {
            String jobType = jobTypeEnum.getType();
            List<WedprJobTable> jobList =
                    wedprJobTableMapper.getJobDateLine(jobType, startTime, endTime);
            JobTypeStat jobTypeStat = new JobTypeStat();
            jobTypeStat.setJobType(jobType);
            int size = jobList.size();
            List<String> dateList = new ArrayList<>(size);
            List<Integer> countList = new ArrayList<>(size);
            for (WedprJobTable wedprJobTable : jobList) {
                dateList.add(wedprJobTable.getCreateTime());
                countList.add(wedprJobTable.getCount());
            }
            jobTypeStat.setDateList(dateList);
            jobTypeStat.setCountList(countList);
            jobTypeStatList.add(jobTypeStat);
        }
        GetJobLineResponse response = new GetJobLineResponse();
        response.setJobTypeStat(jobTypeStatList);
        return response;
    }
}

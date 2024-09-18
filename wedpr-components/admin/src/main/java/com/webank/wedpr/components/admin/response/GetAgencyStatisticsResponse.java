package com.webank.wedpr.components.admin.response;

import lombok.Data;

import java.util.List;

/** Created by caryliao on 2024/9/10 9:26 */
@Data
public class GetAgencyStatisticsResponse {
    private Integer agencyTotalCount;
    private Integer faultAgencyCount;
    private List<AgencyInfo> agencyInfoList;
}

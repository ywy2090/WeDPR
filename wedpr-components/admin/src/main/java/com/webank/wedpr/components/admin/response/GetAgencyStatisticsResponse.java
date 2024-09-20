package com.webank.wedpr.components.admin.response;

import java.util.List;
import lombok.Data;

/** Created by caryliao on 2024/9/10 9:26 */
@Data
public class GetAgencyStatisticsResponse {
    private Integer agencyTotalCount;
    private Integer faultAgencyCount;
    private List<AgencyInfo> agencyInfoList;
}

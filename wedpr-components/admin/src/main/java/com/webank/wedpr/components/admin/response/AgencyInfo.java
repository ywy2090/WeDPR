package com.webank.wedpr.components.admin.response;

import lombok.Data;

import java.util.List;

/**
 * Created by caryliao on 2024/9/14 11:00
 */
@Data
public class AgencyInfo {
    private String agencyName;
    private Boolean agencyStatus;
    private List<PeerAgency> peerAgencyList;
}

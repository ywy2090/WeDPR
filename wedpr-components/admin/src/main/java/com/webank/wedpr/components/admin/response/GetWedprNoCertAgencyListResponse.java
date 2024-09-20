package com.webank.wedpr.components.admin.response;

import lombok.Data;

import java.util.List;

/** Created by caryliao on 2024/8/22 23:23 */
@Data
public class GetWedprNoCertAgencyListResponse {
    private List<WedprAgencyWithoutCertDTO> agencyList;
}

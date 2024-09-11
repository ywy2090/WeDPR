package com.webank.wedpr.components.admin.request;

import lombok.Data;

@Data
public class GetDatasetDateLineRequest {
    private String startTime;
    private String endTime;
}

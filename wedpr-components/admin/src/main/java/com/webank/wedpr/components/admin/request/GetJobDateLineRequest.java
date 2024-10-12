package com.webank.wedpr.components.admin.request;

import lombok.Data;

@Data
public class GetJobDateLineRequest {
    private String startTime;
    private String endTime;
}

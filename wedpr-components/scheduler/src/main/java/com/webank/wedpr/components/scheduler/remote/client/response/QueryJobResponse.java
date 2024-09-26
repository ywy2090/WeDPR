package com.webank.wedpr.components.scheduler.remote.client.response;

import com.webank.wedpr.components.scheduler.remote.client.JobData;
import lombok.Data;

@Data
public class QueryJobResponse {
    private int errorCode;
    private String message;

    private JobData data;
};

package com.webank.wedpr.components.scheduler.remote.client.response;

import lombok.Data;

@Data
public class KillJobResponse {
    private int errorCode;
    private String message;
}

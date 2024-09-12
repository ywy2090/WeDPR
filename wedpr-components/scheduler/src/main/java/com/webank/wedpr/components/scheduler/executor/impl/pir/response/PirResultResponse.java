package com.webank.wedpr.components.scheduler.executor.impl.pir.response;

import lombok.Data;

/**
 * @author zachma
 * @date 2024/9/6
 */
@Data
public class PirResultResponse {
    String jobType;
    PirResultDetail detail;
}

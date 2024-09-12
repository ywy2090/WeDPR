package com.webank.wedpr.components.scheduler.executor.impl.pir.request;

import lombok.Data;

/**
 * @author zachma
 * @date 2024/9/5
 */
@Data
public class PirConfigBody {
    private String datasetId;
    private String[] exists;
    private String[] values;
}

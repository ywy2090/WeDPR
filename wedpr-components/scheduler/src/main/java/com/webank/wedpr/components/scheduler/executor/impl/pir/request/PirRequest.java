package com.webank.wedpr.components.scheduler.executor.impl.pir.request;

import java.util.List;
import lombok.Data;

/**
 * @author zachma
 * @date 2024/9/5
 */
@Data
public class PirRequest {
    private String datasetId;
    private String algorithmType;
    private List<String> searchIds;
}

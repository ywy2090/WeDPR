package com.webank.wedpr.components.admin.response;

import lombok.Data;

@Data
public class DatasetTypeStatistic {
    private String datasetType;
    private Integer count;
    private String usedProportion;
}

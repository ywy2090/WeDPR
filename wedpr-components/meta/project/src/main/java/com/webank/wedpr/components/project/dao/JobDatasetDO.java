package com.webank.wedpr.components.project.dao;

import lombok.Data;

/** Created by caryliao on 2024/9/6 22:43 */
@Data
public class JobDatasetDO {
    private String jobId;
    private String datasetId;
    private Integer reportStatus;
    private String createTime;
    private Integer limitItems;
}

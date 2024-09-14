package com.webank.wedpr.components.admin.response;

import java.util.List;
import lombok.Data;

@Data
public class AgencyDatasetTypeStatistic {
    private String agencyName;
    private List<DatasetTypeStatistic> datasetTypeStatistic;
    private int totalCount;
}

package com.webank.wedpr.components.scheduler.local.executor.impl.mpc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.webank.wedpr.components.scheduler.local.executor.impl.model.DatasetInfo;
import com.webank.wedpr.core.protocol.JobType;
import com.webank.wedpr.core.utils.WeDPRException;
import java.util.List;

public class MPCJobParam {
    @JsonIgnore private transient String jobID;
    @JsonIgnore private transient JobType jobType;
    // the dataset information
    private List<DatasetInfo> dataSetList;

    public void check() throws Exception {
        if (dataSetList == null || dataSetList.isEmpty()) {
            throw new WeDPRException(
                    "Invalid model job param, must define the dataSet information!");
        }
        if (this.jobType == null) {
            throw new WeDPRException("Invalid model job param, must define the job type!");
        }
    }

    public Object toPSIRequest() {
        return null;
    }
}

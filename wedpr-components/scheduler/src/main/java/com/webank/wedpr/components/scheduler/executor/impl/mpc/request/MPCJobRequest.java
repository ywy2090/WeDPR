package com.webank.wedpr.components.scheduler.executor.impl.mpc.request;

import java.util.List;
import lombok.Data;

@Data
public class MPCJobRequest {

    @Data
    public static class MPCDatasetInfo {
        private long datasetRecordCount;
        private String datasetPath;
        private boolean receiveResult;
    }

    private String jobId;
    private String taskID;
    private String sql;
    private String mpcContent;
    private int selfIndex;
    private int partyCount;
    private String inputFilePath;
    // private String outputFilePath;
    private boolean withPSI = false;
    private boolean receiveResult = false;

    private List<MPCDatasetInfo> datasetList;
}

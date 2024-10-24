package com.webank.wedpr.components.scheduler.executor.impl.mpc;

import com.webank.wedpr.components.project.dao.JobDO;
import com.webank.wedpr.components.scheduler.executor.ExecuteResult;
import com.webank.wedpr.components.scheduler.executor.Executor;
import com.webank.wedpr.components.scheduler.executor.impl.mpc.request.MPCJobRequest;

public class MPCExecutor implements Executor {
    @Override
    public Object prepare(JobDO jobDO) throws Exception {
        // get the jobParam
        MPCJobParam jobParam = (MPCJobParam) jobDO.getJobParam();

        MPCJobRequest mpcJobRequest = (MPCJobRequest) jobParam.toMPCJobRequest();

        mpcJobRequest.setTaskID(jobDO.getTaskID());
        return mpcJobRequest;
    }

    @Override
    public void execute(JobDO jobDO) throws Exception {}

    @Override
    public void kill(JobDO jobDO) throws Exception {}

    @Override
    public ExecuteResult queryStatus(String jobID) throws Exception {
        return null;
    }
}

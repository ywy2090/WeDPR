package com.webank.wedpr.components.scheduler.local.executor.impl.mpc;

import com.webank.wedpr.components.project.dao.JobDO;
import com.webank.wedpr.components.scheduler.local.executor.ExecuteResult;
import com.webank.wedpr.components.scheduler.local.executor.Executor;

public class MPCExecutor implements Executor {
    @Override
    public Object prepare(JobDO jobDO) throws Exception {
        return null;
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

package com.webank.wedpr.components.scheduler.service;

import com.webank.wedpr.components.scheduler.executor.impl.pir.request.PirRequest;
import com.webank.wedpr.core.utils.WeDPRResponse;

/**
 * @author zachma
 * @date 2024/9/5
 */
public interface PirService {
    WeDPRResponse createPirProject(String user, PirRequest pirRequest);
}

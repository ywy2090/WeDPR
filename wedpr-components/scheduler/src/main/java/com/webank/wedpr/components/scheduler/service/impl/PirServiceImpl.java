package com.webank.wedpr.components.scheduler.service.impl;

import com.webank.wedpr.components.scheduler.executor.impl.pir.PirExecutor;
import com.webank.wedpr.components.scheduler.executor.impl.pir.request.PirConfigBody;
import com.webank.wedpr.components.scheduler.executor.impl.pir.request.PirRequest;
import com.webank.wedpr.components.scheduler.executor.impl.pir.request.PirServiceRequest;
import com.webank.wedpr.components.scheduler.executor.impl.pir.response.PirResultResponse;
import com.webank.wedpr.components.scheduler.service.PirService;
import com.webank.wedpr.core.config.WeDPRCommonConfig;
import com.webank.wedpr.core.utils.Constant;
import com.webank.wedpr.core.utils.ObjectMapperFactory;
import com.webank.wedpr.core.utils.WeDPRResponse;
import org.springframework.stereotype.Service;

/**
 * @author zachma
 * @date 2024/9/5
 */
@Service
public class PirServiceImpl implements PirService {
    @Override
    public WeDPRResponse createPirProject(String user, PirRequest pirRequest) {
        PirConfigBody pirConfigBody = new PirConfigBody();
        pirConfigBody.setDatasetId(pirRequest.getDatasetId());
        pirConfigBody.setValues(new String[] {"*"});
        pirConfigBody.setExists(new String[] {"*"});

        PirServiceRequest pirServiceRequest = new PirServiceRequest();
        pirServiceRequest.setUser(user);
        pirServiceRequest.setAgency(WeDPRCommonConfig.getAgency());
        pirServiceRequest.setSearchIds(pirRequest.getSearchIds());
        pirServiceRequest.setServiceConfigBody(pirConfigBody);
        pirServiceRequest.setAlgorithmType(pirRequest.getAlgorithmType());

        try {
            PirExecutor pirExecutor = new PirExecutor();
            String send = pirExecutor.send(pirServiceRequest);
            PirResultResponse pirResultResponse =
                    ObjectMapperFactory.getObjectMapper().readValue(send, PirResultResponse.class);
            return new WeDPRResponse(
                    Constant.WEDPR_SUCCESS, Constant.WEDPR_SUCCESS_MSG, pirResultResponse);
        } catch (Exception e) {
            return new WeDPRResponse(Constant.WEDPR_FAILED, "运行PIR任务失败: " + e.getMessage());
        }
    }
}

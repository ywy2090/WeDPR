package com.webank.wedpr.components.scheduler.executor.impl.pir.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedpr.core.utils.ObjectMapperFactory;
import java.util.List;
import lombok.Data;

/**
 * @author zachma
 * @date 2024/9/5
 */
@Data
public class PirServiceRequest {
    private String algorithmType;
    private List<String> searchIds;
    private PirConfigBody serviceConfigBody;
    private String user;
    private String agency;

    public String serialize() throws JsonProcessingException {
        return ObjectMapperFactory.getObjectMapper().writeValueAsString(this);
    }
}

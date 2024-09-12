package com.webank.wedpr.components.scheduler.executor.impl.pir.response;

import lombok.Data;

/**
 * @author zachma
 * @date 2024/9/6
 */
@Data
public class PirResultBody {
    String searchId;
    Boolean searchExist;
    String searchValue;
}

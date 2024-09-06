package com.webank.wedpr.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportStatusEnum {

    /** 未上报 */
    NO_REPORT(0),

    /** 已上报 */
    DO_REPORT(1);

    private Integer reportStatus;
}

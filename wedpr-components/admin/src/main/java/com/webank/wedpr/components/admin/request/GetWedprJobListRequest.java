package com.webank.wedpr.components.admin.request;

import com.webank.wedpr.core.utils.Constant;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetWedprJobListRequest {
    @NotBlank private String projectName;
    private String ownerAgency;
    private String owner;
    private String name;
    private String status;
    private String startTime;
    private String endTime;
    private Integer pageNum = Constant.DEFAULT_PAGE_NUM;

    @Min(value = 5, message = "分页条数最小不能小于5")
    @Max(value = 10000, message = "分页条数最大不能大于10000")
    private Integer pageSize = Constant.DEFAULT_PAGE_SIZE;
}

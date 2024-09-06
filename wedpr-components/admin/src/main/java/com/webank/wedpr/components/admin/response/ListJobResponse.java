package com.webank.wedpr.components.admin.response;

import com.webank.wedpr.components.admin.entity.WedprJobTable;
import java.util.List;
import lombok.Data;

/** Created by caryliao on 2024/9/5 9:35 */
@Data
public class ListJobResponse {
    private Long total;
    private List<WedprJobTable> jobList;
}

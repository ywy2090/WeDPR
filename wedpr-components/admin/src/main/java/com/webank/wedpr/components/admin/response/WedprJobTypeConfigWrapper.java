package com.webank.wedpr.components.admin.response;

import java.util.List;
import lombok.Data;

@Data
public class WedprJobTypeConfigWrapper {
    private String version;
    private List<JobTypeConfig> templates;
}

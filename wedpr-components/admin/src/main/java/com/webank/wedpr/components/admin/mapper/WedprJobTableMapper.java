package com.webank.wedpr.components.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.webank.wedpr.components.admin.entity.WedprJobTable;
import java.util.List;

/**
 * Mapper 接口
 *
 * @author caryliao
 * @since 2024-09-04
 */
public interface WedprJobTableMapper extends BaseMapper<WedprJobTable> {
    List<WedprJobTable> jobTypeStatistic();

    List<WedprJobTable> jobAgencyStatistic();

    List<WedprJobTable> jobAgencyTypeStatistic();
}

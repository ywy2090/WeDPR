package com.webank.wedpr.components.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedpr.components.admin.entity.WedprJobDatasetRelation;
import com.webank.wedpr.components.admin.mapper.WedprJobDatasetRelationMapper;
import com.webank.wedpr.components.admin.service.WedprJobDatasetRelationService;
import org.springframework.stereotype.Service;

/**
 * 服务实现类
 *
 * @author caryliao
 * @since 2024-09-10
 */
@Service
public class WedprJobDatasetRelationServiceImpl
        extends ServiceImpl<WedprJobDatasetRelationMapper, WedprJobDatasetRelation>
        implements WedprJobDatasetRelationService {}

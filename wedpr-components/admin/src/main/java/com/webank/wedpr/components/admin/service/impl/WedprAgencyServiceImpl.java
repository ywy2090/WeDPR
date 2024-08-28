package com.webank.wedpr.components.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedpr.components.admin.common.Utils;
import com.webank.wedpr.components.admin.entity.WedprAgency;
import com.webank.wedpr.components.admin.entity.WedprCert;
import com.webank.wedpr.components.admin.mapper.WedprAgencyMapper;
import com.webank.wedpr.components.admin.request.CreateOrUpdateWedprAgencyRequest;
import com.webank.wedpr.components.admin.request.GetWedprAgencyListRequest;
import com.webank.wedpr.components.admin.request.SetWedprAgencyRequest;
import com.webank.wedpr.components.admin.response.GetWedprAgencyDetailResponse;
import com.webank.wedpr.components.admin.response.GetWedprAgencyListResponse;
import com.webank.wedpr.components.admin.response.WedprAgencyDTO;
import com.webank.wedpr.components.admin.service.WedprAgencyService;
import com.webank.wedpr.components.admin.service.WedprCertService;
import com.webank.wedpr.components.token.auth.model.UserToken;
import com.webank.wedpr.core.protocol.CertStatusViewEnum;
import com.webank.wedpr.core.utils.Constant;
import com.webank.wedpr.core.utils.WeDPRException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 服务实现类
 *
 * @author caryliao
 * @since 2024-08-22
 */
@Service
public class WedprAgencyServiceImpl extends ServiceImpl<WedprAgencyMapper, WedprAgency>
        implements WedprAgencyService {

    @Autowired private WedprCertService wedprCertService;

    public String createOrUpdateAgency(
            CreateOrUpdateWedprAgencyRequest createOrUpdateWedprAgencyRequest, UserToken userToken)
            throws WeDPRException {
        String username = userToken.getUsername();
        String agencyId = createOrUpdateWedprAgencyRequest.getAgencyId();
        if (StringUtils.isEmpty(agencyId)) {
            LambdaQueryWrapper<WedprAgency> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            String agencyName = createOrUpdateWedprAgencyRequest.getAgencyName();
            lambdaQueryWrapper.eq(WedprAgency::getAgencyName, agencyName);
            WedprAgency queriedWedprAgency = getOne(lambdaQueryWrapper);
            // check agencyName
            if (queriedWedprAgency != null) {
                throw new WeDPRException(Constant.WEDPR_FAILED, "agencyName is already exists");
            } else {
                // save agency
                WedprAgency wedprAgency = new WedprAgency();
                wedprAgency.setAgencyName(createOrUpdateWedprAgencyRequest.getAgencyName());
                wedprAgency.setAgencyContact(createOrUpdateWedprAgencyRequest.getAgencyContact());
                wedprAgency.setContactPhone(createOrUpdateWedprAgencyRequest.getContactPhone());
                wedprAgency.setAgencyDesc(createOrUpdateWedprAgencyRequest.getAgencyDesc());
                wedprAgency.setGatewayEndpoint(
                        createOrUpdateWedprAgencyRequest.getGatewayEndpoint());
                wedprAgency.setCreateBy(username);
                wedprAgency.setUpdateBy(username);
                save(wedprAgency);
                return wedprAgency.getAgencyId();
            }
        } else {
            WedprAgency queriedWedprAgency = getById(agencyId);
            if (queriedWedprAgency == null) {
                throw new WeDPRException(Constant.WEDPR_FAILED, "agency does not exists");
            }
            // update agency
            queriedWedprAgency.setAgencyId(createOrUpdateWedprAgencyRequest.getAgencyId());
            queriedWedprAgency.setAgencyName(createOrUpdateWedprAgencyRequest.getAgencyName());
            queriedWedprAgency.setAgencyContact(
                    createOrUpdateWedprAgencyRequest.getAgencyContact());
            queriedWedprAgency.setContactPhone(createOrUpdateWedprAgencyRequest.getContactPhone());
            queriedWedprAgency.setAgencyDesc(createOrUpdateWedprAgencyRequest.getAgencyDesc());
            queriedWedprAgency.setGatewayEndpoint(
                    createOrUpdateWedprAgencyRequest.getGatewayEndpoint());
            queriedWedprAgency.setUpdateBy(username);
            queriedWedprAgency.setUpdateTime(LocalDateTime.now());
            updateById(queriedWedprAgency);
            return agencyId;
        }
    }

    @Override
    public GetWedprAgencyListResponse getWedprAgencyList(GetWedprAgencyListRequest request) {
        // get wedpr list
        LambdaQueryWrapper<WedprAgency> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        String agencyId = request.getAgencyId();
        String agencyName = request.getAgencyName();
        if (!StringUtils.isEmpty(agencyId)) {
            lambdaQueryWrapper.like(WedprAgency::getAgencyId, agencyId);
        }
        if (!StringUtils.isEmpty(agencyName)) {
            lambdaQueryWrapper.like(WedprAgency::getAgencyName, agencyName);
        }
        lambdaQueryWrapper.orderByDesc(WedprAgency::getCreateTime);
        Page<WedprAgency> wedprAgencyPage = new Page<>(request.getPageNum(), request.getPageSize());
        Page<WedprAgency> page = page(wedprAgencyPage, lambdaQueryWrapper);
        GetWedprAgencyListResponse response = new GetWedprAgencyListResponse();
        response.setTotal(page.getTotal());
        List<WedprAgency> wedprAgencyList = page.getRecords();
        List<WedprAgencyDTO> wedprAgencyDTOList = new ArrayList<>();
        for (WedprAgency wedprAgency : wedprAgencyList) {
            WedprAgencyDTO wedprAgencyDTO = new WedprAgencyDTO();
            WedprCert wedprCert =
                    wedprCertService.getOne(
                            new LambdaQueryWrapper<WedprCert>()
                                    .eq(WedprCert::getAgencyName, wedprAgency.getAgencyName()));
            if (wedprCert == null) {
                wedprAgencyDTO.setCertStatus(CertStatusViewEnum.NO_CERT.getStatusValue());
            } else {
                wedprAgencyDTO.setCertStatus(
                        Utils.getCertStatusView(
                                wedprCert.getCertStatus(), wedprCert.getExpireTime()));
            }
            wedprAgencyDTO.setAgencyId(wedprAgency.getAgencyId());
            wedprAgencyDTO.setAgencyName(wedprAgency.getAgencyName());
            wedprAgencyDTO.setAgencyContact(wedprAgency.getAgencyContact());
            wedprAgencyDTO.setContactPhone(wedprAgency.getContactPhone());
            wedprAgencyDTO.setCreateTime(wedprAgency.getCreateTime());
            wedprAgencyDTO.setUserCount(wedprAgency.getUserCount());
            wedprAgencyDTO.setAgencyStatus(wedprAgency.getAgencyStatus());
            wedprAgencyDTOList.add(wedprAgencyDTO);
        }
        response.setWedprAgencyDTOList(wedprAgencyDTOList);
        return response;
    }

    @Override
    public GetWedprAgencyDetailResponse getWedprAgencyDetail(String agencyId)
            throws WeDPRException {
        WedprAgency wedprAgency = checkAgencyExist(agencyId);
        GetWedprAgencyDetailResponse response = new GetWedprAgencyDetailResponse();
        response.setAgencyId(wedprAgency.getAgencyId());
        response.setAgencyName(wedprAgency.getAgencyName());
        response.setAgencyDesc(wedprAgency.getAgencyDesc());
        response.setAgencyContact(wedprAgency.getAgencyContact());
        response.setContactPhone(wedprAgency.getContactPhone());
        response.setGatewayEndpoint(wedprAgency.getGatewayEndpoint());
        return response;
    }

    @Override
    public void deleteWedprAgency(String agencyId) throws WeDPRException {
        checkAgencyExist(agencyId);
        removeById(agencyId);
    }

    @Override
    public void setWedprAgency(SetWedprAgencyRequest setWedprAgencyRequest) throws WeDPRException {
        WedprAgency wedprAgency = checkAgencyExist(setWedprAgencyRequest.getAgencyId());
        wedprAgency.setAgencyStatus(setWedprAgencyRequest.getAgencyStatus());
        updateById(wedprAgency);
    }

    private WedprAgency checkAgencyExist(String agencyId) throws WeDPRException {
        WedprAgency wedprAgency = getById(agencyId);
        if (wedprAgency == null) {
            throw new WeDPRException("agency does not exist");
        }
        return wedprAgency;
    }
}

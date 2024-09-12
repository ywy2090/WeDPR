package com.webank.wedpr.components.scheduler.executor.impl.pir;

import com.webank.wedpr.components.scheduler.executor.impl.pir.request.PirRequest;
import com.webank.wedpr.components.scheduler.service.PirService;
import com.webank.wedpr.components.token.auth.TokenUtils;
import com.webank.wedpr.core.utils.Constant;
import com.webank.wedpr.core.utils.WeDPRResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zachma
 * @date 2024/9/5
 */
@RestController
@RequestMapping(
        path = Constant.WEDPR_API_PREFIX + "/project/pir/",
        produces = {"application/json"})
public class PirController {
    private static final Logger logger = LoggerFactory.getLogger(PirController.class);

    @Autowired private PirService pirService;

    @PostMapping("/create")
    public WeDPRResponse createPirProject(
            @RequestBody PirRequest pirRequest, HttpServletRequest request) {
        try {
            return pirService.createPirProject(
                    TokenUtils.getLoginUser(request).getUsername(), pirRequest);
        } catch (Exception e) {
            logger.warn("createProject exception, error: ", e);
            return new WeDPRResponse(
                    Constant.WEDPR_FAILED, "createProject failed for " + e.getMessage());
        }
    }
}

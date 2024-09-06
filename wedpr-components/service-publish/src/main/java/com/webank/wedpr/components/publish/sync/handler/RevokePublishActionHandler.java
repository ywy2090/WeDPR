package com.webank.wedpr.components.publish.sync.handler;

import com.webank.wedpr.components.publish.entity.WedprPublishedService;
import com.webank.wedpr.components.publish.helper.JsonHelper;
import com.webank.wedpr.components.publish.service.WedprPublishedServiceService;
import com.webank.wedpr.components.publish.sync.PublishSyncAction;
import com.webank.wedpr.core.utils.WeDPRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zachma
 * @date 2024/8/28
 */
public class RevokePublishActionHandler implements PublishActionHandler {
    private static final Logger logger = LoggerFactory.getLogger(RevokePublishActionHandler.class);

    @Override
    public void handle(String content, PublishActionContext context) throws WeDPRException {
        try {
            WedprPublishedService wedprPublishedService =
                    JsonHelper.jsonString2Object(content, WedprPublishedService.class);
            WedprPublishedServiceService wedprPublishService = context.getWedprPublishedService();
            wedprPublishService.syncPublishService(PublishSyncAction.REVOKE, wedprPublishedService);
        } catch (Exception e) {
            logger.error("sync revoke publish service failed: " + content);
            throw new WeDPRException(e);
        }
    }
}
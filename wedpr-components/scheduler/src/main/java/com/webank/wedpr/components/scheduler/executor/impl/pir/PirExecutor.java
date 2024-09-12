package com.webank.wedpr.components.scheduler.executor.impl.pir;

import com.webank.wedpr.components.http.client.HttpClientPool;
import com.webank.wedpr.components.scheduler.executor.impl.pir.request.PirServiceRequest;
import com.webank.wedpr.core.config.WeDPRConfig;
import com.webank.wedpr.core.utils.Constant;
import com.webank.wedpr.core.utils.WeDPRException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * @author zachma
 * @date 2024/9/5
 */
public class PirExecutor {
    private static final String PIR_URL = WeDPRConfig.apply("wedpr.executor.pir.url", null, true);

    private static final Integer CONNECTION_REQUEST_TIME_OUT =
            WeDPRConfig.apply("wedpr.executor.pir.connect.request.timeout.ms", 10000);
    private static final Integer CONNECTION_TIME_OUT =
            WeDPRConfig.apply("wedpr.executor.pir.connect.timeout.ms", 5000);
    private static final Integer REQUEST_TIMEOUT =
            WeDPRConfig.apply("wedpr.executor.pir.request.timeout.ms", 60000);
    private static final Integer MAX_CONNECTION_TOTAL =
            WeDPRConfig.apply("wedpr.executor.pir.max.total.connection", 5);

    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String DEFAULT_CONTENT_TYPE = "application/json";

    private RequestConfig buildConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIME_OUT)
                .setConnectTimeout(CONNECTION_TIME_OUT)
                .setSocketTimeout(REQUEST_TIMEOUT)
                .build();
    }

    private String getPirUrl() {
        return PIR_URL;
    }

    public String send(PirServiceRequest pirServiceRequest) throws WeDPRException {
        try {
            CloseableHttpClient httpClient =
                    HttpClientPool.getHttpClient(MAX_CONNECTION_TOTAL, buildConfig());
            HttpPost httpPost = new HttpPost(getPirUrl());
            StringEntity requestEntity = new StringEntity(pirServiceRequest.serialize());
            httpPost.setEntity(requestEntity);
            httpPost.setHeader(CONTENT_TYPE_KEY, DEFAULT_CONTENT_TYPE);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != Constant.HTTP_SUCCESS) {
                throw new WeDPRException("任务发起失败");
            }
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            throw new WeDPRException("发起任务失败");
        }
    }
}

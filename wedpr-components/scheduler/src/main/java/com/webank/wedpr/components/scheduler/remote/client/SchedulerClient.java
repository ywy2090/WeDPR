package com.webank.wedpr.components.scheduler.remote.client;

import com.webank.wedpr.components.http.client.HttpClientImpl;
import com.webank.wedpr.components.scheduler.local.executor.ExecuteResult;
import com.webank.wedpr.components.scheduler.remote.api.SchedulerClientApi;
import com.webank.wedpr.components.scheduler.remote.client.response.KillJobResponse;
import com.webank.wedpr.components.scheduler.remote.client.response.PostJobResponse;
import com.webank.wedpr.components.scheduler.remote.client.response.QueryJobResponse;
import com.webank.wedpr.components.scheduler.remote.config.SchedulerConfig;
import com.webank.wedpr.core.utils.ObjectMapperFactory;
import com.webank.wedpr.core.utils.WeDPRException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.mortbay.jetty.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerClient implements SchedulerClientApi {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerClient.class);

    private final HttpClientImpl httpClient;

    private final int SCHEDULER_SERVICE_RESPONSE_OK = 0;

    public SchedulerClient(String url, Integer maxConnTotal, RequestConfig requestConfig) {
        this.httpClient = new HttpClientImpl(url, maxConnTotal, requestConfig, null);
    }

    @Override
    public void postJob(String jobId, String request) throws Exception {

        String url = SchedulerConfig.getSchedulerApiUrl(jobId);
        String response = null;
        try {
            response =
                    this.httpClient.executePostAndGetString(
                            url, request, HttpStatus.ORDINAL_200_OK);

            if (logger.isDebugEnabled()) {
                logger.debug(
                        "post job, jobId: {}, url: {}, request: {}, response: {}",
                        jobId,
                        url,
                        request,
                        response);
            }

        } catch (Exception e) {
            logger.error("post job exception, jobId: {}, url: {}, e: ", jobId, url, e);
            throw e;
        }

        PostJobResponse postJobResponse =
                ObjectMapperFactory.getObjectMapper().readValue(response, PostJobResponse.class);

        if (postJobResponse.getErrorCode() != SCHEDULER_SERVICE_RESPONSE_OK) {
            logger.error("post job failed, jobId: {}, response: {}", jobId, postJobResponse);
            throw new WeDPRException("post job failed, errorMsg: " + postJobResponse.getMessage());
        } else {
            logger.debug("post job successfully, jobId: {}, response: {}", jobId, postJobResponse);
        }
    }

    @Override
    public void killJob(String jobId) throws Exception {

        String response = null;
        try {
            String url = SchedulerConfig.getSchedulerApiUrl(jobId);

            HttpDelete httpDelete = new HttpDelete(url);
            response = this.httpClient.executeAndGetString(httpDelete);

            if (logger.isDebugEnabled()) {
                logger.debug("kill job, jobId: {}, url: {}, response: {}", jobId, url, response);
            }

        } catch (Exception e) {
            logger.error("kill job exception, jobId: {}, e: ", jobId, e);
            throw e;
        }

        KillJobResponse killJobResponse =
                ObjectMapperFactory.getObjectMapper().readValue(response, KillJobResponse.class);

        if (killJobResponse.getErrorCode() != SCHEDULER_SERVICE_RESPONSE_OK) {
            logger.error("kill job failed, jobId: {}, response: {}", jobId, killJobResponse);
            throw new WeDPRException(
                    "kill job failed, jobId: "
                            + jobId
                            + ", errorMsg: "
                            + killJobResponse.getMessage());
        } else {
            logger.info("kill job successfully, jobId: {}, response: {}", jobId, killJobResponse);
        }
    }

    @Override
    public ExecuteResult queryJob(String jobId) throws Exception {

        String response = null;
        try {
            String url = SchedulerConfig.getSchedulerApiUrl(jobId);

            HttpGet httpGet = new HttpGet(url);
            response = this.httpClient.executeAndGetString(httpGet);

            if (logger.isInfoEnabled()) {
                logger.info("query job, jobId: {}, url: {}, response: {}", jobId, url, response);
            }

        } catch (Exception e) {
            logger.error("query job exception, jobId: {}, e: ", jobId, e);
            throw e;
        }

        QueryJobResponse queryJobResponse =
                ObjectMapperFactory.getObjectReader().readValue(response, QueryJobResponse.class);

        if (queryJobResponse.getErrorCode() != SCHEDULER_SERVICE_RESPONSE_OK) {
            logger.error(
                    "query job status failed, jobId: {}, response: {}", jobId, queryJobResponse);
            throw new WeDPRException(
                    "query job status failed, jobId: "
                            + jobId
                            + ", errorMsg: "
                            + queryJobResponse.getMessage());
        } else {
            logger.info(
                    "query job status successfully, jobId: {}, response: {}",
                    jobId,
                    queryJobResponse);

            JobData data = queryJobResponse.getData();

            ExecuteResult.ResultStatus resultStatus =
                    ExecuteResult.ResultStatus.valueOf(queryJobResponse.getData().getStatus());

            ExecuteResult executeResult = new ExecuteResult();
            executeResult.setResultStatus(resultStatus);
            executeResult.setMsg("");

            return executeResult;
        }
    }
}

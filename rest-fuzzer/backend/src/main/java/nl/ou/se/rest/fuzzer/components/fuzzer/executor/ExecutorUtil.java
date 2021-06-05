package nl.ou.se.rest.fuzzer.components.fuzzer.executor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzRequest;
import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzResponse;
import nl.ou.se.rest.fuzzer.components.data.fuz.factory.FuzResponseFactory;
import nl.ou.se.rest.fuzzer.components.shared.Constants;

@Service
public class ExecutorUtil {

    // variable(s)
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private static final int TIMEOUT_MS = 5 * 1000;

    private FuzResponseFactory responseFactory = new FuzResponseFactory();

    private CloseableHttpClient httpClient;
    private Authentication authentication;

    // constructor(s)
    private ExecutorUtil() {
        this.init();
    }

    // method(s)
    private void init() {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(TIMEOUT_MS)
                .setConnectionRequestTimeout(TIMEOUT_MS).setSocketTimeout(TIMEOUT_MS).build();

        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    public void destroy() {
        try {
            httpClient.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public FuzResponse processRequest(FuzRequest request) {
        HttpResponse response = null;
        String failureReason = null;

        LocalDateTime ldt = LocalDateTime.now();
        try {
            HttpUriRequest httpUriRequest = ExecutorUtilHelper.getRequest(request);

            // basic authentication
            if (this.authentication != null && (this.authentication instanceof BasicAuthentication)) {
                BasicAuthentication basicAuthentication = (BasicAuthentication) authentication;
                if (!StringUtils.isBlank(basicAuthentication.getUsername())
                        && !StringUtils.isBlank(basicAuthentication.getPassword())) {
                    httpUriRequest.setHeader(HttpHeaders.AUTHORIZATION, basicAuthentication.getHeader());
                }
            }

            if (httpUriRequest != null) {
                response = httpClient.execute(httpUriRequest);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            failureReason = e.getMessage();
        }
        Long ms = ldt.until(LocalDateTime.now(), ChronoUnit.MILLIS);

        logger.info(String.format(Constants.Fuzzer.EXECUTION_TIME, ms));

        return createFuzResponse(request, response, failureReason);
    }

    private FuzResponse createFuzResponse(FuzRequest request, HttpResponse response, String failureReason) {
        responseFactory.create(request.getProject(), request);

        if (response != null) {
            responseFactory.setCode(response.getStatusLine().getStatusCode());
            responseFactory.setDescription(response.getStatusLine().getReasonPhrase());

            String body = null;
            try {
                body = EntityUtils.toString(response.getEntity());
            } catch (ParseException | IOException e) {
                logger.error(e.getMessage());
            }
            responseFactory.setBody(body);
        }

        if (failureReason != null) {
            responseFactory.setFailureReason(failureReason);
        }

        return responseFactory.build();
    }
}
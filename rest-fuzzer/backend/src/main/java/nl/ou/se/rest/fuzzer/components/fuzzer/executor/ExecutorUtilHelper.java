package nl.ou.se.rest.fuzzer.components.fuzzer.executor;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzRequest;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.ParameterContext;
import nl.ou.se.rest.fuzzer.components.shared.Constants;
import nl.ou.se.rest.fuzzer.components.shared.JsonUtil;

public abstract class ExecutorUtilHelper {

    // variable(s)
    private static Logger logger = LoggerFactory.getLogger(ExecutorUtilHelper.class);

    private static final String PLACEHOLDER_PATH_VARIABLE = "\\{%s\\}";

    private static final String HEADER_ACCEPT = "Accept";
    private static final String HEADER_CONTENT_TYPE = "Content-type";

    // method(s)
    public static HttpUriRequest getRequest(FuzRequest request) {
        HttpUriRequest httpUriRequest = null;

        switch (request.getHttpMethod()) {
        case GET:
            httpUriRequest = getGetRequest(request);
            break;
        case POST:
            httpUriRequest = getPostRequest(request);
            break;
        case PATCH:
            httpUriRequest = getPatchRequest(request);
            break;
        case PUT:
            httpUriRequest = getPutRequest(request);
            break;
        case DELETE:
            httpUriRequest = getDeleteRequest(request);
            break;
        default:
            logger.error(Constants.Fuzzer.INVALID_HTTP_METHOD);
            break;
        }

        return httpUriRequest;
    }

    private static HttpUriRequest getGetRequest(FuzRequest request) {
        HttpGet get = new HttpGet(getUri(request));
        setHeaders(get);
        return get;
    }

    private static HttpUriRequest getPostRequest(FuzRequest request) {
        HttpPost post = new HttpPost(getUri(request));
        setHeaders(post);
        post.setEntity(getEntity(request));
        return post;
    }

    private static HttpUriRequest getPatchRequest(FuzRequest request) {
        HttpPatch patch = new HttpPatch(getUri(request));
        setHeaders(patch);
        patch.setEntity(getEntity(request));
        return patch;
    }

    private static HttpUriRequest getPutRequest(FuzRequest request) {
        HttpPut put = new HttpPut(getUri(request));
        setHeaders(put);
        put.setEntity(getEntity(request));
        return put;
    }

    private static HttpUriRequest getDeleteRequest(FuzRequest request) {
        HttpDelete delete = new HttpDelete(getUri(request));
        setHeaders(delete);
        return delete;
    }

    private static void setHeaders(HttpUriRequest httpUriRequest) {
        httpUriRequest.setHeader(HEADER_ACCEPT, "application/json");
        httpUriRequest.setHeader(HEADER_CONTENT_TYPE, "application/json");
    }

    private static HttpEntity getEntity(FuzRequest request) {
        Map<String, Object> parameters = request.getParameterMap(ParameterContext.FORMDATA);
        String json = JsonUtil.mapToString(parameters);
        return jsonToEntity(json);
    }

    private static HttpEntity jsonToEntity(String json) {
        StringEntity entity = null;
        try {
            entity = new StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            logger.warn(String.format(Constants.Fuzzer.ENCODING_UNSUPPORTED, e.getMessage()));
        }
        return entity;
    }

    private static URI getUri(FuzRequest request) {
        URIBuilder uriBuilder = new URIBuilder();
        URI uri = null;
        try {
            uriBuilder.setScheme("http");
            uriBuilder.setHost(request.getProject().getSut().getHost());
            uriBuilder.setPath(getPath(request));

            // add query parameters
            for (Map.Entry<String, Object> entry : request.getParameterMap(ParameterContext.QUERY).entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue().toString());
            }

            uri = uriBuilder.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return uri;
    }

    private static String getPath(FuzRequest request) {
        StringBuilder path = new StringBuilder(request.getProject().getSut().getBasePath());

        // replace path parameters
        String updatedPath = request.getPath();
        for (Map.Entry<String, Object> entry : request.getParameterMap(ParameterContext.PATH).entrySet()) {
            updatedPath = updatedPath.replaceAll(String.format(PLACEHOLDER_PATH_VARIABLE, entry.getKey()),
                    entry.getValue().toString());
        }
        path.append(updatedPath);

        return path.toString();
    }
}
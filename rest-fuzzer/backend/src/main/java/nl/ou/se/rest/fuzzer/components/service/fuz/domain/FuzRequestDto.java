package nl.ou.se.rest.fuzzer.components.service.fuz.domain;

import java.time.LocalDateTime;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.HttpMethod;
import nl.ou.se.rest.fuzzer.components.service.rmd.domain.RmdActionDto;

public class FuzRequestDto {

    // variable(s)
    private Long id;
    private HttpMethod httpMethod;
    private String path;
    private String formdataParametersJson;
    private String headerParametersJson;
    private String pathParametersJson;
    private String queryParametersJson;
    private RmdActionDto action;
    private FuzResponseDto response;
    private FuzSequenceDto sequence;
    private LocalDateTime createdAt;

    // getter(s) and setter(s)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFormdataParametersJson() {
        return formdataParametersJson;
    }

    public void setFormdataParametersJson(String formdataParametersJson) {
        this.formdataParametersJson = formdataParametersJson;
    }

    public String getHeaderParametersJson() {
        return headerParametersJson;
    }

    public void setHeaderParametersJson(String headerParametersJson) {
        this.headerParametersJson = headerParametersJson;
    }

    public String getPathParametersJson() {
        return pathParametersJson;
    }

    public void setPathParametersJson(String pathParametersJson) {
        this.pathParametersJson = pathParametersJson;
    }

    public String getQueryParametersJson() {
        return queryParametersJson;
    }

    public void setQueryParametersJson(String queryParametersJson) {
        this.queryParametersJson = queryParametersJson;
    }

    public RmdActionDto getAction() {
        return action;
    }

    public void setAction(RmdActionDto action) {
        this.action = action;
    }

    public FuzResponseDto getResponse() {
        return response;
    }

    public void setResponse(FuzResponseDto response) {
        this.response = response;
    }

    public FuzSequenceDto getSequence() {
        return sequence;
    }

    public void setSequence(FuzSequenceDto sequence) {
        this.sequence = sequence;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
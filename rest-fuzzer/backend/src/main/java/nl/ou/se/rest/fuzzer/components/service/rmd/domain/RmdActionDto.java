package nl.ou.se.rest.fuzzer.components.service.rmd.domain;

import java.util.ArrayList;
import java.util.List;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.HttpMethod;

public class RmdActionDto {

    // variable(s)
    private Long id;
    private String path;
    private HttpMethod httpMethod;
    private List<RmdParameterDto> parameters = new ArrayList<>();
    private List<RmdResponseDto> responses = new ArrayList<>();
    private List<RmdActionDependencyDto> dependencies = new ArrayList<>();

    // getter(s) and setter(s)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public List<RmdParameterDto> getParameters() {
        return parameters;
    }

    public void setParameters(List<RmdParameterDto> parameters) {
        this.parameters = parameters;
    }

    public List<RmdResponseDto> getResponses() {
        return responses;
    }

    public void setResponses(List<RmdResponseDto> responses) {
        this.responses = responses;
    }

    public List<RmdActionDependencyDto> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<RmdActionDependencyDto> dependencies) {
        this.dependencies = dependencies;
    }
}

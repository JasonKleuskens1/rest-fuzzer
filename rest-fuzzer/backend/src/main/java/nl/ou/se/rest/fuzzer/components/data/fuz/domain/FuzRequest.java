package nl.ou.se.rest.fuzzer.components.data.fuz.domain;

import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import nl.ou.se.rest.fuzzer.components.data.fuz.factory.FuzRequestFactory;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.HttpMethod;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.ParameterContext;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdAction;
import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdParameter;
import nl.ou.se.rest.fuzzer.components.shared.Constants;
import nl.ou.se.rest.fuzzer.components.shared.JsonUtil;

@Entity(name = "fuz_requests")
public class FuzRequest implements Comparable<FuzRequest> {

    // variable(s)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    private String path;

    @NotNull
    @Enumerated(EnumType.STRING)
    private HttpMethod httpMethod;

    private String formdataParametersJson;
    private String headerParametersJson;
    private String pathParametersJson;
    private String queryParametersJson;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private FuzProject project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sequence_id")
    private FuzSequence sequence;

    @OneToOne(fetch = FetchType.LAZY, optional = false, mappedBy = "request")
    private FuzResponse response;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "action_id")
    private RmdAction action;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    // constructor(s)
    public FuzRequest() {
    }

    public FuzRequest(String path, HttpMethod httpMethod) {
        this.path = path;
        this.httpMethod = httpMethod;
    }

    // method(s)
    public int compareTo(FuzRequest other) {
        int projectCompare = this.getProject().compareTo(other.getProject());
        if (projectCompare != 0) {
            return projectCompare;
        }

        return this.getId().compareTo(other.getId());
    }

    public Map<String, Object> getParameterMap(ParameterContext context) {
        String json = null;

        switch (context) {
        case FORMDATA:
            json = this.formdataParametersJson;
            break;
        case HEADER:
            json = this.headerParametersJson;
            break;
        case PATH:
            json = this.pathParametersJson;
            break;
        case QUERY:
            json = this.queryParametersJson;
            break;
        default:
            break;
        }
        return JsonUtil.stringToMap(json);
    }

    public void setParameterMap(ParameterContext context, Map<String, Object> parameters) {
        String json = JsonUtil.mapToString(parameters);

        switch (context) {
        case FORMDATA:
            this.formdataParametersJson = json;
            break;
        case HEADER:
            this.headerParametersJson = json;
            break;
        case PATH:
            this.pathParametersJson = json;
            break;
        case QUERY:
            this.queryParametersJson = json;
            break;
        default:
            break;
        }
    }

    public void replaceParameterValue(RmdParameter parameter, String dictionaryValue) {
        Map<String, Object> map = this.getParameterMap(parameter.getContext());

        if (map != null && map.containsKey(parameter.getName())) {
            map.put(parameter.getName(), dictionaryValue);
            this.setParameterMap(parameter.getContext(), map);
        } else {
            throw new IllegalArgumentException(
                    String.format(Constants.Fuzzer.PARAMETER_UNKNOWN, parameter.getId(), parameter.getName()));
        }
    }

    public void replaceParameterValue(RmdParameter parameter, int dictionaryValue) {
        Map<String, Object> map = this.getParameterMap(parameter.getContext());

        if (map != null && map.containsKey(parameter.getName())) {
            map.put(parameter.getName(), dictionaryValue);
            this.setParameterMap(parameter.getContext(), map);
        } else {
            throw new IllegalArgumentException(
                    String.format(Constants.Fuzzer.PARAMETER_UNKNOWN, parameter.getId(), parameter.getName()));
        }
    }

    public void replaceParameterValue(RmdParameter parameter, boolean dictionaryValue) {
        Map<String, Object> map = this.getParameterMap(parameter.getContext());

        if (map != null && map.containsKey(parameter.getName())) {
            map.put(parameter.getName(), dictionaryValue);
            this.setParameterMap(parameter.getContext(), map);
        } else {
            throw new IllegalArgumentException(
                    String.format(Constants.Fuzzer.PARAMETER_UNKNOWN, parameter.getId(), parameter.getName()));
        }
    }

    public void removeParameter(RmdParameter parameter) {
        Map<String, Object> map = this.getParameterMap(parameter.getContext());

        if (map != null && map.containsKey(parameter.getName())) {
            map.remove(parameter.getName());
            this.setParameterMap(parameter.getContext(), map);
        }
    }

    public FuzRequest getDeepCopy() {
        FuzRequestFactory requestFactory = new FuzRequestFactory();

        requestFactory.create(this.project, this.action);
        for (ParameterContext pc : ParameterContext.values()) {
            requestFactory.addParameterMap(pc, this.getParameterMap(pc));
        }

        return requestFactory.build();
    }

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

    public FuzProject getProject() {
        return project;
    }

    public void setProject(FuzProject project) {
        this.project = project;
    }

    public FuzSequence getSequence() {
        return sequence;
    }

    public void setSequence(FuzSequence sequence) {
        this.sequence = sequence;
    }

    public FuzResponse getResponse() {
        return response;
    }

    public void setResponse(FuzResponse response) {
        this.response = response;
    }

    public RmdAction getAction() {
        return action;
    }

    public void setAction(RmdAction action) {
        this.action = action;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // toString
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
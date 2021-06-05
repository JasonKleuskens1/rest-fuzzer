package nl.ou.se.rest.fuzzer.components.data.rmd.domain;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.SortNatural;

@Entity(name = "rmd_actions")
public class RmdAction implements Comparable<RmdAction> {

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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "action_id")
    @SortNatural
    private SortedSet<RmdParameter> parameters = new TreeSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "action_id")
    @SortNatural
    private SortedSet<RmdResponse> responses = new TreeSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id")
    @SortNatural
    private SortedSet<RmdActionDependency> dependencies = new TreeSet<>();

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "sut_id")
    private RmdSut sut;

    // constructor(s)
    public RmdAction() {
    }

    public RmdAction(String path, String httpMethod) {
        this.path = path;
        this.httpMethod = HttpMethod.valueOf(httpMethod);
    }

    // method(s)
    public int compareTo(RmdAction other) {
        if (this.getSut() != null && other.getSut() != null) {
            int sutCompare = this.getSut().compareTo(other.getSut());
            if (sutCompare != 0) {
                return sutCompare;
            }
        }

        int pathCompare = this.getPath().compareTo(other.getPath());
        if (pathCompare != 0) {
            return pathCompare;
        }
        return this.getHttpMethod().compareTo(other.getHttpMethod());
    }

    public boolean equals(RmdAction other) {
        return this.getId().equals(other.getId());
    }

    public void addParameter(RmdParameter parameter) {
        parameter.setAction(this);
        this.getParameters().add(parameter);
    }

    public void addResponse(RmdResponse response) {
        response.setAction(this);
        this.getResponses().add(response);
    }

    public List<RmdParameter> getParametersByContext(ParameterContext context) {
        return this.getParameters().stream().filter(p -> context.equals(p.getContext())).collect(Collectors.toList());
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

    public SortedSet<RmdParameter> getParameters() {
        return parameters;
    }

    public void setParameters(SortedSet<RmdParameter> parameters) {
        this.parameters = parameters;
    }

    public SortedSet<RmdResponse> getResponses() {
        return responses;
    }

    public void setResponses(SortedSet<RmdResponse> responses) {
        this.responses = responses;
    }

    public SortedSet<RmdActionDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(SortedSet<RmdActionDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public RmdSut getSut() {
        return sut;
    }

    public void setSut(RmdSut sut) {
        this.sut = sut;
    }

    // toString
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
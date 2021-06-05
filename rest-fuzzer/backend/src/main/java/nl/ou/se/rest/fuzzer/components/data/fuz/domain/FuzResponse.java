package nl.ou.se.rest.fuzzer.components.data.fuz.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity(name = "fuz_responses")
public class FuzResponse implements Comparable<FuzResponse> {

    // variable(s)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int statusCode;

    @Column
    private String statusDescription;

    @Column
    private String body;

    @Column
    private String failureReason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    @NotNull
    private FuzProject project;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id")
    @NotNull
    private FuzRequest request;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    // method(s)
    public int compareTo(FuzResponse other) {
        int projectCompare = this.getProject().compareTo(other.getProject());
        if (projectCompare != 0) {
            return projectCompare;
        }

        return new String(this.getRequest().getPath() + this.getRequest().getHttpMethod())
                .compareTo(new String(other.getRequest().getPath() + other.getRequest().getHttpMethod()));
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public FuzProject getProject() {
        return project;
    }

    public void setProject(FuzProject project) {
        this.project = project;
    }

    public FuzRequest getRequest() {
        return request;
    }

    public void setRequest(FuzRequest request) {
        this.request = request;
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

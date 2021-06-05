package nl.ou.se.rest.fuzzer.components.data.report.domain;

import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProject;
import nl.ou.se.rest.fuzzer.components.shared.JsonUtil;

@Entity(name = "reports")
public class Report {

    // variable(s)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReportType type;

    @NotNull
    @NotEmpty
    private String description;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private FuzProject project;

    private String metaDataTuplesJson;

    private String output;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime completedAt;

    // method(s)
    public Map<String, Object> getMetaDataTuples() {
        return JsonUtil.stringToMap(this.metaDataTuplesJson);
    }

    public void setMetaDataTuples(Map<String, Object> metaDataTuples) {
        this.metaDataTuplesJson = JsonUtil.mapToString(metaDataTuples);
    }

    // getter(s) and setter(s)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FuzProject getProject() {
        return project;
    }

    public void setProject(FuzProject project) {
        this.project = project;
    }

    public String getMetaDataTuplesJson() {
        return metaDataTuplesJson;
    }

    public void setMetaDataTuplesJson(String metaDataTuplesJson) {
        this.metaDataTuplesJson = metaDataTuplesJson;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    // toString
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}

package nl.ou.se.rest.fuzzer.components.service.report.domain;

import java.time.LocalDateTime;

import nl.ou.se.rest.fuzzer.components.data.report.domain.ReportType;
import nl.ou.se.rest.fuzzer.components.service.fuz.domain.FuzProjectDto;

public class ReportDto {

    // variable(s)
    private Long id;
    private String description;
    private ReportType type;
    private FuzProjectDto project;
    private String metaDataTuplesJson;
    private String output;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    // getter(s) and setter(s)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public FuzProjectDto getProject() {
        return project;
    }

    public void setProject(FuzProjectDto project) {
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
}

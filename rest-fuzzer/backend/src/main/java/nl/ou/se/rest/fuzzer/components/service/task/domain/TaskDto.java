package nl.ou.se.rest.fuzzer.components.service.task.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TaskDto {

    // variable(s)
    public static final String STATUS_QUEUED = "QUEUED";
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_ENDED = "ENDED";

    private Long id;
    private String canonicalName;
    private String metaDataTuplesJson;
    private BigDecimal progress;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime crashedAt;
    private LocalDateTime finishedAt;

    // getter(s) and setter(s)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public String getMetaDataTuplesJson() {
        return metaDataTuplesJson;
    }

    public void setMetaDataTuplesJson(String metaDataTuplesJson) {
        this.metaDataTuplesJson = metaDataTuplesJson;
    }

    public BigDecimal getProgress() {
        return progress;
    }

    public void setProgress(BigDecimal progress) {
        this.progress = progress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCrashedAt() {
        return crashedAt;
    }

    public void setCrashedAt(LocalDateTime crashedAt) {
        this.crashedAt = crashedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}
package nl.ou.se.rest.fuzzer.components.service.fuz.domain;

import java.time.LocalDateTime;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzProjectType;
import nl.ou.se.rest.fuzzer.components.service.rmd.domain.RmdSutDto;

public class FuzProjectDto {

    // variable(s)
    private Long id;
    private String description;
    private FuzProjectType type;
    private String metaDataTuplesJson;
    private RmdSutDto sut;
    private LocalDateTime createdAt;

    // getters and setters
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
    public FuzProjectType getType() {
        return type;
    }
    public void setType(FuzProjectType type) {
        this.type = type;
    }
    public String getMetaDataTuplesJson() {
        return metaDataTuplesJson;
    }
    public void setMetaDataTuplesJson(String metaDataTuplesJson) {
        this.metaDataTuplesJson = metaDataTuplesJson;
    }
    public RmdSutDto getSut() {
        return sut;
    }
    public void setSut(RmdSutDto sut) {
        this.sut = sut;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
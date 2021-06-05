package nl.ou.se.rest.fuzzer.components.service.rmd.domain;

import java.time.LocalDateTime;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.DiscoveryModus;

public class RmdActionDependencyDto {

    // variable(s)
    private Long id;
    private RmdActionDto action;
    private RmdParameterDto parameter;
    private RmdActionDto actionDependsOn;
    private String parameterDependsOn;
    private DiscoveryModus discoveryModus;
    private LocalDateTime createdAt;

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RmdActionDto getAction() {
        return action;
    }

    public void setAction(RmdActionDto action) {
        this.action = action;
    }

    public RmdParameterDto getParameter() {
        return parameter;
    }

    public void setParameter(RmdParameterDto parameter) {
        this.parameter = parameter;
    }

    public RmdActionDto getActionDependsOn() {
        return actionDependsOn;
    }

    public void setActionDependsOn(RmdActionDto actionDependsOn) {
        this.actionDependsOn = actionDependsOn;
    }

    public String getParameterDependsOn() {
        return parameterDependsOn;
    }

    public void setParameterDependsOn(String parameterDependsOn) {
        this.parameterDependsOn = parameterDependsOn;
    }

    public DiscoveryModus getDiscoveryModus() {
        return discoveryModus;
    }

    public void setDiscoveryModus(DiscoveryModus discoveryModus) {
        this.discoveryModus = discoveryModus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
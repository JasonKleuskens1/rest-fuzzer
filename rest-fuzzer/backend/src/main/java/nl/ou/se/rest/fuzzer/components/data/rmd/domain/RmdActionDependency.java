package nl.ou.se.rest.fuzzer.components.data.rmd.domain;

import java.time.LocalDateTime;

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
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity(name = "rmd_actions_dependencies")
public class RmdActionDependency implements Comparable<RmdActionDependency> {

    // variable(s)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parameter_id")
    private RmdParameter parameter;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "action_id")
    private RmdAction action;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "action_depends_on_id")
    private RmdAction actionDependsOn;

    @NotNull
    private String parameterDependsOn;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DiscoveryModus discoveryModus;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    // constructor(s)
    public RmdActionDependency() {
    }

    public RmdActionDependency(DiscoveryModus discoveryModus, RmdAction action, RmdParameter parameter,
            RmdAction actionDependsOn, String parameterDependsOn) {
        this.discoveryModus = discoveryModus;
        this.action = action;
        this.parameter = parameter;
        this.actionDependsOn = actionDependsOn;
        this.parameterDependsOn = parameterDependsOn;
    }

    // method(s)
    public int compareTo(RmdActionDependency other) {
        if (this.getAction() != null && other.getAction() != null) {
            int compareAction = this.getAction().compareTo(other.getAction());
            if (compareAction != 0) {
                return compareAction;
            }
        }

        if (this.getParameter() != null && other.getParameter() != null) {
            int compareParameter = this.getParameter().compareTo(other.getParameter());
            if (compareParameter != 0) {
                return compareParameter;
            }
        }

        if (this.getActionDependsOn() != null && other.getActionDependsOn() != null) {
            return this.getActionDependsOn().compareTo(other.getActionDependsOn());
        }

        return this.getId().compareTo(this.getId());
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RmdParameter getParameter() {
        return parameter;
    }

    public void setParameter(RmdParameter parameter) {
        this.parameter = parameter;
    }

    public RmdAction getAction() {
        return action;
    }

    public void setAction(RmdAction action) {
        this.action = action;
    }

    public RmdAction getActionDependsOn() {
        return actionDependsOn;
    }

    public void setActionDependsOn(RmdAction actionDependsOn) {
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

    // toString
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
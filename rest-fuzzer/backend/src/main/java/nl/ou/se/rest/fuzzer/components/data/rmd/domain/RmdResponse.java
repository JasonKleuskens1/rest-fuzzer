package nl.ou.se.rest.fuzzer.components.data.rmd.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity(name = "rmd_responses")
public class RmdResponse implements Comparable<RmdResponse> {

    // variable(s)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer httpStatusCode;

    @NotNull
    @NotEmpty
    private String description;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "action_id")
    private RmdAction action;

    // constructor(s)
    public RmdResponse() {
    }

    public RmdResponse(Integer httpStatusCode, String description) {
        this.httpStatusCode = httpStatusCode;
        this.description = description;
    }

    // method(s)
    public int compareTo(RmdResponse other) {
        if (this.getAction() != null && other.getAction() != null) {
            int actionCompare = this.getAction().compareTo(other.getAction());
            if (actionCompare != 0) {
                return actionCompare;
            }
        }

        return this.getHttpStatusCode().compareTo(other.getHttpStatusCode());
    }

    // getter(s) and setter(s)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public RmdAction getAction() {
        return action;
    }

    public void setAction(RmdAction action) {
        this.action = action;
    }

    // toString
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
package nl.ou.se.rest.fuzzer.components.data.rmd.domain;

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
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import nl.ou.se.rest.fuzzer.components.shared.JsonUtil;

@Entity(name = "rmd_parameters")
public class RmdParameter implements Comparable<RmdParameter> {

    // variable(s)
    public static final String META_DATA_PATTERN = "PATTERN";
    public static final String META_DATA_FORMAT = "FORMAT";

    public static final String META_DATA_MIN_VALUE = "MIN_VALUE";
    public static final String META_DATA_MAX_VALUE = "MAX_VALUE";

    public static final String META_DATA_MIN_LENGTH = "MIN_LENGTH";
    public static final String META_DATA_MAX_LENGTH = "MAX_LENGTH";

    public static final String META_DATA_MIN_ITEMS = "MIN_ITEMS";
    public static final String META_DATA_MAX_ITEMS = "MAX_ITEMS";

    public static final String META_DATA_ENUM = "ENUM";
    public static final String META_DATA_DEFAULT = "DEFAULT";

    public static final String META_DATA_ARRAY_FORMAT = "ARRAY_FORMAT";
    public static final String META_DATA_ARRAY_TYPE = "ARRAY_TYPE";
    public static final String META_DATA_ARRAY_ENUM = "ARRAY_ENUM";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer position;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 64)
    private String name;

    @NotNull
    private Boolean required;
    @NotNull
    private Boolean requiredNonFuzzable;

    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ParameterType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ParameterContext context;

    private String metaDataTuplesJson;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "action_id")
    private RmdAction action;

    // constructor(s)
    public RmdParameter() {
    }

    public RmdParameter(int position, String name, Boolean required, String description, String type, String context) {
        this.position = position;
        this.name = name;
        this.required = required;
        this.required = false;
        
        this.description = description;
        this.type = ParameterType.valueOf(type);
        this.context = ParameterContext.valueOf(context);
    }

    // method(s)
    public int compareTo(RmdParameter other) {
        if (this.getAction() != null && other.getAction() != null) {
            int actionCompare = this.getAction().compareTo(other.getAction());
            if (actionCompare != 0) {
                return actionCompare;
            }
        }

        return this.getPosition().compareTo(other.getPosition());
    }

    public boolean equals(RmdParameter other) {
        return this.getId().equals(other.getId());
    }

    public Map<String, Object> getMetaDataTuples() {
        return JsonUtil.stringToMap(this.metaDataTuplesJson);
    }

    public void setMetaDataTuples(Map<String, Object> metaDataTuples) {
        this.metaDataTuplesJson = JsonUtil.mapToString(metaDataTuples);
    }

    public Object getMetaDataTupleValue(String key, Object defaultValue) {
        if (this.getMetaDataTuples().containsKey(key)) {
            return this.getMetaDataTuples().get(key);
        }
        return defaultValue;
    }

    // getter(s) and setter(s)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType type) {
        this.type = type;
    }

    public ParameterContext getContext() {
        return context;
    }

    public void setContext(ParameterContext context) {
        this.context = context;
    }

    public String getMetaDataTuplesJson() {
        return metaDataTuplesJson;
    }

    public void setMetaDataTuplesJson(String metaDataTuplesJson) {
        this.metaDataTuplesJson = metaDataTuplesJson;
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
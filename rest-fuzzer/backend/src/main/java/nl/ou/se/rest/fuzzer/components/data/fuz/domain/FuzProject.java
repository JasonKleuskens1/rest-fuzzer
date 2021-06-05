package nl.ou.se.rest.fuzzer.components.data.fuz.domain;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.SortNatural;

import nl.ou.se.rest.fuzzer.components.data.rmd.domain.RmdSut;
import nl.ou.se.rest.fuzzer.components.shared.JsonUtil;

@Entity(name = "fuz_projects")
public class FuzProject implements Comparable<FuzProject> {

    // variable(s)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private FuzProjectType type;

    @NotNull
    @NotEmpty
    private String metaDataTuplesJson;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sut_id")
    @NotNull
    private RmdSut sut;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_id")
    @SortNatural
    private SortedSet<FuzSequence> sequences = new TreeSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_id")
    @SortNatural
    private SortedSet<FuzRequest> requests = new TreeSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_id")
    @SortNatural
    private SortedSet<FuzResponse> responses = new TreeSet<>();

    // method(s)
    public int compareTo(FuzProject other) {
        return this.getId().compareTo(other.getId());
    }

    public Map<String, Object> getMetaDataTuples() {
        return JsonUtil.stringToMap(this.metaDataTuplesJson);
    }

    public void setMetaDataTuples(Map<String, Object> metaDataTuples) {
        this.metaDataTuplesJson = JsonUtil.mapToString(metaDataTuples);
    }

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

    public RmdSut getSut() {
        return sut;
    }

    public void setSut(RmdSut sut) {
        this.sut = sut;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public SortedSet<FuzSequence> getSequences() {
        return sequences;
    }

    public void setSequences(SortedSet<FuzSequence> sequences) {
        this.sequences = sequences;
    }

    public SortedSet<FuzRequest> getRequests() {
        return requests;
    }

    public void setRequests(SortedSet<FuzRequest> requests) {
        this.requests = requests;
    }

    public SortedSet<FuzResponse> getResponses() {
        return responses;
    }

    public void setResponses(SortedSet<FuzResponse> responses) {
        this.responses = responses;
    }

    // toString
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
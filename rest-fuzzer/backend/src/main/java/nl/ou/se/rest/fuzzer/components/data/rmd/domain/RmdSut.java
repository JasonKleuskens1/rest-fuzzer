package nl.ou.se.rest.fuzzer.components.data.rmd.domain;

import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.SortNatural;

import nl.ou.se.rest.fuzzer.components.shared.Constants;

@Entity(name = "rmd_suts")
@NamedEntityGraph(name = Constants.ENTITY_GRAPH_RMD_SUTS_ALL_RELATIONS, attributeNodes = @NamedAttributeNode(value = "actions", subgraph = "suts.subgraph.actions"), subgraphs = {
		@NamedSubgraph(name = "suts.subgraph.actions", attributeNodes = { @NamedAttributeNode(value = "parameters"),
                @NamedAttributeNode(value = "responses") }) })
public class RmdSut implements Comparable<RmdSut> {

    // variable(s)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String location;

    @Size(max = 64)
    private String title;

    @Size(max = 128)
    private String description;

    @Size(max = 255)
    private String host;

    @Size(max = 255)
    private String basePath;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sut_id")
    @SortNatural
    private SortedSet<RmdAction> actions = new TreeSet<>();

    // constructor(s)
    public RmdSut() {
    }

    // method(s)
    public int compareTo(RmdSut other) {
        return this.getId().compareTo(other.getId());
    }

    public void addAction(RmdAction action) {
        action.setSut(this);
        this.actions.add(action);
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public SortedSet<RmdAction> getActions() {
        return actions;
    }

    public void setActions(SortedSet<RmdAction> actions) {
        this.actions = actions;
    }

    // toString
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
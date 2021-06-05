package nl.ou.se.rest.fuzzer.components.service.rmd.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RmdSutDto {

    // variable(s)
    private Long id;
    private String location;
    private String title;
    private String description;
    private String host;
    private String basePath;
    private LocalDateTime createdAt;
    List<RmdActionDto> actions = new ArrayList<>();

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
    public List<RmdActionDto> getActions() {
        return actions;
    }
    public void setActions(List<RmdActionDto> actions) {
        this.actions = actions;
    }
}

package nl.ou.se.rest.fuzzer.components.data.fuz.domain;

import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import nl.ou.se.rest.fuzzer.components.shared.JsonUtil;

@Entity(name = "fuz_configurations")
public class FuzConfiguration {

    // variable(s)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String name;

    @NotNull
    @NotEmpty
    private String itemsJson;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    // constructor(s)
    public FuzConfiguration() {
    }

    public FuzConfiguration(String name, String itemsJson) {
        this.name = name;
        this.itemsJson = itemsJson;
    }
    
    // method(s)
    public Map<String, Object> getItemTuples() {
        return JsonUtil.stringToMap(this.itemsJson);
    }

    public void setItemTuples(Map<String, Object> itemTuples) {
        this.itemsJson = JsonUtil.mapToString(itemTuples);
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItemsJson() {
        return itemsJson;
    }

    public void setItemsJson(String itemsJson) {
        this.itemsJson = itemsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
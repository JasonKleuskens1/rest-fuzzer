package nl.ou.se.rest.fuzzer.components.service.fuz.domain;

import java.time.LocalDateTime;
import java.util.List;

public class FuzDictionaryDto {

    // variable(s)
    private Long id;
    private String name;
    private String itemsText;
    private List<String> items;
    private LocalDateTime createdAt;

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
    public String getItemsText() {
        return itemsText;
    }
    public void setItemsText(String itemsText) {
        this.itemsText = itemsText;
    }
    public List<String> getItems() {
        return items;
    }
    public void setItems(List<String> items) {
        this.items = items;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
package nl.ou.se.rest.fuzzer.components.data.fuz.domain;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity(name = "fuz_dictionaries")
public class FuzDictionary {

    private static final String ITEM_SEPERATOR = "\n";

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
    private String itemsText;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    // constructor(s)
    public FuzDictionary() {
    }

    public FuzDictionary(String name, String itemsText) {
        this.name = name;
        this.itemsText = itemsText;
    }

    // method(s)
    public List<String> getItems() {
        return Arrays.asList(this.itemsText.split(ITEM_SEPERATOR));
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

    public String getItemsText() {
        return itemsText;
    }

    public void setItemsText(String itemsText) {
        this.itemsText = itemsText;
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
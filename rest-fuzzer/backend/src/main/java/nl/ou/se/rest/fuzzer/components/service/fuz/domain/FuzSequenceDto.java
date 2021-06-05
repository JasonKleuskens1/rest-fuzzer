package nl.ou.se.rest.fuzzer.components.service.fuz.domain;

import java.time.LocalDateTime;
import java.util.List;

import nl.ou.se.rest.fuzzer.components.data.fuz.domain.FuzSequenceStatus;

public class FuzSequenceDto {

    // variable(s)
    private Long id;
    private int position;
    private int length;
    private FuzSequenceStatus status;
    private List<FuzRequestDto> requests;
    private LocalDateTime createdAt;

    // getter(s) and setter(s)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public FuzSequenceStatus getStatus() {
        return status;
    }

    public void setStatus(FuzSequenceStatus status) {
        this.status = status;
    }

    public List<FuzRequestDto> getRequests() {
        return requests;
    }

    public void setRequests(List<FuzRequestDto> requests) {
        this.requests = requests;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
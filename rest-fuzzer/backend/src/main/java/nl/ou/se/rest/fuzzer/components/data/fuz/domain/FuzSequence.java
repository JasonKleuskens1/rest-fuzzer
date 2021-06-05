package nl.ou.se.rest.fuzzer.components.data.fuz.domain;

import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.TreeSet;

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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.SortNatural;

@Entity(name = "fuz_sequences")
public class FuzSequence implements Comparable<FuzSequence> {

    // variable(s)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int position;

    @Column
    private int length;

    @NotNull
    @Enumerated(EnumType.STRING)
    private FuzSequenceStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private FuzProject project;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "sequence_id")
    @SortNatural
    private SortedSet<FuzRequest> requests = new TreeSet<>();

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    // constructor(s)
    public FuzSequence() {
    }

    public FuzSequence(int position, FuzProject project) {
        this.position = position;
        this.status = FuzSequenceStatus.CREATED;
        this.project = project;
        this.createdAt = LocalDateTime.now();
    }

    // method(s)
    public int compareTo(FuzSequence other) {
        int projectCompare = this.getProject().compareTo(other.getProject());
        if (projectCompare != 0) {
            return projectCompare;
        }

        return Integer.valueOf(this.getPosition()).compareTo(Integer.valueOf(other.getPosition()));
    }

    public void addRequest(FuzRequest request) {
        request.setSequence(this);
        this.requests.add(request);
        this.length = this.requests.size();
    }

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

    public FuzProject getProject() {
        return project;
    }

    public void setProject(FuzProject project) {
        this.project = project;
    }

    public SortedSet<FuzRequest> getRequests() {
        return requests;
    }

    public void setRequests(SortedSet<FuzRequest> requests) {
        this.requests = requests;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

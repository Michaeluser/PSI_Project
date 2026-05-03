package sk.stuba.fiit.bikeflow.dispatch.domain;

import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dispatch_request")
public class DispatchRequest {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String requestNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_facility_id")
    private Facility sourceFacility;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_facility_id")
    private Facility targetFacility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchStatus status;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(length = 1500)
    private String notes;

    @OneToMany(mappedBy = "dispatchRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DispatchRequestItem> items = new ArrayList<>();

    public DispatchRequest() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getRequestNumber() { return requestNumber; }
    public void setRequestNumber(String requestNumber) { this.requestNumber = requestNumber; }
    public Facility getSourceFacility() { return sourceFacility; }
    public void setSourceFacility(Facility sourceFacility) { this.sourceFacility = sourceFacility; }
    public Facility getTargetFacility() { return targetFacility; }
    public void setTargetFacility(Facility targetFacility) { this.targetFacility = targetFacility; }
    public DispatchPriority getPriority() { return priority; }
    public void setPriority(DispatchPriority priority) { this.priority = priority; }
    public DispatchStatus getStatus() { return status; }
    public void setStatus(DispatchStatus status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<DispatchRequestItem> getItems() { return items; }
}

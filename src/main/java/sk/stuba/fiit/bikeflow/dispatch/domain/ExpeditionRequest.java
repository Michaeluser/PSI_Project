package sk.stuba.fiit.bikeflow.dispatch.domain;

import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dispatch_request")
public class ExpeditionRequest {

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
    private RequestPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(length = 1500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id")
    private WarehouseStaff requestedBy;

    @OneToMany(mappedBy = "expeditionRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpeditionRequestItem> items = new ArrayList<>();

    public ExpeditionRequest() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getRequestNumber() { return requestNumber; }
    public void setRequestNumber(String requestNumber) { this.requestNumber = requestNumber; }
    public Facility getSourceFacility() { return sourceFacility; }
    public void setSourceFacility(Facility sourceFacility) { this.sourceFacility = sourceFacility; }
    public Facility getTargetFacility() { return targetFacility; }
    public void setTargetFacility(Facility targetFacility) { this.targetFacility = targetFacility; }
    public RequestPriority getPriority() { return priority; }
    public void setPriority(RequestPriority priority) { this.priority = priority; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public WarehouseStaff getRequestedBy() { return requestedBy; }
    public void setRequestedBy(WarehouseStaff requestedBy) { this.requestedBy = requestedBy; }
    public List<ExpeditionRequestItem> getItems() { return items; }
}

package sk.stuba.fiit.bikeflow.sparepart.domain;

import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBooking;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * External procurement order created when a SparePart is out of stock.
 * Corresponds to PartOrder in the F4 Service Class Diagram.
 * Its estimatedDelivery drives recomputation of the ServiceBooking completion date.
 */
@Entity
@Table(name = "part_order")
public class PartOrder {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spare_part_id")
    private SparePart sparePart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_booking_id")
    private ServiceBooking serviceBooking;

    @Column(nullable = false)
    private int orderedQuantity;

    private LocalDate estimatedDelivery;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartOrderStatus status;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    public PartOrder() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SparePart getSparePart() { return sparePart; }
    public void setSparePart(SparePart sparePart) { this.sparePart = sparePart; }
    public ServiceBooking getServiceBooking() { return serviceBooking; }
    public void setServiceBooking(ServiceBooking serviceBooking) { this.serviceBooking = serviceBooking; }
    public int getOrderedQuantity() { return orderedQuantity; }
    public void setOrderedQuantity(int orderedQuantity) { this.orderedQuantity = orderedQuantity; }
    public LocalDate getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(LocalDate estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
    public PartOrderStatus getStatus() { return status; }
    public void setStatus(PartOrderStatus status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

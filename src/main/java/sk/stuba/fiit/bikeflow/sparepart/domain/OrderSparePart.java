package sk.stuba.fiit.bikeflow.sparepart.domain;

import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBooking;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * Line item linking a ServiceBooking to a specific SparePart and quantity.
 * Corresponds to OrderSparePart in the F4 Service Class Diagram.
 */
@Entity
@Table(name = "order_spare_part")
public class OrderSparePart {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_booking_id")
    private ServiceBooking serviceBooking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spare_part_id")
    private SparePart sparePart;

    @Column(nullable = false)
    private int quantity;

    public OrderSparePart() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ServiceBooking getServiceBooking() { return serviceBooking; }
    public void setServiceBooking(ServiceBooking serviceBooking) { this.serviceBooking = serviceBooking; }
    public SparePart getSparePart() { return sparePart; }
    public void setSparePart(SparePart sparePart) { this.sparePart = sparePart; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

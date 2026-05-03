package sk.stuba.fiit.bikeflow.servicebooking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "service_work_item")
public class ServiceWorkItem {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_booking_id")
    private ServiceBooking serviceBooking;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal laborPrice;

    public ServiceWorkItem() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ServiceBooking getServiceBooking() {
        return serviceBooking;
    }

    public void setServiceBooking(ServiceBooking serviceBooking) {
        this.serviceBooking = serviceBooking;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getLaborPrice() {
        return laborPrice;
    }

    public void setLaborPrice(BigDecimal laborPrice) {
        this.laborPrice = laborPrice;
    }
}

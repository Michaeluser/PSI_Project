package sk.stuba.fiit.bikeflow.rental.domain;

import sk.stuba.fiit.bikeflow.bike.domain.Bike;
import sk.stuba.fiit.bikeflow.common.Cancellable;
import sk.stuba.fiit.bikeflow.common.Notifiable;
import sk.stuba.fiit.bikeflow.customer.domain.CustomerAccount;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "rental")
public class Rental implements Cancellable, Notifiable {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String rentalNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private CustomerAccount customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bike_id")
    private Bike bike;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;

    @Column(nullable = false)
    private Integer plannedMinutes;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    @Column(precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    private OffsetDateTime startedAt;
    private OffsetDateTime endedAt;

    public Rental() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getRentalNumber() { return rentalNumber; }
    public void setRentalNumber(String rentalNumber) { this.rentalNumber = rentalNumber; }
    public CustomerAccount getCustomer() { return customer; }
    public void setCustomer(CustomerAccount customer) { this.customer = customer; }
    public Bike getBike() { return bike; }
    public void setBike(Bike bike) { this.bike = bike; }
    public RentalStatus getStatus() { return status; }
    public void setStatus(RentalStatus status) { this.status = status; }
    public Integer getPlannedMinutes() { return plannedMinutes; }
    public void setPlannedMinutes(Integer plannedMinutes) { this.plannedMinutes = plannedMinutes; }
    public BigDecimal getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(BigDecimal estimatedPrice) { this.estimatedPrice = estimatedPrice; }
    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }
    public OffsetDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(OffsetDateTime endedAt) { this.endedAt = endedAt; }

    @Override
    public void cancel() { this.status = RentalStatus.CANCELLED; }

    @Override
    public boolean isCancelled() { return this.status == RentalStatus.CANCELLED; }

    @Override
    public String getEmail() { return customer != null ? customer.getEmail() : null; }

    @Override
    public String getPhone() { return rentalNumber; }
}

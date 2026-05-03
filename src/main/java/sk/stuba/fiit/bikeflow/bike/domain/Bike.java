package sk.stuba.fiit.bikeflow.bike.domain;

import sk.stuba.fiit.bikeflow.customer.domain.CustomerAccount;
import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bike")
public class Bike {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String modelName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BikeCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BikeStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerMinute;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserved_by_customer_id")
    private CustomerAccount reservedByCustomer;

    protected Bike() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public BikeCategory getCategory() {
        return category;
    }

    public void setCategory(BikeCategory category) {
        this.category = category;
    }

    public BikeStatus getStatus() {
        return status;
    }

    public void setStatus(BikeStatus status) {
        this.status = status;
    }

    public BigDecimal getPricePerMinute() {
        return pricePerMinute;
    }

    public void setPricePerMinute(BigDecimal pricePerMinute) {
        this.pricePerMinute = pricePerMinute;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public CustomerAccount getReservedByCustomer() {
        return reservedByCustomer;
    }

    public void setReservedByCustomer(CustomerAccount reservedByCustomer) {
        this.reservedByCustomer = reservedByCustomer;
    }
}

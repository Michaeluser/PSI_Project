package sk.stuba.fiit.bikeflow.dispatch.domain;

import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "warehouse_staff")
public class WarehouseStaff {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id")
    private Facility warehouse;

    public WarehouseStaff() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Facility getWarehouse() { return warehouse; }
    public void setWarehouse(Facility warehouse) { this.warehouse = warehouse; }
}

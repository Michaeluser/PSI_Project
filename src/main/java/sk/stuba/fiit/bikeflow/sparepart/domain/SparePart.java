package sk.stuba.fiit.bikeflow.sparepart.domain;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Physical spare part stored in a service workshop.
 * Corresponds to the SparePart class in the F4 Service Class Diagram.
 */
@Entity
@Table(name = "spare_part")
public class SparePart {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int stockQuantity;

    public SparePart() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
}

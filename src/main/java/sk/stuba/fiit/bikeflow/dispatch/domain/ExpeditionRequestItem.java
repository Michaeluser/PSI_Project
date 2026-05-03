package sk.stuba.fiit.bikeflow.dispatch.domain;

import sk.stuba.fiit.bikeflow.product.domain.Product;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "dispatch_request_item")
public class ExpeditionRequestItem {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dispatch_request_id")
    private ExpeditionRequest expeditionRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int requestedQuantity;

    public ExpeditionRequestItem() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ExpeditionRequest getExpeditionRequest() { return expeditionRequest; }
    public void setExpeditionRequest(ExpeditionRequest expeditionRequest) { this.expeditionRequest = expeditionRequest; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public int getRequestedQuantity() { return requestedQuantity; }
    public void setRequestedQuantity(int requestedQuantity) { this.requestedQuantity = requestedQuantity; }
}

package sk.stuba.fiit.bikeflow.product.api;

import java.util.UUID;

public record ProductResponse(UUID id, String sku, String name, String unit) {
}

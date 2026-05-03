package sk.stuba.fiit.bikeflow.inventory.api;

import java.util.UUID;

public record InventoryOverviewResponse(
        UUID productId,
        String productName,
        String sku,
        int quantity) {
}

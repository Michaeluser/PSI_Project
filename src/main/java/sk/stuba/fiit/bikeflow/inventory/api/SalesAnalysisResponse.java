package sk.stuba.fiit.bikeflow.inventory.api;

import java.util.UUID;

public record SalesAnalysisResponse(
        UUID productId,
        String productName,
        int totalQuantitySold) {
}

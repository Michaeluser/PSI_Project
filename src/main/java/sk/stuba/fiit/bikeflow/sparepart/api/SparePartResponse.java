package sk.stuba.fiit.bikeflow.sparepart.api;

import java.util.UUID;

public record SparePartResponse(
        UUID id,
        String sku,
        String name,
        int stockQuantity
) {}

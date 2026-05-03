package sk.stuba.fiit.bikeflow.servicebooking.api;

import sk.stuba.fiit.bikeflow.servicebooking.domain.ServicePartAvailabilityStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceRequiredPartResponse(
        UUID id,
        UUID productId,
        String productName,
        int requestedQuantity,
        int availableQuantity,
        BigDecimal unitPrice,
        ServicePartAvailabilityStatus availabilityStatus) {
}

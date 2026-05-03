package sk.stuba.fiit.bikeflow.servicebooking.api;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceWorkItemResponse(
        UUID id,
        String description,
        BigDecimal laborPrice) {
}

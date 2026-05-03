package sk.stuba.fiit.bikeflow.servicebooking.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceRequiredPartPayload(
        @NotNull UUID productId,
        @Min(1) int requestedQuantity,
        @NotNull @PositiveOrZero BigDecimal unitPrice) {
}

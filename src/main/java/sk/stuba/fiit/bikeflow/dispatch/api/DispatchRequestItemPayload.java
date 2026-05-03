package sk.stuba.fiit.bikeflow.dispatch.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DispatchRequestItemPayload(
        @NotNull UUID productId,
        @Min(1) int requestedQuantity) {
}

package sk.stuba.fiit.bikeflow.rental.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PreRegisterRentalRequest(
        @NotNull UUID customerId,
        @NotNull UUID bikeId,
        @Min(1) int plannedMinutes) {
}

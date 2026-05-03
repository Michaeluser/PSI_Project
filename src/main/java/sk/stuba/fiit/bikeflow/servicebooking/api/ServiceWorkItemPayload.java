package sk.stuba.fiit.bikeflow.servicebooking.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ServiceWorkItemPayload(
        @NotBlank String description,
        @NotNull @PositiveOrZero BigDecimal laborPrice) {
}

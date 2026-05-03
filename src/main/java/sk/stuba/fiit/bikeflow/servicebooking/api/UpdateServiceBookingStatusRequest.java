package sk.stuba.fiit.bikeflow.servicebooking.api;

import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBookingStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record UpdateServiceBookingStatusRequest(
        @NotNull ServiceBookingStatus status,
        BigDecimal preliminaryPrice,
        OffsetDateTime estimatedCompletionAt,
        String notes) {
}

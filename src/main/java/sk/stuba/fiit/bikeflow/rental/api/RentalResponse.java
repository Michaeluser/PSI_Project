package sk.stuba.fiit.bikeflow.rental.api;

import sk.stuba.fiit.bikeflow.rental.domain.RentalStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record RentalResponse(
        UUID id,
        String rentalNumber,
        UUID customerId,
        String customerName,
        UUID bikeId,
        String bikeCode,
        String bikeModelName,
        RentalStatus status,
        Integer plannedMinutes,
        BigDecimal estimatedPrice,
        BigDecimal finalPrice,
        OffsetDateTime createdAt,
        OffsetDateTime startedAt,
        OffsetDateTime endedAt) {
}

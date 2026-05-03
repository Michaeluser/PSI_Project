package sk.stuba.fiit.bikeflow.servicebooking.api;

import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBookingStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ServiceBookingResponse(
        UUID id,
        String bookingNumber,
        String customerName,
        String customerEmail,
        String bikeBrand,
        String bikeModel,
        String problemDescription,
        OffsetDateTime preferredFrom,
        OffsetDateTime preferredTo,
        OffsetDateTime scheduledAt,
        ServiceBookingStatus status,
        BigDecimal preliminaryPrice,
        OffsetDateTime estimatedCompletionAt,
        UUID servicePointId,
        String servicePointName,
        String notes) {
}

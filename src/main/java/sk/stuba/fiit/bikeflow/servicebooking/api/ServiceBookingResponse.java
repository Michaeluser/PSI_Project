package sk.stuba.fiit.bikeflow.servicebooking.api;

import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBookingStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
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
        OffsetDateTime receivedAt,
        String technicalState,
        String additionalFindings,
        OffsetDateTime clientApprovedAt,
        OffsetDateTime completedAt,
        OffsetDateTime clientNotifiedAt,
        int loyaltyDiscountPercent,
        BigDecimal loyaltyDiscountAmount,
        String partsOrderSummary,
        UUID servicePointId,
        String servicePointName,
        String notes,
        List<ServiceWorkItemResponse> workItems,
        List<ServiceRequiredPartResponse> requiredParts) {
}

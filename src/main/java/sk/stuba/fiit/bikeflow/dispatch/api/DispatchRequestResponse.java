package sk.stuba.fiit.bikeflow.dispatch.api;

import sk.stuba.fiit.bikeflow.dispatch.domain.DispatchPriority;
import sk.stuba.fiit.bikeflow.dispatch.domain.DispatchStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record DispatchRequestResponse(
        UUID id,
        String requestNumber,
        UUID sourceFacilityId,
        String sourceFacilityName,
        UUID targetFacilityId,
        String targetFacilityName,
        DispatchPriority priority,
        DispatchStatus status,
        OffsetDateTime createdAt,
        String notes,
        List<DispatchRequestResponseItem> items) {

    public record DispatchRequestResponseItem(UUID productId, String productName, int requestedQuantity) {
    }
}

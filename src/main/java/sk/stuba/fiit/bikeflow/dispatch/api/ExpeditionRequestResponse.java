package sk.stuba.fiit.bikeflow.dispatch.api;

import sk.stuba.fiit.bikeflow.dispatch.domain.RequestPriority;
import sk.stuba.fiit.bikeflow.dispatch.domain.RequestStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ExpeditionRequestResponse(
        UUID id,
        String requestNumber,
        UUID sourceFacilityId,
        String sourceFacilityName,
        UUID targetFacilityId,
        String targetFacilityName,
        RequestPriority priority,
        RequestStatus status,
        OffsetDateTime createdAt,
        String notes,
        UUID requestedById,
        List<ExpeditionRequestResponseItem> items) {

    public record ExpeditionRequestResponseItem(UUID productId, String productName, int requestedQuantity) {
    }
}

package sk.stuba.fiit.bikeflow.dispatch.api;

import sk.stuba.fiit.bikeflow.dispatch.domain.RequestPriority;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateExpeditionRequest(
        @NotNull UUID sourceFacilityId,
        @NotNull UUID targetFacilityId,
        @NotNull RequestPriority priority,
        UUID requestedById,
        String notes,
        @Valid @NotEmpty List<ExpeditionRequestItemPayload> items) {
}

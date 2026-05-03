package sk.stuba.fiit.bikeflow.dispatch.api;

import sk.stuba.fiit.bikeflow.dispatch.domain.DispatchPriority;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateDispatchRequest(
        @NotNull UUID sourceFacilityId,
        @NotNull UUID targetFacilityId,
        @NotNull DispatchPriority priority,
        String notes,
        @Valid @NotEmpty List<DispatchRequestItemPayload> items) {
}

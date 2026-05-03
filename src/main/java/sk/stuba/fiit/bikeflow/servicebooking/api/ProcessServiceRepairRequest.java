package sk.stuba.fiit.bikeflow.servicebooking.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProcessServiceRepairRequest(
        @NotBlank String technicalState,
        String additionalFindings,
        @NotEmpty List<@Valid ServiceWorkItemPayload> workItems,
        @NotNull List<@Valid ServiceRequiredPartPayload> requiredParts,
        @NotNull Boolean clientApproved) {
}

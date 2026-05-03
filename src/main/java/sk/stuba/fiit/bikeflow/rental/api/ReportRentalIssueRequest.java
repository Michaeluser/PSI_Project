package sk.stuba.fiit.bikeflow.rental.api;

import sk.stuba.fiit.bikeflow.rental.domain.RentalIssueType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportRentalIssueRequest(
        @NotNull RentalIssueType issueType,
        @NotBlank String description) {
}

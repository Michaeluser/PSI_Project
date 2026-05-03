package sk.stuba.fiit.bikeflow.rental.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SubmitFeedbackRequest(
        @NotNull @Min(1) @Max(5) Integer rating,
        String comment
) {}

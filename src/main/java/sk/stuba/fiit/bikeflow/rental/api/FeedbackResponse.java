package sk.stuba.fiit.bikeflow.rental.api;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FeedbackResponse(
        UUID id,
        UUID rentalId,
        Integer rating,
        String comment,
        OffsetDateTime submittedAt
) {}

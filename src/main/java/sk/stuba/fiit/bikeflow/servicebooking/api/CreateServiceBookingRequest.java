package sk.stuba.fiit.bikeflow.servicebooking.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateServiceBookingRequest(
        @NotBlank String customerName,
        @NotBlank @Email String customerEmail,
        @NotBlank String bikeBrand,
        @NotBlank String bikeModel,
        @NotBlank String problemDescription,
        @NotNull @FutureOrPresent OffsetDateTime preferredFrom,
        @NotNull @Future OffsetDateTime preferredTo,
        @NotNull UUID servicePointId) {
}

package sk.stuba.fiit.bikeflow.sparepart.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record AddSparePartRequest(
        @NotNull UUID sparePartId,
        @NotNull @Min(1) Integer quantity,
        LocalDate estimatedDelivery
) {}

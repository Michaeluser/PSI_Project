package sk.stuba.fiit.bikeflow.bike.api;

import sk.stuba.fiit.bikeflow.bike.domain.BikeCategory;
import sk.stuba.fiit.bikeflow.bike.domain.BikeStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record BikeResponse(
        UUID id,
        String code,
        String modelName,
        BikeCategory category,
        BikeStatus status,
        BigDecimal pricePerMinute,
        UUID facilityId,
        String facilityName,
        String city) {
}

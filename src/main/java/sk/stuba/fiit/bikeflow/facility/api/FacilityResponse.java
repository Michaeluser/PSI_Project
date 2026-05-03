package sk.stuba.fiit.bikeflow.facility.api;

import sk.stuba.fiit.bikeflow.facility.domain.FacilityType;

import java.util.UUID;

public record FacilityResponse(
        UUID id,
        String code,
        String name,
        FacilityType type,
        String city,
        String addressLine) {
}

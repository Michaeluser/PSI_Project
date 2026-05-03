package sk.stuba.fiit.bikeflow.facility.api;

import sk.stuba.fiit.bikeflow.facility.domain.FacilityStatus;

import java.util.UUID;

public record FacilityResponse(
        UUID id,
        String code,
        String name,
        FacilityStatus type,
        String city,
        String addressLine) {
}

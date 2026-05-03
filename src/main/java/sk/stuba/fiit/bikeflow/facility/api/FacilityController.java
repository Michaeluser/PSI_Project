package sk.stuba.fiit.bikeflow.facility.api;

import sk.stuba.fiit.bikeflow.facility.repository.FacilityRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
public class FacilityController {

    private final FacilityRepository facilityRepository;

    public FacilityController(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @GetMapping
    public List<FacilityResponse> getFacilities() {
        return facilityRepository.findAll()
                .stream()
                .map(facility -> new FacilityResponse(
                        facility.getId(),
                        facility.getCode(),
                        facility.getName(),
                        facility.getType(),
                        facility.getCity(),
                        facility.getAddressLine()))
                .toList();
    }
}

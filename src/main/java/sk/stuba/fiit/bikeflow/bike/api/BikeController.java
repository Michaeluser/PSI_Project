package sk.stuba.fiit.bikeflow.bike.api;

import sk.stuba.fiit.bikeflow.bike.domain.BikeStatus;
import sk.stuba.fiit.bikeflow.bike.repository.BikeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bikes")
public class BikeController {

    private final BikeRepository bikeRepository;

    public BikeController(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    @GetMapping
    public List<BikeResponse> getBikes(@RequestParam String city) {
        return bikeRepository.findAvailableInCity(city, BikeStatus.AVAILABLE)
                .stream()
                .map(bike -> new BikeResponse(
                        bike.getId(),
                        bike.getCode(),
                        bike.getModelName(),
                        bike.getCategory(),
                        bike.getStatus(),
                        bike.getPricePerMinute(),
                        bike.getFacility().getId(),
                        bike.getFacility().getName(),
                        bike.getFacility().getCity()))
                .toList();
    }
}

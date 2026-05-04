package sk.stuba.fiit.bikeflow.sparepart.api;

import sk.stuba.fiit.bikeflow.sparepart.application.SparePartService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class SparePartController {

    private final SparePartService sparePartService;

    public SparePartController(SparePartService sparePartService) {
        this.sparePartService = sparePartService;
    }

    @GetMapping("/api/spare-parts")
    public List<SparePartResponse> getAll() {
        return sparePartService.getAll();
    }

    @PostMapping("/api/service-bookings/{bookingId}/parts")
    @ResponseStatus(HttpStatus.CREATED)
    public AddSparePartResponse addToBooking(
            @PathVariable UUID bookingId,
            @Valid @RequestBody AddSparePartRequest request) {
        return sparePartService.addToBooking(bookingId, request);
    }
}

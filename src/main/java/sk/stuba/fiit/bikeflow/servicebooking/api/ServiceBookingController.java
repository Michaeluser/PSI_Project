package sk.stuba.fiit.bikeflow.servicebooking.api;

import sk.stuba.fiit.bikeflow.servicebooking.application.ServiceBookingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/service-bookings")
public class ServiceBookingController {

    private final ServiceBookingService serviceBookingService;

    public ServiceBookingController(ServiceBookingService serviceBookingService) {
        this.serviceBookingService = serviceBookingService;
    }

    @GetMapping
    public List<ServiceBookingResponse> getAll() {
        return serviceBookingService.getAll();
    }

    @PostMapping
    public ServiceBookingResponse create(@Valid @RequestBody CreateServiceBookingRequest request) {
        return serviceBookingService.create(request);
    }

    @PatchMapping("/{bookingId}/status")
    public ServiceBookingResponse updateStatus(
            @PathVariable UUID bookingId,
            @Valid @RequestBody UpdateServiceBookingStatusRequest request) {
        return serviceBookingService.updateStatus(bookingId, request);
    }
}

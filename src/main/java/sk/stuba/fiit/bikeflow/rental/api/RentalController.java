package sk.stuba.fiit.bikeflow.rental.api;

import sk.stuba.fiit.bikeflow.rental.application.RentalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public List<RentalResponse> getAll() {
        return rentalService.getAll();
    }

    @PostMapping("/pre-register")
    public RentalResponse preRegister(@Valid @RequestBody PreRegisterRentalRequest request) {
        return rentalService.preRegister(request);
    }

    @PostMapping("/{rentalId}/start")
    public RentalResponse start(@PathVariable UUID rentalId) {
        return rentalService.startRental(rentalId);
    }

    @PostMapping("/{rentalId}/finish")
    public RentalResponse finish(@PathVariable UUID rentalId) {
        return rentalService.finishRental(rentalId);
    }

    @PostMapping("/{rentalId}/issue")
    public RentalResponse reportIssue(
            @PathVariable UUID rentalId,
            @Valid @RequestBody ReportRentalIssueRequest request) {
        return rentalService.reportIssue(rentalId, request);
    }
}

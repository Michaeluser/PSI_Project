package sk.stuba.fiit.bikeflow.dispatch.api;

import sk.stuba.fiit.bikeflow.dispatch.application.ExpeditionRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expedition-requests")
public class ExpeditionRequestController {

    private final ExpeditionRequestService expeditionRequestService;

    public ExpeditionRequestController(ExpeditionRequestService expeditionRequestService) {
        this.expeditionRequestService = expeditionRequestService;
    }

    @GetMapping
    public List<ExpeditionRequestResponse> getAll() {
        return expeditionRequestService.getAll();
    }

    @PostMapping
    public ExpeditionRequestResponse create(@Valid @RequestBody CreateExpeditionRequest request) {
        return expeditionRequestService.create(request);
    }
}

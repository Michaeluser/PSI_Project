package sk.stuba.fiit.bikeflow.dispatch.api;

import sk.stuba.fiit.bikeflow.dispatch.application.DispatchRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispatch-requests")
public class DispatchRequestController {

    private final DispatchRequestService dispatchRequestService;

    public DispatchRequestController(DispatchRequestService dispatchRequestService) {
        this.dispatchRequestService = dispatchRequestService;
    }

    @GetMapping
    public List<DispatchRequestResponse> getAll() {
        return dispatchRequestService.getAll();
    }

    @PostMapping
    public DispatchRequestResponse create(@Valid @RequestBody CreateDispatchRequest request) {
        return dispatchRequestService.create(request);
    }
}

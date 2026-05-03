package sk.stuba.fiit.bikeflow.customer.api;

import sk.stuba.fiit.bikeflow.customer.repository.CustomerAccountRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerAccountRepository customerAccountRepository;

    public CustomerController(CustomerAccountRepository customerAccountRepository) {
        this.customerAccountRepository = customerAccountRepository;
    }

    @GetMapping
    public List<CustomerSummaryResponse> getCustomers() {
        return customerAccountRepository.findAll()
                .stream()
                .map(customer -> new CustomerSummaryResponse(
                        customer.getId(),
                        customer.getFullName(),
                        customer.getEmail(),
                        customer.getCreditBalance(),
                        customer.isVerifiedPaymentCard()))
                .toList();
    }
}

package sk.stuba.fiit.bikeflow.rental.application;

import sk.stuba.fiit.bikeflow.bike.domain.Bike;
import sk.stuba.fiit.bikeflow.bike.domain.BikeStatus;
import sk.stuba.fiit.bikeflow.bike.repository.BikeRepository;
import sk.stuba.fiit.bikeflow.customer.domain.CustomerAccount;
import sk.stuba.fiit.bikeflow.customer.repository.CustomerAccountRepository;
import sk.stuba.fiit.bikeflow.rental.api.PreRegisterRentalRequest;
import sk.stuba.fiit.bikeflow.rental.domain.Rental;
import sk.stuba.fiit.bikeflow.rental.domain.RentalStatus;
import sk.stuba.fiit.bikeflow.rental.repository.FeedbackRepository;
import sk.stuba.fiit.bikeflow.rental.repository.RentalIssueReportRepository;
import sk.stuba.fiit.bikeflow.rental.repository.RentalRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RentalServiceTest {

    @Test
    void shouldPreRegisterRentalWhenCustomerHasEnoughCredits() {
        RentalRepository rentalRepository = mock(RentalRepository.class);
        RentalIssueReportRepository issueRepository = mock(RentalIssueReportRepository.class);
        CustomerAccountRepository customerRepository = mock(CustomerAccountRepository.class);
        BikeRepository bikeRepository = mock(BikeRepository.class);
        FeedbackRepository feedbackRepository = mock(FeedbackRepository.class);

        CustomerAccount customer = new CustomerAccount();
        customer.setId(UUID.randomUUID());
        customer.setFullName("Andrej");
        customer.setCreditBalance(new BigDecimal("100.00"));
        customer.setVerifiedPaymentCard(true);
        customer.setActive(true);
        customer.setCreatedAt(OffsetDateTime.now());

        Bike bike = new Bike();
        bike.setId(UUID.randomUUID());
        bike.setCode("BK-TEST");
        bike.setModelName("Test Bike");
        bike.setPricePerMinute(new BigDecimal("0.50"));
        bike.setStatus(BikeStatus.AVAILABLE);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(bikeRepository.findById(bike.getId())).thenReturn(Optional.of(bike));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalService service = new RentalService(rentalRepository, issueRepository, customerRepository, bikeRepository, feedbackRepository);

        var response = service.preRegister(new PreRegisterRentalRequest(customer.getId(), bike.getId(), 20));

        assertEquals(RentalStatus.PRE_REGISTERED, response.status());
        assertEquals(new BigDecimal("10.00"), response.estimatedPrice());
        assertEquals(BikeStatus.PRE_RESERVED, bike.getStatus());
    }
}

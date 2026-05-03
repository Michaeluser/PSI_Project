package sk.stuba.fiit.bikeflow.rental.application;

import sk.stuba.fiit.bikeflow.bike.domain.Bike;
import sk.stuba.fiit.bikeflow.bike.domain.BikeStatus;
import sk.stuba.fiit.bikeflow.bike.repository.BikeRepository;
import sk.stuba.fiit.bikeflow.customer.domain.CustomerAccount;
import sk.stuba.fiit.bikeflow.customer.repository.CustomerAccountRepository;
import sk.stuba.fiit.bikeflow.rental.api.PreRegisterRentalRequest;
import sk.stuba.fiit.bikeflow.rental.domain.Rental;
import sk.stuba.fiit.bikeflow.rental.domain.RentalStatus;
import sk.stuba.fiit.bikeflow.rental.repository.RentalIssueReportRepository;
import sk.stuba.fiit.bikeflow.rental.repository.RentalRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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

        UUID customerId = UUID.randomUUID();
        CustomerAccount customer = mock(CustomerAccount.class);
        when(customer.getId()).thenReturn(customerId);
        when(customer.getFullName()).thenReturn("Andrej");
        when(customer.getCreditBalance()).thenReturn(new BigDecimal("100.00"));
        when(customer.isVerifiedPaymentCard()).thenReturn(true);
        when(customer.isActive()).thenReturn(true);

        UUID bikeId = UUID.randomUUID();
        AtomicReference<BikeStatus> bikeStatus = new AtomicReference<>(BikeStatus.AVAILABLE);
        Bike bike = mock(Bike.class);
        when(bike.getId()).thenReturn(bikeId);
        when(bike.getCode()).thenReturn("BK-TEST");
        when(bike.getModelName()).thenReturn("Test Bike");
        when(bike.getPricePerMinute()).thenReturn(new BigDecimal("0.50"));
        when(bike.getStatus()).thenAnswer(invocation -> bikeStatus.get());
        doAnswer(invocation -> {
            bikeStatus.set(invocation.getArgument(0));
            return null;
        }).when(bike).setStatus(any(BikeStatus.class));

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(bikeRepository.findById(bike.getId())).thenReturn(Optional.of(bike));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RentalService service = new RentalService(rentalRepository, issueRepository, customerRepository, bikeRepository);

        var response = service.preRegister(new PreRegisterRentalRequest(customer.getId(), bike.getId(), 20));

        assertEquals(RentalStatus.PRE_REGISTERED, response.status());
        assertEquals(new BigDecimal("10.00"), response.estimatedPrice());
        assertEquals(BikeStatus.PRE_RESERVED, bike.getStatus());
    }
}

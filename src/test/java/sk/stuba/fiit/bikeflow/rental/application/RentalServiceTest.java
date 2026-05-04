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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RentalServiceTest {

    private RentalService buildService(
            RentalRepository rentalRepository,
            RentalIssueReportRepository issueRepository,
            CustomerAccountRepository customerRepository,
            BikeRepository bikeRepository,
            FeedbackRepository feedbackRepository) {
        return new RentalService(rentalRepository, issueRepository, customerRepository,
                bikeRepository, feedbackRepository);
    }

    @Test
    void shouldPreRegisterRentalWhenCustomerHasEnoughCredits() {
        RentalRepository rentalRepository = mock(RentalRepository.class);
        RentalIssueReportRepository issueRepository = mock(RentalIssueReportRepository.class);
        CustomerAccountRepository customerRepository = mock(CustomerAccountRepository.class);
        BikeRepository bikeRepository = mock(BikeRepository.class);
        FeedbackRepository feedbackRepository = mock(FeedbackRepository.class);

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
        when(bike.getStatus()).thenAnswer(inv -> bikeStatus.get());
        doAnswer(inv -> { bikeStatus.set(inv.getArgument(0)); return null; })
                .when(bike).setStatus(any(BikeStatus.class));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(bikeRepository.findById(bikeId)).thenReturn(Optional.of(bike));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        RentalService service = buildService(rentalRepository, issueRepository,
                customerRepository, bikeRepository, feedbackRepository);

        var response = service.preRegister(new PreRegisterRentalRequest(customerId, bikeId, 20));

        assertEquals(RentalStatus.PRELIMINARY, response.status());
        assertEquals(new BigDecimal("10.00"), response.estimatedPrice());
        assertEquals(BikeStatus.PRE_RESERVED, bike.getStatus());
    }

    @Test
    void shouldCancelPreliminaryRentalAndReleaseBike() {
        RentalRepository rentalRepository = mock(RentalRepository.class);
        RentalIssueReportRepository issueRepository = mock(RentalIssueReportRepository.class);
        CustomerAccountRepository customerRepository = mock(CustomerAccountRepository.class);
        BikeRepository bikeRepository = mock(BikeRepository.class);
        FeedbackRepository feedbackRepository = mock(FeedbackRepository.class);

        UUID rentalId = UUID.randomUUID();
        CustomerAccount customer = mock(CustomerAccount.class);
        when(customer.getId()).thenReturn(UUID.randomUUID());
        when(customer.getFullName()).thenReturn("Andrej");

        AtomicReference<BikeStatus> bikeStatus = new AtomicReference<>(BikeStatus.PRE_RESERVED);
        Bike bike = mock(Bike.class);
        when(bike.getId()).thenReturn(UUID.randomUUID());
        when(bike.getCode()).thenReturn("BK-TEST");
        when(bike.getModelName()).thenReturn("Test Bike");
        when(bike.getStatus()).thenAnswer(inv -> bikeStatus.get());
        doAnswer(inv -> { bikeStatus.set(inv.getArgument(0)); return null; })
                .when(bike).setStatus(any(BikeStatus.class));

        Rental rental = new Rental();
        rental.setId(rentalId);
        rental.setRentalNumber("RT-123456");
        rental.setCustomer(customer);
        rental.setBike(bike);
        rental.setStatus(RentalStatus.PRELIMINARY);
        rental.setPlannedMinutes(20);
        rental.setEstimatedPrice(new BigDecimal("10.00"));

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(inv -> inv.getArgument(0));

        RentalService service = buildService(rentalRepository, issueRepository,
                customerRepository, bikeRepository, feedbackRepository);

        var response = service.cancelRental(rentalId);

        assertEquals(RentalStatus.CANCELLED, response.status());
        assertEquals(BikeStatus.AVAILABLE, bike.getStatus());
        // credit is NOT deducted at pre-register, so nothing to refund at cancel
        verify(customer, never()).setCreditBalance(any());
    }

    @Test
    void shouldThrowExceptionWhenCancellingNonPreliminaryRental() {
        RentalRepository rentalRepository = mock(RentalRepository.class);
        RentalIssueReportRepository issueRepository = mock(RentalIssueReportRepository.class);
        CustomerAccountRepository customerRepository = mock(CustomerAccountRepository.class);
        BikeRepository bikeRepository = mock(BikeRepository.class);
        FeedbackRepository feedbackRepository = mock(FeedbackRepository.class);

        UUID rentalId = UUID.randomUUID();
        Rental rental = new Rental();
        rental.setId(rentalId);
        rental.setStatus(RentalStatus.ACTIVE);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

        RentalService service = buildService(rentalRepository, issueRepository,
                customerRepository, bikeRepository, feedbackRepository);

        assertThrows(Exception.class, () -> service.cancelRental(rentalId));
    }
}

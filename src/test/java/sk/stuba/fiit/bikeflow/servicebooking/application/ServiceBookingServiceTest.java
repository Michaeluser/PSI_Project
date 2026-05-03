package sk.stuba.fiit.bikeflow.servicebooking.application;

import sk.stuba.fiit.bikeflow.common.NotificationService;
import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import sk.stuba.fiit.bikeflow.facility.repository.FacilityRepository;
import sk.stuba.fiit.bikeflow.servicebooking.api.CreateServiceBookingRequest;
import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBookingStatus;
import sk.stuba.fiit.bikeflow.servicebooking.repository.ServiceBookingRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServiceBookingServiceTest {

    @Test
    void shouldCreateBookingInFirstAvailableSlot() {
        ServiceBookingRepository bookingRepository = mock(ServiceBookingRepository.class);
        FacilityRepository facilityRepository = mock(FacilityRepository.class);
        NotificationService notificationService = mock(NotificationService.class);

        Facility facility = new Facility();
        facility.setId(UUID.randomUUID());
        facility.setName("Service");

        when(facilityRepository.findById(facility.getId())).thenReturn(Optional.of(facility));
        when(bookingRepository.countActiveBySlot(any(), any())).thenReturn(0L);
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceBookingService service = new ServiceBookingService(bookingRepository, facilityRepository, notificationService);

        OffsetDateTime from = OffsetDateTime.now().plusDays(1).withMinute(15);
        OffsetDateTime to = from.plusHours(2);

        var response = service.create(new CreateServiceBookingRequest(
                "Andrej",
                "andrej@example.com",
                "Trek",
                "Domane",
                "Brake issue",
                from,
                to,
                facility.getId()
        ));

        assertEquals(ServiceBookingStatus.SCHEDULED, response.status());
        assertEquals(from.withMinute(0).withSecond(0).withNano(0), response.scheduledAt());
    }
}

package sk.stuba.fiit.bikeflow.servicebooking.application;

import sk.stuba.fiit.bikeflow.common.NotificationService;
import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import sk.stuba.fiit.bikeflow.facility.repository.FacilityRepository;
import sk.stuba.fiit.bikeflow.dispatch.domain.DispatchRequest;
import sk.stuba.fiit.bikeflow.dispatch.repository.DispatchRequestRepository;
import sk.stuba.fiit.bikeflow.inventory.domain.InventoryStock;
import sk.stuba.fiit.bikeflow.inventory.repository.InventoryStockRepository;
import sk.stuba.fiit.bikeflow.product.domain.Product;
import sk.stuba.fiit.bikeflow.product.repository.ProductRepository;
import sk.stuba.fiit.bikeflow.servicebooking.api.CreateServiceBookingRequest;
import sk.stuba.fiit.bikeflow.servicebooking.api.ProcessServiceRepairRequest;
import sk.stuba.fiit.bikeflow.servicebooking.api.ServiceRequiredPartPayload;
import sk.stuba.fiit.bikeflow.servicebooking.api.ServiceWorkItemPayload;
import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBooking;
import sk.stuba.fiit.bikeflow.servicebooking.domain.ServicePartAvailabilityStatus;
import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBookingStatus;
import sk.stuba.fiit.bikeflow.servicebooking.repository.ServiceBookingRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServiceBookingServiceTest {

    @Test
    void shouldCreateBookingInFirstAvailableSlot() {
        ServiceBookingRepository bookingRepository = mock(ServiceBookingRepository.class);
        FacilityRepository facilityRepository = mock(FacilityRepository.class);
        NotificationService notificationService = mock(NotificationService.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        InventoryStockRepository inventoryStockRepository = mock(InventoryStockRepository.class);
        DispatchRequestRepository dispatchRequestRepository = mock(DispatchRequestRepository.class);

        Facility facility = mock(Facility.class);
        when(facility.getId()).thenReturn(UUID.randomUUID());
        when(facility.getName()).thenReturn("Service");

        when(facilityRepository.findById(facility.getId())).thenReturn(Optional.of(facility));
        when(bookingRepository.countActiveBySlot(any(), any())).thenReturn(0L);
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceBookingService service = new ServiceBookingService(bookingRepository, facilityRepository, notificationService);
        ServiceBookingService service = new ServiceBookingService(
                bookingRepository,
                facilityRepository,
                productRepository,
                inventoryStockRepository,
                dispatchRequestRepository);

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

    @Test
    void shouldProcessRepairCreatePartDispatchAndApplyLoyaltyDiscount() {
        ServiceBookingRepository bookingRepository = mock(ServiceBookingRepository.class);
        FacilityRepository facilityRepository = mock(FacilityRepository.class);
        ProductRepository productRepository = mock(ProductRepository.class);

        UUID bookingId = UUID.randomUUID();
        UUID servicePointId = UUID.randomUUID();
        UUID sourceFacilityId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Facility servicePoint = mock(Facility.class);
        when(servicePoint.getId()).thenReturn(servicePointId);
        when(servicePoint.getName()).thenReturn("BikeFlow Service");

        Facility sourceFacility = mock(Facility.class);
        when(sourceFacility.getId()).thenReturn(sourceFacilityId);
        when(sourceFacility.getName()).thenReturn("Warehouse");

        Product product = mock(Product.class);
        when(product.getId()).thenReturn(productId);
        when(product.getName()).thenReturn("Disc brake set");

        when(sourceStock.getFacility()).thenReturn(sourceFacility);

        ServiceBooking booking = new ServiceBooking();
        booking.setId(bookingId);
        booking.setBookingNumber("SB-1");
        booking.setCustomerName("Andrej");
        booking.setCustomerEmail("andrej@example.com");
        booking.setBikeBrand("Trek");
        booking.setBikeModel("Domane");
        booking.setProblemDescription("Brake issue");
        booking.setPreferredFrom(OffsetDateTime.now().plusDays(1));
        booking.setPreferredTo(OffsetDateTime.now().plusDays(1).plusHours(2));
        booking.setScheduledAt(OffsetDateTime.now().plusDays(1));
        booking.setCreatedAt(OffsetDateTime.now());
        booking.setStatus(ServiceBookingStatus.SCHEDULED);
        booking.setServicePoint(servicePoint);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.countByCustomerEmailIgnoreCaseAndStatus("andrej@example.com", ServiceBookingStatus.DONE))
                .thenReturn(2L);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(inventoryStockRepository.findByFacilityIdAndProductId(servicePointId, productId)).thenReturn(Optional.empty());
        when(inventoryStockRepository.findAvailableSources(productId, servicePointId, 2)).thenReturn(List.of(sourceStock));
        when(dispatchRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceBookingService service = new ServiceBookingService(
                bookingRepository,
                facilityRepository,
                productRepository,
                inventoryStockRepository,
                dispatchRequestRepository);

        var response = service.processRepair(bookingId, new ProcessServiceRepairRequest(
                "Brake pads worn.",
                "Rear rotor is bent.",
                List.of(new ServiceWorkItemPayload("Brake service", new BigDecimal("100.00"))),
                List.of(new ServiceRequiredPartPayload(productId, 2, new BigDecimal("20.00"))),
                true
        ));

        assertEquals(ServiceBookingStatus.WAITING_FOR_PARTS, response.status());
        assertEquals(0, response.preliminaryPrice().compareTo(new BigDecimal("130.00")));
        assertEquals(10, response.loyaltyDiscountPercent());
        assertEquals(ServicePartAvailabilityStatus.ORDER_REQUESTED, response.requiredParts().get(0).availabilityStatus());
        assertTrue(response.partsOrderSummary().contains("DR-"));
        verify(dispatchRequestRepository).save(any(DispatchRequest.class));
    }
}
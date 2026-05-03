package sk.stuba.fiit.bikeflow.servicebooking.application;

import sk.stuba.fiit.bikeflow.common.DateRange;
import sk.stuba.fiit.bikeflow.common.NotificationService;
import sk.stuba.fiit.bikeflow.common.exception.BusinessRuleException;
import sk.stuba.fiit.bikeflow.common.exception.NotFoundException;
import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import sk.stuba.fiit.bikeflow.facility.repository.FacilityRepository;
import sk.stuba.fiit.bikeflow.servicebooking.api.CreateServiceBookingRequest;
import sk.stuba.fiit.bikeflow.servicebooking.api.ServiceBookingResponse;
import sk.stuba.fiit.bikeflow.servicebooking.api.UpdateServiceBookingStatusRequest;
import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBooking;
import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBookingStatus;
import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceCapacity;
import sk.stuba.fiit.bikeflow.servicebooking.domain.TimeSlot;
import sk.stuba.fiit.bikeflow.servicebooking.repository.ServiceBookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ServiceBookingService {

    private static final ServiceCapacity CAPACITY = new ServiceCapacity(2);
    private static final List<ServiceBookingStatus> ACTIVE_STATUSES = List.of(
            ServiceBookingStatus.SCHEDULED,
            ServiceBookingStatus.RECEIVED,
            ServiceBookingStatus.WAITING_FOR_PARTS,
            ServiceBookingStatus.IN_REPAIR
    );

    private final ServiceBookingRepository serviceBookingRepository;
    private final FacilityRepository facilityRepository;
    private final NotificationService notificationService;

    public ServiceBookingService(
            ServiceBookingRepository serviceBookingRepository,
            FacilityRepository facilityRepository,
            NotificationService notificationService) {
        this.serviceBookingRepository = serviceBookingRepository;
        this.facilityRepository = facilityRepository;
        this.notificationService = notificationService;
    }

    public List<ServiceBookingResponse> getAll() {
        return serviceBookingRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ServiceBookingResponse create(CreateServiceBookingRequest request) {
        DateRange preferredWindow = new DateRange(request.preferredFrom(), request.preferredTo());

        Facility servicePoint = facilityRepository.findById(request.servicePointId())
                .orElseThrow(() -> new NotFoundException("Service point was not found."));

        TimeSlot selectedSlot = findFirstAvailableSlot(preferredWindow);

        ServiceBooking booking = new ServiceBooking();
        booking.setId(UUID.randomUUID());
        booking.setBookingNumber("SB-" + System.currentTimeMillis());
        booking.setCustomerName(request.customerName());
        booking.setCustomerEmail(request.customerEmail());
        booking.setBikeBrand(request.bikeBrand());
        booking.setBikeModel(request.bikeModel());
        booking.setProblemDescription(request.problemDescription());
        booking.setPreferredWindow(preferredWindow);
        booking.setScheduledAt(selectedSlot.getStart());
        booking.setCreatedAt(OffsetDateTime.now());
        booking.setStatus(ServiceBookingStatus.SCHEDULED);
        booking.setServicePoint(servicePoint);

        ServiceBookingResponse response = toResponse(serviceBookingRepository.save(booking));
        notificationService.sendConfirmation(booking, "Service booking confirmed. Booking number: " + booking.getBookingNumber() + ", scheduled at: " + booking.getScheduledAt());
        return response;
    }

    public ServiceBookingResponse updateStatus(UUID bookingId, UpdateServiceBookingStatusRequest request) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Service booking was not found."));

        booking.setStatus(request.status());
        booking.setPreliminaryPrice(request.preliminaryPrice());
        booking.setEstimatedCompletionAt(request.estimatedCompletionAt());
        booking.setNotes(request.notes());

        ServiceBookingResponse response = toResponse(serviceBookingRepository.save(booking));
        notificationService.sendStatusUpdate(booking, "Service booking status updated to " + request.status() + ". Booking number: " + booking.getBookingNumber());
        return response;
    }

    TimeSlot findFirstAvailableSlot(DateRange preferredWindow) {
        TimeSlot slot = new TimeSlot(preferredWindow.getFrom());
        OffsetDateTime upperBound = preferredWindow.getTo().plusDays(7);

        while (!slot.isAfter(upperBound)) {
            long activeCount = serviceBookingRepository.countActiveBySlot(slot.getStart(), ACTIVE_STATUSES);
            if (!CAPACITY.isFull(activeCount)) {
                return slot;
            }
            slot = slot.next();
        }

        throw new BusinessRuleException("No available service slot was found.");
    }

    private ServiceBookingResponse toResponse(ServiceBooking booking) {
        return new ServiceBookingResponse(
                booking.getId(),
                booking.getBookingNumber(),
                booking.getCustomerName(),
                booking.getCustomerEmail(),
                booking.getBikeBrand(),
                booking.getBikeModel(),
                booking.getProblemDescription(),
                booking.getPreferredWindow().getFrom(),
                booking.getPreferredWindow().getTo(),
                booking.getScheduledAt(),
                booking.getStatus(),
                booking.getPreliminaryPrice(),
                booking.getEstimatedCompletionAt(),
                booking.getServicePoint().getId(),
                booking.getServicePoint().getName(),
                booking.getNotes()
        );
    }
}

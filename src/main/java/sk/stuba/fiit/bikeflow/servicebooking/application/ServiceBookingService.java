package sk.stuba.fiit.bikeflow.servicebooking.application;

import sk.stuba.fiit.bikeflow.common.exception.BusinessRuleException;
import sk.stuba.fiit.bikeflow.common.exception.NotFoundException;
import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import sk.stuba.fiit.bikeflow.facility.repository.FacilityRepository;
import sk.stuba.fiit.bikeflow.servicebooking.api.CreateServiceBookingRequest;
import sk.stuba.fiit.bikeflow.servicebooking.api.ServiceBookingResponse;
import sk.stuba.fiit.bikeflow.servicebooking.api.UpdateServiceBookingStatusRequest;
import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBooking;
import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBookingStatus;
import sk.stuba.fiit.bikeflow.servicebooking.repository.ServiceBookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ServiceBookingService {

    private static final int SLOT_CAPACITY = 2;
    private static final List<ServiceBookingStatus> ACTIVE_STATUSES = List.of(
            ServiceBookingStatus.SCHEDULED,
            ServiceBookingStatus.RECEIVED,
            ServiceBookingStatus.WAITING_FOR_PARTS,
            ServiceBookingStatus.IN_REPAIR
    );

    private final ServiceBookingRepository serviceBookingRepository;
    private final FacilityRepository facilityRepository;

    public ServiceBookingService(
            ServiceBookingRepository serviceBookingRepository,
            FacilityRepository facilityRepository) {
        this.serviceBookingRepository = serviceBookingRepository;
        this.facilityRepository = facilityRepository;
    }

    public List<ServiceBookingResponse> getAll() {
        return serviceBookingRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ServiceBookingResponse create(CreateServiceBookingRequest request) {
        if (!request.preferredTo().isAfter(request.preferredFrom())) {
            throw new BusinessRuleException("Preferred end must be after preferred start.");
        }

        Facility servicePoint = facilityRepository.findById(request.servicePointId())
                .orElseThrow(() -> new NotFoundException("Service point was not found."));

        OffsetDateTime selectedSlot = findFirstAvailableSlot(request.preferredFrom(), request.preferredTo());

        ServiceBooking booking = new ServiceBooking();
        booking.setId(UUID.randomUUID());
        booking.setBookingNumber("SB-" + System.currentTimeMillis());
        booking.setCustomerName(request.customerName());
        booking.setCustomerEmail(request.customerEmail());
        booking.setBikeBrand(request.bikeBrand());
        booking.setBikeModel(request.bikeModel());
        booking.setProblemDescription(request.problemDescription());
        booking.setPreferredFrom(request.preferredFrom());
        booking.setPreferredTo(request.preferredTo());
        booking.setScheduledAt(selectedSlot);
        booking.setCreatedAt(OffsetDateTime.now());
        booking.setStatus(ServiceBookingStatus.SCHEDULED);
        booking.setServicePoint(servicePoint);

        return toResponse(serviceBookingRepository.save(booking));
    }

    public ServiceBookingResponse updateStatus(UUID bookingId, UpdateServiceBookingStatusRequest request) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Service booking was not found."));

        booking.setStatus(request.status());
        booking.setPreliminaryPrice(request.preliminaryPrice());
        booking.setEstimatedCompletionAt(request.estimatedCompletionAt());
        booking.setNotes(request.notes());

        return toResponse(serviceBookingRepository.save(booking));
    }

    OffsetDateTime findFirstAvailableSlot(OffsetDateTime preferredFrom, OffsetDateTime preferredTo) {
        OffsetDateTime slot = preferredFrom.truncatedTo(ChronoUnit.HOURS);
        OffsetDateTime upperBound = preferredTo.plusDays(7).truncatedTo(ChronoUnit.HOURS);

        while (!slot.isAfter(upperBound)) {
            long activeCount = serviceBookingRepository.countActiveBySlot(slot, ACTIVE_STATUSES);
            if (activeCount < SLOT_CAPACITY) {
                return slot;
            }
            slot = slot.plusHours(1);
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
                booking.getPreferredFrom(),
                booking.getPreferredTo(),
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

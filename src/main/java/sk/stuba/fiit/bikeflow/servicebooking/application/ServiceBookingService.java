package sk.stuba.fiit.bikeflow.servicebooking.application;

import sk.stuba.fiit.bikeflow.common.DateRange;
import sk.stuba.fiit.bikeflow.common.NotificationService;
import sk.stuba.fiit.bikeflow.common.exception.BusinessRuleException;
import sk.stuba.fiit.bikeflow.common.exception.NotFoundException;
import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import sk.stuba.fiit.bikeflow.facility.repository.FacilityRepository;
import sk.stuba.fiit.bikeflow.product.domain.Product;
import sk.stuba.fiit.bikeflow.product.repository.ProductRepository;
import sk.stuba.fiit.bikeflow.servicebooking.api.CreateServiceBookingRequest;
import sk.stuba.fiit.bikeflow.servicebooking.api.ProcessServiceRepairRequest;
import sk.stuba.fiit.bikeflow.servicebooking.api.ServiceRequiredPartResponse;
import sk.stuba.fiit.bikeflow.servicebooking.api.ServiceBookingResponse;
import sk.stuba.fiit.bikeflow.servicebooking.api.ServiceWorkItemResponse;
import sk.stuba.fiit.bikeflow.servicebooking.api.UpdateServiceBookingStatusRequest;
import sk.stuba.fiit.bikeflow.servicebooking.domain.*;
import sk.stuba.fiit.bikeflow.servicebooking.repository.ServiceBookingRepository;
import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceCapacity;
import sk.stuba.fiit.bikeflow.servicebooking.domain.TimeSlot;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class ServiceBookingService {

    private static final ServiceCapacity CAPACITY = new ServiceCapacity(2);
    private static final int SLOT_CAPACITY = 2;
    private static final int LOYALTY_COMPLETED_REPAIRS_THRESHOLD = 2;
    private static final int LOYALTY_DISCOUNT_PERCENT = 10;
    private static final int MISSING_PARTS_DELAY_DAYS = 3;
    private static final List<ServiceBookingStatus> ACTIVE_STATUSES = List.of(
            ServiceBookingStatus.SCHEDULED,
            ServiceBookingStatus.RECEIVED,
            ServiceBookingStatus.WAITING_FOR_PARTS,
            ServiceBookingStatus.IN_REPAIR
    );

    private final ServiceBookingRepository serviceBookingRepository;
    private final FacilityRepository facilityRepository;
    private final NotificationService notificationService;
    private final ProductRepository productRepository;

    public ServiceBookingService(
            ServiceBookingRepository serviceBookingRepository,
            FacilityRepository facilityRepository,
            NotificationService notificationService) {
            FacilityRepository facilityRepository,
            ProductRepository productRepository,
        this.serviceBookingRepository = serviceBookingRepository;
        this.facilityRepository = facilityRepository;
        this.notificationService = notificationService;
        this.productRepository = productRepository;
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

    public ServiceBookingResponse processRepair(UUID bookingId, ProcessServiceRepairRequest request) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Service booking was not found."));

        ensureRepairCanBeProcessed(booking);

        OffsetDateTime now = OffsetDateTime.now();
        if (booking.getReceivedAt() == null) {
            booking.setReceivedAt(now);
        }
        booking.setTechnicalState(request.technicalState());
        booking.setAdditionalFindings(blankToNull(request.additionalFindings()));
        booking.getWorkItems().clear();
        booking.getRequiredParts().clear();

        BigDecimal laborTotal = BigDecimal.ZERO;
        for (var payload : request.workItems()) {
            ServiceWorkItem workItem = new ServiceWorkItem();
            workItem.setId(UUID.randomUUID());
            workItem.setServiceBooking(booking);
            workItem.setDescription(payload.description());
            workItem.setLaborPrice(money(payload.laborPrice()));
            booking.getWorkItems().add(workItem);
            laborTotal = laborTotal.add(payload.laborPrice());
        }

        BigDecimal partsTotal = BigDecimal.ZERO;
        List<MissingPart> missingParts = new ArrayList<>();
        for (var payload : request.requiredParts()) {
            Product product = productRepository.findById(payload.productId())
                    .orElseThrow(() -> new NotFoundException("Required part was not found."));

            int availableQuantity = inventoryStockRepository
                    .findByFacilityIdAndProductId(booking.getServicePoint().getId(), product.getId())
                    .map(InventoryStock::getQuantity)
                    .orElse(0);

            int missingQuantity = Math.max(0, payload.requestedQuantity() - availableQuantity);

            ServiceRequiredPart part = new ServiceRequiredPart();
            part.setId(UUID.randomUUID());
            part.setServiceBooking(booking);
            part.setProduct(product);
            part.setRequestedQuantity(payload.requestedQuantity());
            part.setAvailableQuantity(Math.min(availableQuantity, payload.requestedQuantity()));
            part.setUnitPrice(money(payload.unitPrice()));
            part.setAvailabilityStatus(missingQuantity == 0
                    ? ServicePartAvailabilityStatus.AVAILABLE
                    : ServicePartAvailabilityStatus.ORDER_REQUESTED);
            booking.getRequiredParts().add(part);

            partsTotal = partsTotal.add(payload.unitPrice().multiply(BigDecimal.valueOf(payload.requestedQuantity())));
            if (missingQuantity > 0) {
                missingParts.add(new MissingPart(product, missingQuantity));
            }
        }

        int loyaltyDiscountPercent = determineLoyaltyDiscountPercent(booking);
        BigDecimal discountAmount = money(laborTotal
                .multiply(BigDecimal.valueOf(loyaltyDiscountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

        booking.setLoyaltyDiscountPercent(loyaltyDiscountPercent);
        booking.setLoyaltyDiscountAmount(discountAmount);
        booking.setPreliminaryPrice(money(laborTotal.add(partsTotal).subtract(discountAmount)));
        booking.setEstimatedCompletionAt(calculateEstimatedCompletion(booking, request.workItems().size(), missingParts));

        if (!request.clientApproved()) {
            booking.setStatus(ServiceBookingStatus.REJECTED);
            booking.setClientApprovedAt(null);
            booking.setPartsOrderSummary(buildRejectedPartsSummary(missingParts));
            booking.setNotes(appendNote(booking.getNotes(), "Client rejected the calculated price or completion term."));
            return toResponse(serviceBookingRepository.save(booking));
        }

        List<String> dispatchNumbers = createDispatchRequestsForMissingParts(booking, missingParts);
        booking.setPartsOrderSummary(buildPartsOrderSummary(missingParts, dispatchNumbers));
        booking.setClientApprovedAt(now);
        booking.setStatus(missingParts.isEmpty()
                ? ServiceBookingStatus.IN_REPAIR
                : ServiceBookingStatus.WAITING_FOR_PARTS);

        return toResponse(serviceBookingRepository.save(booking));
    }

    public ServiceBookingResponse completeRepair(UUID bookingId) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Service booking was not found."));

        if (booking.getStatus() == ServiceBookingStatus.WAITING_FOR_PARTS) {
            throw new BusinessRuleException("Repair cannot be completed while required parts are still waiting.");
        }
        if (booking.getStatus() != ServiceBookingStatus.IN_REPAIR
                && booking.getStatus() != ServiceBookingStatus.RECEIVED) {
            throw new BusinessRuleException("Service booking cannot be completed from status " + booking.getStatus() + ".");
        }

        OffsetDateTime now = OffsetDateTime.now();
        booking.setStatus(ServiceBookingStatus.DONE);
        booking.setCompletedAt(now);
        booking.setClientNotifiedAt(now);
        booking.setNotes(appendNote(booking.getNotes(), "Client was notified that the repair is complete."));

        return toResponse(serviceBookingRepository.save(booking));
    }

    public ServiceBookingResponse markNoShow(UUID bookingId) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Service booking was not found."));

        if (booking.getStatus() != ServiceBookingStatus.SCHEDULED) {
            throw new BusinessRuleException("Only scheduled service booking can be marked as no-show.");
        }

        booking.setStatus(ServiceBookingStatus.NO_SHOW);
        booking.setNotes(appendNote(booking.getNotes(), "Client did not arrive at the agreed service time."));

        return toResponse(serviceBookingRepository.save(booking));
    }

    public ServiceBookingResponse rejectEstimate(UUID bookingId) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Service booking was not found."));

        ensureRepairCanBeProcessed(booking);
        booking.setStatus(ServiceBookingStatus.REJECTED);
        booking.setClientApprovedAt(null);
        booking.setNotes(appendNote(booking.getNotes(), "Client did not agree with the price or completion term."));

        return toResponse(serviceBookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    public List<ServiceBookingResponse> getServiceHistory(String customerEmail) {
        return serviceBookingRepository.findByCustomerEmailIgnoreCaseOrderByCreatedAtDesc(customerEmail)
                .stream()
                .map(this::toResponse)
                .toList();
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
                booking.getReceivedAt(),
                booking.getTechnicalState(),
                booking.getAdditionalFindings(),
                booking.getClientApprovedAt(),
                booking.getCompletedAt(),
                booking.getClientNotifiedAt(),
                booking.getLoyaltyDiscountPercent(),
                booking.getLoyaltyDiscountAmount(),
                booking.getPartsOrderSummary(),
                booking.getServicePoint().getId(),
                booking.getServicePoint().getName(),
                booking.getNotes(),
                booking.getWorkItems().stream()
                        .map(item -> new ServiceWorkItemResponse(
                                item.getId(),
                                item.getDescription(),
                                item.getLaborPrice()))
                        .toList(),
                booking.getRequiredParts().stream()
                        .map(part -> new ServiceRequiredPartResponse(
                                part.getId(),
                                part.getProduct().getId(),
                                part.getProduct().getName(),
                                part.getRequestedQuantity(),
                                part.getAvailableQuantity(),
                                part.getUnitPrice(),
                                part.getAvailabilityStatus()))
                        .toList()
        );
    }

    private void ensureRepairCanBeProcessed(ServiceBooking booking) {
        if (booking.getStatus() == ServiceBookingStatus.DONE
                || booking.getStatus() == ServiceBookingStatus.CANCELLED
                || booking.getStatus() == ServiceBookingStatus.REJECTED
                || booking.getStatus() == ServiceBookingStatus.NO_SHOW) {
            throw new BusinessRuleException("Service booking cannot be processed from status " + booking.getStatus() + ".");
        }
    }

    private int determineLoyaltyDiscountPercent(ServiceBooking booking) {
        long completedRepairs = serviceBookingRepository.countByCustomerEmailIgnoreCaseAndStatus(
                booking.getCustomerEmail(),
                ServiceBookingStatus.DONE);
        return completedRepairs >= LOYALTY_COMPLETED_REPAIRS_THRESHOLD ? LOYALTY_DISCOUNT_PERCENT : 0;
    }

    private OffsetDateTime calculateEstimatedCompletion(
            ServiceBooking booking,
            int workItemCount,
            List<MissingPart> missingParts) {

        OffsetDateTime start = booking.getReceivedAt() == null ? OffsetDateTime.now() : booking.getReceivedAt();
        if (booking.getScheduledAt() != null && booking.getScheduledAt().isAfter(start)) {
            start = booking.getScheduledAt();
        }

        int workDays = Math.max(1, workItemCount);
        int partDelayDays = missingParts.isEmpty() ? 0 : MISSING_PARTS_DELAY_DAYS;

        return start.plusDays(workDays + partDelayDays).truncatedTo(ChronoUnit.HOURS);
    }

    private String buildPartsOrderSummary(List<MissingPart> missingParts, List<String> dispatchNumbers) {
        if (missingParts.isEmpty()) {
            return "All required parts are available.";
        }
        if (dispatchNumbers.isEmpty()) {
            return "Some required parts are missing and no internal source has enough stock. External purchase is required.";
        }
        return "Missing parts requested through dispatch: " + String.join(", ", dispatchNumbers) + ".";
    }

    private String buildRejectedPartsSummary(List<MissingPart> missingParts) {
        if (missingParts.isEmpty()) {
            return "All required parts were available, but the repair estimate was rejected.";
        }
        return "Missing parts were identified, but no dispatch was created because the repair estimate was rejected.";
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private String appendNote(String currentNotes, String addition) {
        if (currentNotes == null || currentNotes.isBlank()) {
            return addition;
        }
        return currentNotes + "\n" + addition;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private record MissingPart(Product product, int quantity) {
    }
}
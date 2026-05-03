package sk.stuba.fiit.bikeflow.sparepart.application;

import sk.stuba.fiit.bikeflow.common.NotificationService;
import sk.stuba.fiit.bikeflow.common.exception.NotFoundException;
import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBooking;
import sk.stuba.fiit.bikeflow.servicebooking.domain.ServiceBookingStatus;
import sk.stuba.fiit.bikeflow.servicebooking.repository.ServiceBookingRepository;
import sk.stuba.fiit.bikeflow.sparepart.api.AddSparePartRequest;
import sk.stuba.fiit.bikeflow.sparepart.api.AddSparePartResponse;
import sk.stuba.fiit.bikeflow.sparepart.api.SparePartResponse;
import sk.stuba.fiit.bikeflow.sparepart.domain.OrderSparePart;
import sk.stuba.fiit.bikeflow.sparepart.domain.PartOrder;
import sk.stuba.fiit.bikeflow.sparepart.domain.PartOrderStatus;
import sk.stuba.fiit.bikeflow.sparepart.domain.SparePart;
import sk.stuba.fiit.bikeflow.sparepart.repository.OrderSparePartRepository;
import sk.stuba.fiit.bikeflow.sparepart.repository.PartOrderRepository;
import sk.stuba.fiit.bikeflow.sparepart.repository.SparePartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SparePartService {

    private final SparePartRepository sparePartRepository;
    private final OrderSparePartRepository orderSparePartRepository;
    private final PartOrderRepository partOrderRepository;
    private final ServiceBookingRepository serviceBookingRepository;
    private final NotificationService notificationService;

    public SparePartService(
            SparePartRepository sparePartRepository,
            OrderSparePartRepository orderSparePartRepository,
            PartOrderRepository partOrderRepository,
            ServiceBookingRepository serviceBookingRepository,
            NotificationService notificationService) {
        this.sparePartRepository = sparePartRepository;
        this.orderSparePartRepository = orderSparePartRepository;
        this.partOrderRepository = partOrderRepository;
        this.serviceBookingRepository = serviceBookingRepository;
        this.notificationService = notificationService;
    }

    public List<SparePartResponse> getAll() {
        return sparePartRepository.findAll()
                .stream()
                .map(p -> new SparePartResponse(p.getId(), p.getSku(), p.getName(), p.getStockQuantity()))
                .toList();
    }

    public AddSparePartResponse addToBooking(UUID bookingId, AddSparePartRequest request) {
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Service booking was not found."));

        SparePart part = sparePartRepository.findById(request.sparePartId())
                .orElseThrow(() -> new NotFoundException("Spare part was not found."));

        OrderSparePart line = new OrderSparePart();
        line.setId(UUID.randomUUID());
        line.setServiceBooking(booking);
        line.setSparePart(part);
        line.setQuantity(request.quantity());
        orderSparePartRepository.save(line);

        boolean stockSufficient = part.getStockQuantity() >= request.quantity();
        PartOrder partOrder = null;

        if (stockSufficient) {
            part.setStockQuantity(part.getStockQuantity() - request.quantity());
            sparePartRepository.save(part);
            notificationService.sendStatusUpdate(booking,
                    "Spare part '" + part.getName() + "' (qty: " + request.quantity() + ") reserved from stock for booking " + booking.getBookingNumber());
        } else {
            partOrder = new PartOrder();
            partOrder.setId(UUID.randomUUID());
            partOrder.setSparePart(part);
            partOrder.setServiceBooking(booking);
            partOrder.setOrderedQuantity(request.quantity());
            partOrder.setEstimatedDelivery(request.estimatedDelivery());
            partOrder.setStatus(PartOrderStatus.ORDERED);
            partOrder.setCreatedAt(OffsetDateTime.now());
            partOrderRepository.save(partOrder);

            booking.setStatus(ServiceBookingStatus.WAITING_FOR_PARTS);
            serviceBookingRepository.save(booking);

            notificationService.sendStatusUpdate(booking,
                    "Spare part '" + part.getName() + "' out of stock — part order created (delivery: " + request.estimatedDelivery() + "). Booking " + booking.getBookingNumber() + " set to WAITING_FOR_PARTS.");
        }

        return new AddSparePartResponse(
                line.getId(),
                part.getId(),
                part.getName(),
                request.quantity(),
                partOrder != null,
                partOrder != null ? partOrder.getId() : null,
                partOrder != null ? partOrder.getEstimatedDelivery() : null,
                partOrder != null ? partOrder.getStatus() : null
        );
    }
}

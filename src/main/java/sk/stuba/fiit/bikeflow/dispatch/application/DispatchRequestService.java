package sk.stuba.fiit.bikeflow.dispatch.application;

import sk.stuba.fiit.bikeflow.common.exception.BusinessRuleException;
import sk.stuba.fiit.bikeflow.common.exception.NotFoundException;
import sk.stuba.fiit.bikeflow.dispatch.api.CreateDispatchRequest;
import sk.stuba.fiit.bikeflow.dispatch.api.DispatchRequestItemPayload;
import sk.stuba.fiit.bikeflow.dispatch.api.DispatchRequestResponse;
import sk.stuba.fiit.bikeflow.dispatch.domain.DispatchRequest;
import sk.stuba.fiit.bikeflow.dispatch.domain.DispatchRequestItem;
import sk.stuba.fiit.bikeflow.dispatch.domain.DispatchStatus;
import sk.stuba.fiit.bikeflow.dispatch.repository.DispatchRequestRepository;
import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import sk.stuba.fiit.bikeflow.facility.repository.FacilityRepository;
import sk.stuba.fiit.bikeflow.inventory.domain.InventoryStock;
import sk.stuba.fiit.bikeflow.inventory.repository.InventoryStockRepository;
import sk.stuba.fiit.bikeflow.product.domain.Product;
import sk.stuba.fiit.bikeflow.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DispatchRequestService {

    private final DispatchRequestRepository dispatchRequestRepository;
    private final FacilityRepository facilityRepository;
    private final ProductRepository productRepository;
    private final InventoryStockRepository inventoryStockRepository;

    public DispatchRequestService(
            DispatchRequestRepository dispatchRequestRepository,
            FacilityRepository facilityRepository,
            ProductRepository productRepository,
            InventoryStockRepository inventoryStockRepository) {
        this.dispatchRequestRepository = dispatchRequestRepository;
        this.facilityRepository = facilityRepository;
        this.productRepository = productRepository;
        this.inventoryStockRepository = inventoryStockRepository;
    }

    public List<DispatchRequestResponse> getAll() {
        return dispatchRequestRepository.findAll().stream().map(this::toResponse).toList();
    }

    public DispatchRequestResponse create(CreateDispatchRequest request) {
        if (request.sourceFacilityId().equals(request.targetFacilityId())) {
            throw new BusinessRuleException("Source facility must be different from target facility.");
        }

        Facility source = facilityRepository.findById(request.sourceFacilityId())
                .orElseThrow(() -> new NotFoundException("Source facility was not found."));
        Facility target = facilityRepository.findById(request.targetFacilityId())
                .orElseThrow(() -> new NotFoundException("Target facility was not found."));

        DispatchRequest dispatchRequest = new DispatchRequest();
        dispatchRequest.setId(UUID.randomUUID());
        dispatchRequest.setRequestNumber("DR-" + System.currentTimeMillis());
        dispatchRequest.setSourceFacility(source);
        dispatchRequest.setTargetFacility(target);
        dispatchRequest.setPriority(request.priority());
        dispatchRequest.setStatus(DispatchStatus.CREATED);
        dispatchRequest.setCreatedAt(OffsetDateTime.now());
        dispatchRequest.setNotes(request.notes());

        for (DispatchRequestItemPayload payload : request.items()) {
            Product product = productRepository.findById(payload.productId())
                    .orElseThrow(() -> new NotFoundException("Product was not found."));

            InventoryStock stock = inventoryStockRepository.findByFacilityIdAndProductId(source.getId(), product.getId())
                    .orElseThrow(() -> new BusinessRuleException("Selected product is not stocked in the source facility."));

            if (stock.getQuantity() < payload.requestedQuantity()) {
                throw new BusinessRuleException(
                        "Requested quantity for product '" + product.getName() + "' exceeds available stock (" + stock.getQuantity() + ").");
            }

            DispatchRequestItem item = new DispatchRequestItem();
            item.setId(UUID.randomUUID());
            item.setDispatchRequest(dispatchRequest);
            item.setProduct(product);
            item.setRequestedQuantity(payload.requestedQuantity());
            dispatchRequest.getItems().add(item);
        }

        return toResponse(dispatchRequestRepository.save(dispatchRequest));
    }

    private DispatchRequestResponse toResponse(DispatchRequest entity) {
        return new DispatchRequestResponse(
                entity.getId(),
                entity.getRequestNumber(),
                entity.getSourceFacility().getId(),
                entity.getSourceFacility().getName(),
                entity.getTargetFacility().getId(),
                entity.getTargetFacility().getName(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getNotes(),
                entity.getItems().stream()
                        .map(item -> new DispatchRequestResponse.DispatchRequestResponseItem(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getRequestedQuantity()))
                        .toList()
        );
    }
}

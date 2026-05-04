package sk.stuba.fiit.bikeflow.dispatch.application;

import sk.stuba.fiit.bikeflow.common.exception.BusinessRuleException;
import sk.stuba.fiit.bikeflow.common.exception.NotFoundException;
import sk.stuba.fiit.bikeflow.dispatch.api.CreateExpeditionRequest;
import sk.stuba.fiit.bikeflow.dispatch.api.ExpeditionRequestItemPayload;
import sk.stuba.fiit.bikeflow.dispatch.api.ExpeditionRequestResponse;
import sk.stuba.fiit.bikeflow.dispatch.domain.ExpeditionRequest;
import sk.stuba.fiit.bikeflow.dispatch.domain.ExpeditionRequestItem;
import sk.stuba.fiit.bikeflow.dispatch.domain.RequestStatus;
import sk.stuba.fiit.bikeflow.dispatch.domain.WarehouseStaff;
import sk.stuba.fiit.bikeflow.dispatch.repository.ExpeditionRequestRepository;
import sk.stuba.fiit.bikeflow.dispatch.repository.WarehouseStaffRepository;
import sk.stuba.fiit.bikeflow.facility.domain.Facility;
import sk.stuba.fiit.bikeflow.facility.repository.FacilityRepository;
import sk.stuba.fiit.bikeflow.inventory.domain.StockItem;
import sk.stuba.fiit.bikeflow.inventory.repository.StockItemRepository;
import sk.stuba.fiit.bikeflow.product.domain.Product;
import sk.stuba.fiit.bikeflow.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ExpeditionRequestService {

    private final ExpeditionRequestRepository expeditionRequestRepository;
    private final FacilityRepository facilityRepository;
    private final ProductRepository productRepository;
    private final StockItemRepository stockItemRepository;
    private final WarehouseStaffRepository warehouseStaffRepository;

    public ExpeditionRequestService(
            ExpeditionRequestRepository expeditionRequestRepository,
            FacilityRepository facilityRepository,
            ProductRepository productRepository,
            StockItemRepository stockItemRepository,
            WarehouseStaffRepository warehouseStaffRepository) {
        this.expeditionRequestRepository = expeditionRequestRepository;
        this.facilityRepository = facilityRepository;
        this.productRepository = productRepository;
        this.stockItemRepository = stockItemRepository;
        this.warehouseStaffRepository = warehouseStaffRepository;
    }

    public List<ExpeditionRequestResponse> getAll() {
        return expeditionRequestRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ExpeditionRequestResponse create(CreateExpeditionRequest request) {
        if (request.sourceFacilityId().equals(request.targetFacilityId())) {
            throw new BusinessRuleException("Source facility must be different from target facility.");
        }

        Facility source = facilityRepository.findById(request.sourceFacilityId())
                .orElseThrow(() -> new NotFoundException("Source facility was not found."));
        Facility target = facilityRepository.findById(request.targetFacilityId())
                .orElseThrow(() -> new NotFoundException("Target facility was not found."));

        WarehouseStaff requestedBy = null;
        if (request.requestedById() != null) {
            requestedBy = warehouseStaffRepository.findById(request.requestedById())
                    .orElseThrow(() -> new NotFoundException("Warehouse staff member was not found."));
        }

        ExpeditionRequest expeditionRequest = new ExpeditionRequest();
        expeditionRequest.setId(UUID.randomUUID());
        expeditionRequest.setRequestNumber("EXP-" + System.currentTimeMillis());
        expeditionRequest.setSourceFacility(source);
        expeditionRequest.setTargetFacility(target);
        expeditionRequest.setPriority(request.priority());
        expeditionRequest.setStatus(RequestStatus.CREATED);
        expeditionRequest.setCreatedAt(OffsetDateTime.now());
        expeditionRequest.setNotes(request.notes());
        expeditionRequest.setRequestedBy(requestedBy);

        for (ExpeditionRequestItemPayload payload : request.items()) {
            Product product = productRepository.findById(payload.productId())
                    .orElseThrow(() -> new NotFoundException("Product was not found."));

            StockItem stock = stockItemRepository.findByFacilityIdAndProductId(source.getId(), product.getId())
                    .orElseThrow(() -> new BusinessRuleException("Selected product is not stocked in the source facility."));

            if (stock.getQuantity() < payload.requestedQuantity()) {
                throw new BusinessRuleException(
                        "Requested quantity for product '" + product.getName() + "' exceeds available stock (" + stock.getQuantity() + ").");
            }

            ExpeditionRequestItem item = new ExpeditionRequestItem();
            item.setId(UUID.randomUUID());
            item.setExpeditionRequest(expeditionRequest);
            item.setProduct(product);
            item.setRequestedQuantity(payload.requestedQuantity());
            expeditionRequest.getItems().add(item);
        }

        return toResponse(expeditionRequestRepository.save(expeditionRequest));
    }

    private ExpeditionRequestResponse toResponse(ExpeditionRequest entity) {
        return new ExpeditionRequestResponse(
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
                entity.getRequestedBy() != null ? entity.getRequestedBy().getId() : null,
                entity.getItems().stream()
                        .map(item -> new ExpeditionRequestResponse.ExpeditionRequestResponseItem(
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getRequestedQuantity()))
                        .toList()
        );
    }
}

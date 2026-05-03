package sk.stuba.fiit.bikeflow.inventory.api;

import sk.stuba.fiit.bikeflow.inventory.repository.InventoryStockRepository;
import sk.stuba.fiit.bikeflow.inventory.repository.SalesHistoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryStockRepository inventoryStockRepository;
    private final SalesHistoryRepository salesHistoryRepository;

    public InventoryController(
            InventoryStockRepository inventoryStockRepository,
            SalesHistoryRepository salesHistoryRepository) {
        this.inventoryStockRepository = inventoryStockRepository;
        this.salesHistoryRepository = salesHistoryRepository;
    }

    @GetMapping("/overview")
    public List<InventoryOverviewResponse> getOverview(@RequestParam UUID facilityId) {
        return inventoryStockRepository.findOverviewByFacilityId(facilityId)
                .stream()
                .map(stock -> new InventoryOverviewResponse(
                        stock.getProduct().getId(),
                        stock.getProduct().getName(),
                        stock.getProduct().getSku(),
                        stock.getQuantity()))
                .toList();
    }

    @GetMapping("/sales-analysis")
    public List<SalesAnalysisResponse> getSalesAnalysis(
            @RequestParam UUID facilityId,
            @RequestParam(defaultValue = "30") int days) {

        LocalDate toDate = LocalDate.now();
        LocalDate fromDate = toDate.minusDays(days);

        LinkedHashMap<UUID, SalesAnalysisResponse> aggregated = new LinkedHashMap<>();
        salesHistoryRepository.findByFacilityAndPeriod(facilityId, fromDate, toDate)
                .forEach(item -> aggregated.compute(
                        item.getProduct().getId(),
                        (id, existing) -> existing == null
                                ? new SalesAnalysisResponse(id, item.getProduct().getName(), item.getQuantitySold())
                                : new SalesAnalysisResponse(id, existing.productName(), existing.totalQuantitySold() + item.getQuantitySold())
                ));

        return aggregated.values().stream().toList();
    }
}

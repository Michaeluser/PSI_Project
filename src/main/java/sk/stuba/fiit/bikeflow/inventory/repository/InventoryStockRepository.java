package sk.stuba.fiit.bikeflow.inventory.repository;

import sk.stuba.fiit.bikeflow.inventory.domain.InventoryStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryStockRepository extends JpaRepository<InventoryStock, UUID> {

    @Query("""
            select s
            from InventoryStock s
            join fetch s.facility f
            join fetch s.product p
            where f.id = :facilityId
            order by p.name asc
            """)
    List<InventoryStock> findOverviewByFacilityId(UUID facilityId);

    Optional<InventoryStock> findByFacilityIdAndProductId(UUID facilityId, UUID productId);

    @Query("""
            select s
            from InventoryStock s
            join fetch s.facility f
            join fetch s.product p
            where p.id = :productId
              and f.id <> :targetFacilityId
              and s.quantity >= :quantity
            order by s.quantity desc
            """)
    List<InventoryStock> findAvailableSources(UUID productId, UUID targetFacilityId, int quantity);
}

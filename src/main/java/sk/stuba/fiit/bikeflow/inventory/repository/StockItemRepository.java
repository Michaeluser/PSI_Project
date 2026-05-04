package sk.stuba.fiit.bikeflow.inventory.repository;

import sk.stuba.fiit.bikeflow.inventory.domain.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockItemRepository extends JpaRepository<StockItem, UUID> {

    @Query("""
            select s
            from StockItem s
            join fetch s.facility f
            join fetch s.product p
            where f.id = :facilityId
            order by p.name asc
            """)
    List<StockItem> findOverviewByFacilityId(UUID facilityId);

    Optional<StockItem> findByFacilityIdAndProductId(UUID facilityId, UUID productId);

    @Query("""
            select s
            from StockItem s
            join fetch s.facility f
            join fetch s.product p
            where p.id = :productId
              and f.id <> :targetFacilityId
              and s.quantity >= :quantity
            order by s.quantity desc
            """)
    List<StockItem> findAvailableSources(UUID productId, UUID targetFacilityId, int quantity);
}